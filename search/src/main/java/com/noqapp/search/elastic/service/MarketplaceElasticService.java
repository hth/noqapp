package com.noqapp.search.elastic.service;

import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.PropertyEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.market.HouseholdItemManager;
import com.noqapp.repository.market.PropertyManager;
import com.noqapp.search.elastic.domain.MarketplaceElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.repository.MarketplaceElasticManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * hitender
 * 2/28/21 11:10 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class MarketplaceElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceElasticService.class);

    private PropertyManager propertyManager;
    private HouseholdItemManager householdItemManager;
    private MarketplaceElasticManager<MarketplaceElastic> marketplaceElasticManager;
    private ApiHealthService apiHealthService;

    @Autowired
    public MarketplaceElasticService(
        PropertyManager propertyManager,
        HouseholdItemManager householdItemManager,
        MarketplaceElasticManager<MarketplaceElastic> marketplaceElasticManager,
        ApiHealthService apiHealthService
    ) {
        this.propertyManager = propertyManager;
        this.householdItemManager = householdItemManager;
        this.marketplaceElasticManager = marketplaceElasticManager;
        this.apiHealthService = apiHealthService;
    }

    @Async
    public void save(MarketplaceElastic marketplaceElastic) {
        marketplaceElasticManager.save(marketplaceElastic);
    }

    @Async
    void save(List<MarketplaceElastic> marketplaceElastics) {
        LOG.info("Bulk save size={}", marketplaceElastics.size());
        if (!marketplaceElastics.isEmpty()) {
            marketplaceElasticManager.save(marketplaceElastics);
        }
    }

    @Async
    public void delete(String id) {
        LOG.info("Deleting store from elastic id={}", id);
        marketplaceElasticManager.delete(id);
    }

    /**
     * Helps add marketplace when marketplace index is missing.
     * Should be called when initializing index for first time.
     */
    public void addAllMarketplaceToElastic() {
        Instant start = Instant.now();
        long countPropertyElastic = 0, countHouseholdElastic = 0;
        try (Stream<PropertyEntity> stream = propertyManager.findAllWithStream()) {
            List<MarketplaceElastic> marketplaceElastics = new ArrayList<>();
            stream.iterator().forEachRemaining(marketplace -> {
                MarketplaceElastic marketplaceElastic = null;
                try {
                    marketplaceElastic = DomainConversion.getAsMarketplaceElastic(marketplace);
                    marketplaceElastics.add(marketplaceElastic);
                } catch (Exception e) {
                    LOG.error("Failed to insert in elastic data={} reason={}",
                        marketplaceElastic,
                        e.getLocalizedMessage(),
                        e);
                }
            });
            save(marketplaceElastics);
            countPropertyElastic += marketplaceElastics.size();
        }

        try (Stream<HouseholdItemEntity> stream = householdItemManager.findAllWithStream()) {
            List<MarketplaceElastic> marketplaceElastics = new ArrayList<>();
            stream.iterator().forEachRemaining(marketplace -> {
                MarketplaceElastic marketplaceElastic = null;
                try {
                    marketplaceElastic = DomainConversion.getAsMarketplaceElastic(marketplace);
                    marketplaceElastics.add(marketplaceElastic);
                } catch (Exception e) {
                    LOG.error("Failed to insert in elastic data={} reason={}",
                        marketplaceElastic,
                        e.getLocalizedMessage(),
                        e);
                }
            });
            save(marketplaceElastics);
            countHouseholdElastic += marketplaceElastics.size();
        }

        apiHealthService.insert(
            "/addAllMarketplaceToElastic",
            "addAllMarketplaceToElastic",
            this.getClass().getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        LOG.info("Added countPropertyElastic={} countHouseholdElastic={} to Elastic in duration={}",
            countPropertyElastic,
            countHouseholdElastic,
            Duration.between(start, Instant.now()).toMillis());
    }
}
