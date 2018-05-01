package com.noqapp.search.elastic.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noqapp.common.utils.Constants;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreElasticList;
import com.noqapp.search.elastic.domain.StoreHourElastic;
import com.noqapp.search.elastic.dsl.Conditions;
import com.noqapp.search.elastic.dsl.Filter;
import com.noqapp.search.elastic.dsl.GeoDistance;
import com.noqapp.search.elastic.dsl.Options;
import com.noqapp.search.elastic.dsl.Query;
import com.noqapp.search.elastic.dsl.QueryString;
import com.noqapp.search.elastic.dsl.Search;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.json.ElasticBizStoreSource;
import com.noqapp.search.elastic.json.ElasticResult;
import com.noqapp.search.elastic.repository.BizStoreElasticManager;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;

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

    private static String[] includeFields = new String[] {"N", "BT", "BC", "BCI", "BID", "AD", "AR", "TO", "DT", "SH", "ST", "SS", "CC", "CS", "PH", "PI", "RA", "RC", "DN", "QR", "GH", "WL", "DI"};
    private static String[] excludeFields = new String[] {"_type"};

    private BizStoreElasticManager<BizStoreElastic> bizStoreElasticManager;
    private ElasticAdministrationService elasticAdministrationService;
    private ElasticsearchClientConfiguration elasticsearchClientConfiguration;
    private BizStoreManager bizStoreManager;
    private StoreHourManager storeHourManager;
    private ApiHealthService apiHealthService;

    private int limitRecords;
    private ObjectMapper objectMapper;

    @Autowired
    public BizStoreElasticService(
            @Value("${limitRecords:5}")
            int limitRecords,

            BizStoreElasticManager<BizStoreElastic> bizStoreElasticManager,
            ElasticAdministrationService elasticAdministrationService,
            ElasticsearchClientConfiguration elasticsearchClientConfiguration,
            BizStoreManager bizStoreManager,
            StoreHourManager storeHourManager,
            ApiHealthService apiHealthService
    ) {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.limitRecords = limitRecords;
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
        return bizStoreElasticManager.searchByBusinessName(businessName, limitRecords);
    }

    public List<ElasticBizStoreSource> createBizStoreSearchDSLQuery(String searchParameter) {
        return createBizStoreSearchDSLQuery(searchParameter, null);
    }

    public List<ElasticBizStoreSource> createBizStoreSearchDSLQuery(String searchParameter, String geoHash) {
        LOG.info("User search parameter={}", searchParameter);

        Query q = new Query();
        if (StringUtils.isNotBlank(searchParameter)) {
            /* Search across all the specified fields. */
            q.setConditions(new Conditions()
                    .setOptions(new Options()
                            .setQueryStringMultiMatch(new QueryString()
                                    .setQuery(searchParameter)
                            )
                    )
            );
        } else {
            /* When blank then do a match all. Should be avoided as its little too vague and set Fields as null. */
            q.setConditions(new Conditions().setOptions(new Options().setQueryStringMatchAll(new QueryString().setFields(null))));
        }

        if (StringUtils.isNotBlank(geoHash)) {
            q.getConditions().setFilter(new Filter()
                    .setGeoDistance(new GeoDistance()
                            .setDistance(Constants.MAX_Q_SEARCH_DISTANCE_WITH_UNITS)
                            .setGeoHash(geoHash)
                    ));
        }

        LOG.info("Elastic query q={}", q.asJson());
        Search search = new Search()
                .setFrom(0)
                .setSize(limitRecords)
                .setQuery(q);

        String result = executeSearchOnBizStoreUsingDSLFilteredData(search.asJson());
        if (StringUtils.isNotBlank(result)) {
            try {
                //TODO(hth) this is hard coded to just one type of search; should be extendable for other searches.
                ElasticResult elasticResult = objectMapper.readValue(result, ElasticResult.class);
                return elasticResult.getHits() == null ? new ArrayList<>() : elasticResult.getHits().getElasticSources();
            } catch (IOException e) {
                LOG.error("Failed parsing elastic result searchParameter={} reason={}", searchParameter, e.getLocalizedMessage(), e);
                return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }

    /**
     * Performs search on the index with provided DSL.
     */
    private String executeSearchOnBizStoreUsingDSL(String dslQuery) {
        LOG.info("DSL Query={}", dslQuery);
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

    /**
     * Performs search on the index with provided DSL with filtered set of data sent in response. The fields below
     * are fetched when searched. Fetched fields are populated in mapped object.
     */
    private String executeSearchOnBizStoreUsingDSLFilteredData(String dslQuery) {
        LOG.info("DSL Query={}", dslQuery);
        String result = elasticAdministrationService.executeDSLQuerySearch(
                BizStoreElastic.INDEX
                        + "/"
                        + BizStoreElastic.TYPE
                        + "/_search?pretty&filter_path=hits.hits._source&_source=N,BT,BC,BCI,BID,AD,AR,TO,DT,SH,ST,SS,CC,CS,PH,PI,RA,RC,DN,QR,GH,WL,DI",
                dslQuery
        );

        LOG.info("DSL Query result={}", result);
        return result;
    }

    @Mobile
    public BizStoreElasticList executeSearchOnBizStoreUsingRestClient(String geoHash, String scrollId) throws IOException {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();

        SearchResponse searchResponse;
        if (StringUtils.isNotBlank(scrollId)) {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(TimeValue.timeValueSeconds(30));
            searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().searchScroll(scrollRequest);
        } else {
            SearchRequest searchRequest = new SearchRequest(BizStoreElastic.INDEX);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.fetchSource(includeFields, excludeFields);
//        searchSourceBuilder.query(matchQuery("CC", "India"));
            searchSourceBuilder.query(geoDistanceQuery("GH").geohash(geoHash).distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
            searchSourceBuilder.size(limitRecords);
            searchRequest.source(searchSourceBuilder);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));

            searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().search(searchRequest);
        }

        bizStoreElastics.setScrollId(searchResponse.getScrollId());
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
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
                        .setRating(map.containsKey("RA") ? Float.valueOf(map.get("RA").toString()) : 3.0f)
                        .setRatingCount(map.containsKey("RC") ? Integer.valueOf(map.get("RC").toString()) : 0)
                        .setBizNameId(map.containsKey("BID") ? map.get("BID").toString() : "")
                        .setDisplayName(map.containsKey("DN") ? map.get("DN").toString() : "")
                        .setCodeQR(map.containsKey("QR") ? map.get("QR").toString() : "")
                        .setGeoHash(map.containsKey("GH") ? map.get("GH").toString() : "")
                        .setWebLocation(map.containsKey("WL") ? map.get("WL").toString() : "")
                        .setDisplayImage(map.containsKey("DI") ? map.get("DI").toString() : "");

                //TODO(hth) remove this call, currently it populates the images
                bizStoreElastic.getDisplayImage();
                bizStoreElastics.addBizStoreElastic(bizStoreElastic);
            }
        }
        return bizStoreElastics;
    }
}
