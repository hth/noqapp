package com.noqapp.search.elastic.service;

import static com.noqapp.search.elastic.service.BizStoreElasticService.excludeFields;
import static com.noqapp.search.elastic.service.BizStoreElasticService.includeFields;
import static com.noqapp.search.elastic.service.BizStoreElasticService.populateSearchData;
import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreElasticList;
import com.noqapp.search.elastic.domain.BizStoreSpatialElastic;
import com.noqapp.search.elastic.repository.BizStoreSpatialElasticManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 11/27/19 7:02 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class BizStoreSpatialElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreSpatialElasticService.class);
    static final long SECONDS_FOR_NEAR_ME = 10L;

    private BizStoreSpatialElasticManager<BizStoreElastic> bizStoreSpatialElasticManager;
    private RestHighLevelClient restHighLevelClient;

    private int paginationSize;

    @Autowired
    public BizStoreSpatialElasticService(
        @Value("${BizStoreSpatial.paginationSize}")
        int paginationSize,

        BizStoreSpatialElasticManager<BizStoreElastic> bizStoreSpatialElasticManager,
        RestHighLevelClient restHighLevelClient
    ) {
        this.paginationSize = paginationSize;

        this.bizStoreSpatialElasticManager = bizStoreSpatialElasticManager;
        this.restHighLevelClient = restHighLevelClient;
    }

    @Async
    public void save(BizStoreElastic bizStoreElastic) {
        bizStoreSpatialElasticManager.save(bizStoreElastic);
    }

    @Async
    void save(Set<BizStoreElastic> bizStoreElastics) {
        LOG.info("Bulk save size={}", bizStoreElastics.size());
        if (!bizStoreElastics.isEmpty()) {
            bizStoreSpatialElasticManager.save(bizStoreElastics);
        }
    }

    @Async
    public void delete(String id) {
        LOG.info("Deleting store from elastic id={}", id);
        bizStoreSpatialElasticManager.delete(id);
    }

    @Mobile
    public BizStoreElasticList nearMeExcludedBusinessTypes(String geoHash, String scrollId, int from) {
        return nearMeExcludedBusinessTypes(new ArrayList<>(), new ArrayList<>(), BusinessTypeEnum.ZZ, geoHash, scrollId, from);
    }

    @Mobile
    public BizStoreElasticList nearMeExcludedBusinessTypes(List<BusinessTypeEnum> filterMustNotBusinessTypes, List<BusinessTypeEnum> filteringFor, String geoHash, String scrollId, int from) {
        return nearMeExcludedBusinessTypes(filterMustNotBusinessTypes, filteringFor, BusinessTypeEnum.ZZ, geoHash, scrollId, from);
    }

    @Mobile
    public BizStoreElasticList nearMeExcludedBusinessTypes(
        List<BusinessTypeEnum> filterMustNotBusinessTypes,
        List<BusinessTypeEnum> filteringFor,
        BusinessTypeEnum searchedOnBusinessType,
        String geoHash,
        String scrollId,
        int from
    ) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(SECONDS_FOR_NEAR_ME));
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreSpatialElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.matchAllQuery());
                for (BusinessTypeEnum businessType : filterMustNotBusinessTypes) {
                    boolQueryBuilder.mustNot(QueryBuilders.matchQuery("BT", businessType.name()));
                }

                String distance;
                if (filteringFor.contains(BusinessTypeEnum.CD) || filteringFor.contains(BusinessTypeEnum.CDQ)) {
                    distance = Constants.MAX_Q_SEARCH_DISTANCE;
                } else if (filteringFor.contains(BusinessTypeEnum.HS) || filteringFor.contains(BusinessTypeEnum.DO)) {
                    distance = Constants.MAX_Q_SEARCH_DISTANCE;
                } else {
                    distance = "200";
                }

                boolQueryBuilder.filter(geoDistanceQuery("GH")
                    .geohash(geoHash)
                    .distance(distance, DistanceUnit.KILOMETERS));
                searchSourceBuilder.query(boolQueryBuilder);

                searchSourceBuilder.size(paginationSize);
                searchRequest.source(searchSourceBuilder);
                if (from > 0) {
                    searchSourceBuilder.from(from);
                } else {
                    searchRequest.scroll(TimeValue.timeValueSeconds(SECONDS_FOR_NEAR_ME));
                }

                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            }

            populateSearchData(bizStoreElastics, searchResponse.getHits().getHits());
            return bizStoreElastics.setScrollId(searchResponse.getScrollId())
                .setFrom(from + paginationSize)
                .setSize(paginationSize)
                .setSearchedOnBusinessType(searchedOnBusinessType);
        } catch (IOException e) {
            LOG.error("Failed getting data reason={}", e.getLocalizedMessage(), e);
            return bizStoreElastics;
        }
    }

    @Mobile
    public BizStoreElasticList nearMeByBusinessTypes(
        List<BusinessTypeEnum> filterOnBusinessTypes,
        String geoHash,
        String scrollId,
        int from
    ) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(SECONDS_FOR_NEAR_ME));
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreSpatialElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                for (BusinessTypeEnum businessType : filterOnBusinessTypes) {
                    boolQueryBuilder.should(QueryBuilders.termQuery("BT", businessType.name()));
                }
                boolQueryBuilder.filter(geoDistanceQuery("GH")
                    .geohash(geoHash)
                    .distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
                searchSourceBuilder.query(boolQueryBuilder);

                searchSourceBuilder.size(paginationSize);
                searchRequest.source(searchSourceBuilder);
                if (from > 0) {
                    searchSourceBuilder.from(from);
                } else {
                    searchRequest.scroll(TimeValue.timeValueSeconds(SECONDS_FOR_NEAR_ME));
                }

                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            }

            populateSearchData(bizStoreElastics, searchResponse.getHits().getHits());
            return bizStoreElastics.setScrollId(searchResponse.getScrollId())
                .setFrom(from + paginationSize)
                .setSize(paginationSize);
        } catch (IOException e) {
            LOG.error("Failed getting data reason={}", e.getLocalizedMessage(), e);
            return bizStoreElastics;
        }
    }
}
