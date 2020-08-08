package com.noqapp.search.elastic.service;

import static com.noqapp.search.elastic.service.BizStoreElasticService.excludeFields;
import static com.noqapp.search.elastic.service.BizStoreElasticService.includeFields;
import static com.noqapp.search.elastic.service.BizStoreElasticService.populateSearchData;
import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreElasticList;
import com.noqapp.search.elastic.domain.BizStoreSpatialElastic;
import com.noqapp.search.elastic.repository.BizStoreSpatialElasticManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
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
    private static final long SECONDS = 10L;

    private BizStoreSpatialElasticManager<BizStoreElastic> bizStoreSpatialElasticManager;
    private ElasticsearchClientConfiguration elasticsearchClientConfiguration;

    @Autowired
    public BizStoreSpatialElasticService(
        BizStoreSpatialElasticManager<BizStoreElastic> bizStoreSpatialElasticManager,
        ElasticsearchClientConfiguration elasticsearchClientConfiguration
    ) {
        this.bizStoreSpatialElasticManager = bizStoreSpatialElasticManager;
        this.elasticsearchClientConfiguration = elasticsearchClientConfiguration;
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
    public BizStoreElasticList filteredSearch(BusinessTypeEnum filterMustNotBusinessType, String geoHash, String scrollId) {
        return searchByBusinessType(null, filterMustNotBusinessType, geoHash, scrollId);
    }

    @Mobile
    public BizStoreElasticList searchByBusinessType(
        BusinessTypeEnum filterMustBusinessType,
        BusinessTypeEnum filterMustNotBusinessType,
        String geoHash,
        String scrollId
    ) {
        BizStoreElasticList bizStoreElastics;
        if (null == filterMustBusinessType) {
            bizStoreElastics = executeSearchExcludingBusinessType(filterMustNotBusinessType, geoHash, scrollId);
        } else {
            switch (filterMustBusinessType) {
                case DO:
                    bizStoreElastics = executeFilterBySearchOnBizStoreUsingRestClient(
                        filterMustBusinessType,
                        filterMustNotBusinessType,
                        geoHash,
                        scrollId);
                    break;
                default:
                    bizStoreElastics = executeSearchExcludingBusinessType(filterMustNotBusinessType, geoHash, scrollId);
            }
        }
        return bizStoreElastics;
    }

    @Mobile
    public BizStoreElasticList executeSearchExcludingBusinessType(
        BusinessTypeEnum filterMustNotBusinessType,
        String geoHash,
        String scrollId
    ) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(SECONDS));
                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreSpatialElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                /* Search All Query. */
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.matchAllQuery());
                if (null != filterMustNotBusinessType) {
                    boolQueryBuilder.mustNot(QueryBuilders.matchQuery("BT", filterMustNotBusinessType.getName()));
                }
                boolQueryBuilder.filter(geoDistanceQuery("GH")
                        .geohash(geoHash)
                        .distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
                searchSourceBuilder.query(boolQueryBuilder);

                searchSourceBuilder.size(PaginationEnum.THIRTY.getLimit());
                searchRequest.source(searchSourceBuilder);
                searchRequest.scroll(TimeValue.timeValueSeconds(SECONDS));

                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
            }

            bizStoreElastics.setScrollId(searchResponse.getScrollId());
            populateSearchData(bizStoreElastics, searchResponse.getHits().getHits());
            return bizStoreElastics;
        } catch (IOException e) {
            LOG.error("Failed getting data reason={}", e.getLocalizedMessage(), e);
            return bizStoreElastics;
        }
    }

    @Mobile
    public BizStoreElasticList executeFilterBySearchOnBizStoreUsingRestClient(
        BusinessTypeEnum filterMustBusinessType,
        BusinessTypeEnum filterMustNotBusinessType,
        String geoHash,
        String scrollId
    ) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(SECONDS));
                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreSpatialElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                /* Search just DO Query. */
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.matchQuery("BT", filterMustBusinessType.getName()));
                if (null != filterMustNotBusinessType) {
                    boolQueryBuilder.mustNot(QueryBuilders.matchQuery("BT", filterMustNotBusinessType.getName()));
                }
                boolQueryBuilder.filter(geoDistanceQuery("GH")
                        .geohash(geoHash)
                        .distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
                searchSourceBuilder.query(boolQueryBuilder);

                searchSourceBuilder.size(PaginationEnum.THIRTY.getLimit());
                searchRequest.source(searchSourceBuilder);
                searchRequest.scroll(TimeValue.timeValueSeconds(SECONDS));

                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
            }

            bizStoreElastics.setScrollId(searchResponse.getScrollId());
            populateSearchData(bizStoreElastics, searchResponse.getHits().getHits());
            return bizStoreElastics;
        } catch (IOException e) {
            LOG.error("Failed getting data reason={}", e.getLocalizedMessage(), e);
            return bizStoreElastics;
        }
    }

    @Mobile
    public BizStoreElasticList nearMeByBusinessTypes(
        List<BusinessTypeEnum> filterOnBusinessTypes,
        String geoHash,
        String scrollId
    ) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(SECONDS));
                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreSpatialElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                /* Search just DO Query. */
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                for (BusinessTypeEnum businessType : filterOnBusinessTypes) {
                    boolQueryBuilder.should(QueryBuilders.matchQuery("BT", businessType));
                }
                boolQueryBuilder.filter(geoDistanceQuery("GH")
                    .geohash(geoHash)
                    .distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
                searchSourceBuilder.query(boolQueryBuilder);

                searchSourceBuilder.size(PaginationEnum.THIRTY.getLimit());
                searchRequest.source(searchSourceBuilder);
                searchRequest.scroll(TimeValue.timeValueSeconds(SECONDS));

                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
            }

            bizStoreElastics.setScrollId(searchResponse.getScrollId());
            populateSearchData(bizStoreElastics, searchResponse.getHits().getHits());
            return bizStoreElastics;
        } catch (IOException e) {
            LOG.error("Failed getting data reason={}", e.getLocalizedMessage(), e);
            return bizStoreElastics;
        }
    }
}
