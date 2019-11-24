package com.noqapp.search.elastic.service;

import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreElasticList;
import com.noqapp.search.elastic.domain.StoreHourElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.repository.BizStoreElasticManager;

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
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/14/17 2:55 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class BizStoreElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreElasticService.class);
    private static final long MINUTES = 10L;

    private static String[] includeFields = new String[]{
        "N", "BT", "BC", "BCI", "BID", "AD", "AR", "TO", "DT", "SH",
        "ST", "SS", "CC", "CS", "PH", "PI", "RA", "RC", "DN", "QR",
        "GH", "WL", "FF", "DI"};
    private static String[] excludeFields = new String[]{"_type"};

    private BizStoreElasticManager<BizStoreElastic> bizStoreElasticManager;
    private ElasticAdministrationService elasticAdministrationService;
    private ElasticsearchClientConfiguration elasticsearchClientConfiguration;
    private BizStoreManager bizStoreManager;
    private StoreHourManager storeHourManager;
    private ApiHealthService apiHealthService;

    @Autowired
    public BizStoreElasticService(
        BizStoreElasticManager<BizStoreElastic> bizStoreElasticManager,
        ElasticAdministrationService elasticAdministrationService,
        ElasticsearchClientConfiguration elasticsearchClientConfiguration,
        BizStoreManager bizStoreManager,
        StoreHourManager storeHourManager,
        ApiHealthService apiHealthService
    ) {
        this.bizStoreElasticManager = bizStoreElasticManager;
        this.elasticAdministrationService = elasticAdministrationService;
        this.elasticsearchClientConfiguration = elasticsearchClientConfiguration;
        this.bizStoreManager = bizStoreManager;
        this.storeHourManager = storeHourManager;
        this.apiHealthService = apiHealthService;
    }

    @Async
    public void save(BizStoreElastic bizStoreElastic) {
        bizStoreElasticManager.save(bizStoreElastic);
    }

    @Async
    void save(List<BizStoreElastic> bizStoreElastics) {
        LOG.info("Bulk save size={}", bizStoreElastics.size());
        if (!bizStoreElastics.isEmpty()) {
            bizStoreElasticManager.save(bizStoreElastics);
        }
    }

    @Async
    public void delete(String id) {
        LOG.info("Deleting store from elastic id={}", id);
        bizStoreElasticManager.delete(id);
    }

    /**
     * Helps add store when noqapp_biz_store index is missing.
     * Should be called when initializing index for first time.
     */
    public void addAllBizStoreToElastic() {
        Instant start = Instant.now();
        long count = 0;
        try (Stream<BizStoreEntity> stream = bizStoreManager.findAllWithStream()) {
            List<BizStoreElastic> bizStoreElastics = new ArrayList<>();
            stream.iterator().forEachRemaining(bizStore -> {
                BizStoreElastic bizStoreElastic = null;
                try {
                    bizStoreElastic = DomainConversion.getAsBizStoreElastic(
                        bizStore,
                        storeHourManager.findAll(bizStore.getId()));

                    bizStoreElastics.add(bizStoreElastic);
                } catch (Exception e) {
                    LOG.error("Failed to insert in elastic data={} reason={}",
                        bizStoreElastic,
                        e.getLocalizedMessage(),
                        e);
                }
            });
            save(bizStoreElastics);
            count += bizStoreElastics.size();
        }

        apiHealthService.insert(
            "/addAllBizStoreToElastic",
            "addAllBizStoreToElastic",
            this.getClass().getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        LOG.info("Added total={} BizStore to Elastic in duration={}", count, Duration.between(start, Instant.now()).toMillis());
    }

    /**
     * Search using RestHighLevelClient. DSL Query free search.
     * //TODO(hth) Check not working search.
     */
    public List<BizStoreElastic> searchByBusinessName(String businessName) {
        LOG.info("Searching for {}", businessName);
        return bizStoreElasticManager.searchByBusinessName(businessName, PaginationEnum.TEN.getLimit());
    }

    /**
     * Performs search on the index with provided DSL.
     */
    private String executeSearchOnBizStoreUsingDSL(String dslQuery) {
        LOG.info("DSL dslQuery={}", dslQuery);
        String result = elasticAdministrationService.executeDSLQuerySearch(
            BizStoreElastic.INDEX
                + "/"
                + BizStoreElastic.TYPE
                + "/_search?pretty=true",
            dslQuery
        );

        LOG.info("DSL Query result={}", result);
        return result;
    }

    @Mobile
    public BizStoreElasticList executeNearMeSearchOnBizStoreUsingRestClient(
        String query,
        String cityName,
        String geoHash,
        String filters,
        String scrollId
    ) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueMinutes(MINUTES));
                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                /* Choose field match or matchAllQuery. */
                searchSourceBuilder.query(QueryBuilders.multiMatchQuery(query, "N", "DN", "BC"));
