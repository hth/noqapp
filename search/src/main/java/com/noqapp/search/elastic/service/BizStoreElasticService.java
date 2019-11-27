package com.noqapp.search.elastic.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreElasticList;
import com.noqapp.search.elastic.domain.StoreHourElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.repository.BizStoreElasticManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.elasticsearch.search.SearchHit;

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
    static final long MINUTES = 10L;

    static String[] includeFields = new String[]{
        "N", "BT", "BC", "BCI", "BID", "AD", "AR", "TO", "DT", "SH",
        "ST", "SS", "CC", "CS", "PH", "PI", "RA", "RC", "DN", "QR",
        "GH", "WL", "FF", "DI"};
    static String[] excludeFields = new String[]{"_type"};

    private BizStoreElasticManager<BizStoreElastic> bizStoreElasticManager;
    private ElasticAdministrationService elasticAdministrationService;
    private BizStoreManager bizStoreManager;
    private StoreHourManager storeHourManager;
    private BizStoreSpatialElasticService bizStoreSpatialElasticService;
    private ApiHealthService apiHealthService;

    @Autowired
    public BizStoreElasticService(
        BizStoreElasticManager<BizStoreElastic> bizStoreElasticManager,
        ElasticAdministrationService elasticAdministrationService,
        BizStoreManager bizStoreManager,
        StoreHourManager storeHourManager,
        BizStoreSpatialElasticService bizStoreSpatialElasticService,
        ApiHealthService apiHealthService
    ) {
        this.bizStoreElasticManager = bizStoreElasticManager;
        this.elasticAdministrationService = elasticAdministrationService;
        this.bizStoreManager = bizStoreManager;
        this.storeHourManager = storeHourManager;
        this.bizStoreSpatialElasticService = bizStoreSpatialElasticService;
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
    void saveSpatial(Set<BizStoreElastic> bizStoreElastics) {
        LOG.info("Bulk save size={}", bizStoreElastics.size());
        if (!bizStoreElastics.isEmpty()) {
            bizStoreSpatialElasticService.save(bizStoreElastics);
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
        long countBizStoreElastic = 0, countBizStoreSpatialElastic = 0;
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
            countBizStoreElastic += bizStoreElastics.size();

            Set<BizStoreElastic> bizStoreSpatialElastics = new HashSet<>(bizStoreElastics);
            saveSpatial(bizStoreSpatialElastics);
            countBizStoreSpatialElastic += bizStoreSpatialElastics.size();
        }

        apiHealthService.insert(
            "/addAllBizStoreToElastic",
            "addAllBizStoreToElastic",
            this.getClass().getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        LOG.info("Added countBizStoreElastic={} countBizStoreSpatialElastic={} to Elastic in duration={}",
            countBizStoreElastic,
            countBizStoreSpatialElastic,
            Duration.between(start, Instant.now()).toMillis());
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

    static void populateSearchData(BizStoreElasticList bizStoreElastics, SearchHit[] searchHits) {
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
}
