package com.noqapp.search.elastic.service;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.dsl.Conditions;
import com.noqapp.search.elastic.dsl.Filter;
import com.noqapp.search.elastic.dsl.GeoDistance;
import com.noqapp.search.elastic.dsl.Options;
import com.noqapp.search.elastic.dsl.Query;
import com.noqapp.search.elastic.dsl.QueryString;
import com.noqapp.search.elastic.dsl.Search;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.repository.BizStoreElasticManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
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

    private BizStoreElasticManager<BizStoreElastic> bizStoreElasticManager;
    private ElasticAdministrationService elasticAdministrationService;
    private BizStoreManager bizStoreManager;
    private ApiHealthService apiHealthService;

    private int limitRecords;

    @Autowired
    public BizStoreElasticService(
            @Value("${limitRecords:10}")
            int limitRecords,

            BizStoreElasticManager<BizStoreElastic> bizStoreElasticManager,
            ElasticAdministrationService elasticAdministrationService,
            BizStoreManager bizStoreManager,
            ApiHealthService apiHealthService
    ) {
        this.limitRecords = limitRecords;
        this.bizStoreElasticManager = bizStoreElasticManager;
        this.elasticAdministrationService = elasticAdministrationService;
        this.bizStoreManager = bizStoreManager;
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
        bizStoreElasticManager.delete(id);
    }

    /**
     * Helps add store when noqapp_biz_store index is missing.
     * Should be called when initializing index for first time.
     */
    public void addAllBizStoreToElastic() {
        Instant start = Instant.now();
        long count = 0;
        try (Stream<BizStoreEntity> stream = bizStoreManager.findAll()) {
            List<BizStoreElastic> bizStoreElastics = stream.map(DomainConversion::getAsBizStoreElastic).collect(Collectors.toList());
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
     *
     * @param businessName
     * @return
     */
    public List<BizStoreElastic> searchByBusinessName(String businessName) {
        LOG.info("Searching for {}", businessName);
        return bizStoreElasticManager.searchByBusinessName(businessName, limitRecords);
    }

    public String createBizStoreSearchDSLQuery(String searchParameter) {
        return createBizStoreSearchDSLQuery(searchParameter, null);
    }

    public String createBizStoreSearchDSLQuery(String searchParameter, String geoHash) {
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
            q.setQueryStringMatchAll(new QueryString().setQuery(null).setFields(null));
        }

        if (StringUtils.isNotBlank(geoHash)) {
            q.getConditions().setFilter(new Filter()
                    .setGeoDistance(new GeoDistance()
                            .setDistance(Constants.MAX_Q_SEARCH_DISTANCE)
                            .setGeoHash(geoHash)
                    ));
        }

        Search search = new Search()
                .setFrom(0)
                .setSize(10)
                .setQuery(q);

        executeSearchOnBizStoreUsingDSL(search.asJson());
        return null;
    }

    /**
     * Performs search on the index with provided DSL.
     *
     * @param dslQuery
     * @return
     */
    private String executeSearchOnBizStoreUsingDSL(String dslQuery) {
        LOG.info("DSL Query={}", dslQuery);
        return elasticAdministrationService.executeDSLQuerySearch(
                BizStoreElastic.INDEX
                        + "/"
                        + BizStoreElastic.TYPE
                        + "/_search?pretty=true",
                dslQuery
        );
    }
}