//                searchSourceBuilder.query(QueryBuilders
//                        .matchPhrasePrefixQuery("N", query)
//                        /* to limit the number of wildcard matches that can possibly match. */
//                        .maxExpansions(1));

                /* Term for exact query. */
                //searchSourceBuilder.query(QueryBuilders.termQuery(query, "N"));

                searchSourceBuilder.query(geoDistanceQuery("GH")
                    .geohash(geoHash)
                    .distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
                searchSourceBuilder.sort(new GeoDistanceSortBuilder("GH", geoHash).order(SortOrder.DESC));
                searchSourceBuilder.size(PaginationEnum.TEN.getLimit());
                searchRequest.source(searchSourceBuilder);
                searchRequest.scroll(TimeValue.timeValueMinutes(MINUTES));

                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
                LOG.info("Search query={} geoHash={} searchSourceBuilder={}", query, geoHash, searchSourceBuilder);
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
    public BizStoreElasticList executeNearMeSearchOnBizStoreUsingRestClient(String geoHash, String scrollId) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueMinutes(MINUTES));
                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);
                searchSourceBuilder.query(geoDistanceQuery("GH")
                    .geohash(geoHash)
                    .distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
                searchSourceBuilder.size(PaginationEnum.TEN.getLimit());
                searchRequest.source(searchSourceBuilder);
                searchRequest.scroll(TimeValue.timeValueMinutes(MINUTES));

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
    public BizStoreElasticList executeFilterBySearchOnBizStoreUsingRestClient(BusinessTypeEnum businessType, String geoHash, String scrollId) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueMinutes(MINUTES));
                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                /* Search Query. */
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("BT", businessType);
                GeoDistanceQueryBuilder geoDistanceQueryBuilder = geoDistanceQuery("GH")
                    .geohash(geoHash)
                    .distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS);
                searchSourceBuilder.query(matchQueryBuilder);

                searchSourceBuilder.size(PaginationEnum.TEN.getLimit());
                searchRequest.source(searchSourceBuilder);
                searchRequest.scroll(TimeValue.timeValueMinutes(MINUTES));

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

    private void populateSearchData(BizStoreElasticList bizStoreElastics, SearchHit[] searchHits) {
        if (searchHits != null && searchHits.length > 0) {
            for (SearchHit hit : searchHits) {
                Map<String, Object> map = hit.getSourceAsMap();
                BizStoreElastic bizStoreElastic = new BizStoreElastic()
                    .setId(map.containsKey("id") ? map.get("id").toString() : "")
                    .setBusinessName(map.containsKey("N") ? map.get("N").toString() : "")
                    .setBusinessType(map.containsKey("BT") ? BusinessTypeEnum.valueOf(map.get("BT").toString()) : BusinessTypeEnum.ST)
                    .setBizCategoryName(map.containsKey("BC") ? map.get("BC").toString() : "")
                    .setBizCategoryId(map.containsKey("BCI") ? map.get("BCI").toString() : "")
                    .setAddress(map.containsKey("AD") ? map.get("AD").toString() : "")
                    .setArea(map.containsKey("AR") ? map.get("AR").toString() : "")
                    .setTown(map.containsKey("TO") ? map.get("TO").toString() : "")
                    .setDistrict(map.containsKey("DT") ? map.get("DT").toString() : "")
                    .setStoreHourElasticList(map.containsKey("SH") ? (List<StoreHourElastic>) map.get("SH") : new ArrayList<>())
                    .setState(map.containsKey("ST") ? map.get("ST").toString() : "")
                    .setStateShortName(map.containsKey("SS") ? map.get("SS").toString() : "")
                    .setCountry(map.containsKey("CC") ? map.get("CC").toString() : "")
                    .setCountryShortName(map.containsKey("CS") ? map.get("CS").toString() : "")
                    .setPhone(map.containsKey("PH") ? map.get("PH").toString() : "")
                    .setPlaceId(map.containsKey("PI") ? map.get("PI").toString() : "")
                    .setRating(map.containsKey("RA") ? Float.parseFloat(map.get("RA").toString()) : 3.0f)
                    .setRatingCount(map.containsKey("RC") ? Integer.parseInt(map.get("RC").toString()) : 0)
                    .setBizNameId(map.containsKey("BID") ? map.get("BID").toString() : "")
                    .setDisplayName(map.containsKey("DN") ? map.get("DN").toString() : "")
                    .setProductPrice(map.containsKey("PP") ? Integer.parseInt(map.get("PP").toString()) : 0)
                    .setCodeQR(map.containsKey("QR") ? map.get("QR").toString() : "")
                    .setGeoHash(map.containsKey("GH") ? map.get("GH").toString() : "")
                    .setWebLocation(map.containsKey("WL") ? map.get("WL").toString() : "")
                    .setFamousFor(map.containsKey("FF") ? map.get("FF").toString() : "")
                    .setDisplayImage(map.containsKey("DI") ? map.get("DI").toString() : "");
                bizStoreElastics.addBizStoreElastic(bizStoreElastic);
            }
        }
    }

    @Mobile
    public BizStoreElasticList nearMeSearch(String geoHash, String scrollId) {
        return searchByBusinessType(null, geoHash, scrollId);
    }

    @Mobile
    public BizStoreElasticList searchByBusinessType(BusinessTypeEnum businessType, String geoHash, String scrollId) {
        BizStoreElasticList bizStoreElastics;
        if (null == businessType) {
            bizStoreElastics = executeNearMeSearchOnBizStoreUsingRestClient(geoHash, scrollId);
        } else {
            bizStoreElastics = executeFilterBySearchOnBizStoreUsingRestClient(businessType, geoHash, scrollId);
        }
        Set<BizStoreElastic> bizStoreElasticSet = new HashSet<>(bizStoreElastics.getBizStoreElastics());
        int hits = 0;
        while (bizStoreElasticSet.size() < PaginationEnum.TEN.getLimit() && hits < 3) {
            LOG.debug("NearMe found size={} scrollId={}",
                bizStoreElasticSet.size(),
                bizStoreElastics.getScrollId().substring(bizStoreElastics.getScrollId().length() - 10));
            BizStoreElasticList bizStoreElasticsFetched = executeNearMeSearchOnBizStoreUsingRestClient(null, bizStoreElastics.getScrollId());
            bizStoreElastics.setScrollId(bizStoreElasticsFetched.getScrollId());
            bizStoreElasticSet.addAll(bizStoreElasticsFetched.getBizStoreElastics());

            hits++;
        }
        bizStoreElastics.setBizStoreElastics(bizStoreElasticSet);
        return bizStoreElastics;
    }
}
