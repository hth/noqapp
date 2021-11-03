package com.noqapp.search.elastic.service;

import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.market.HouseholdItemManager;
import com.noqapp.repository.market.PropertyRentalManager;
import com.noqapp.search.elastic.domain.MarketplaceElastic;
import com.noqapp.search.elastic.domain.MarketplaceElasticList;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.repository.MarketplaceElasticManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.elasticsearch.search.SearchHit;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /** Include field are the fields to be included upon completing the search. */
    static String[] includeFields = new String[]{"BT", "CS", "DS", "EI", "GH", "VC", "RA", "MC", "PI", "PP", "TG", "TI", "TO", "TS"};
    static String[] excludeFields = new String[]{"_type"};

    private PropertyRentalManager propertyRentalManager;
    private HouseholdItemManager householdItemManager;
    private MarketplaceElasticManager<MarketplaceElastic> marketplaceElasticManager;
    private ApiHealthService apiHealthService;

    @Autowired
    public MarketplaceElasticService(
        PropertyRentalManager propertyRentalManager,
        HouseholdItemManager householdItemManager,
        MarketplaceElasticManager<MarketplaceElastic> marketplaceElasticManager,
        ApiHealthService apiHealthService
    ) {
        this.propertyRentalManager = propertyRentalManager;
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
        long countPropertyRentalElastic = 0, countHouseholdElastic = 0;
        try (Stream<PropertyRentalEntity> stream = propertyRentalManager.findAllWithStream()) {
            List<MarketplaceElastic> marketplaceElastics = new ArrayList<>();
            stream.iterator().forEachRemaining(marketplace -> {
                MarketplaceElastic marketplaceElastic = null;
                try {
                    marketplaceElastic = DomainConversion.getAsMarketplaceElastic(marketplace);
                    marketplaceElastics.add(marketplaceElastic);
                } catch (Exception e) {
                    LOG.error("Failed to insert marketplace in elastic data={} reason={}",
                        marketplaceElastic,
                        e.getLocalizedMessage(),
                        e);
                }
            });
            save(marketplaceElastics);
            countPropertyRentalElastic += marketplaceElastics.size();
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
        LOG.info("Added countPropertyRentalElastic={} countHouseholdElastic={} to Elastic in duration={}",
            countPropertyRentalElastic,
            countHouseholdElastic,
            Duration.between(start, Instant.now()).toMillis());
    }

    static void populateSearchData(MarketplaceElasticList marketplaceElastics, SearchHit[] searchHits) {
        if (searchHits != null && searchHits.length > 0) {
            for (SearchHit hit : searchHits) {
                Map<String, Object> map = hit.getSourceAsMap();
                MarketplaceElastic marketplaceElastic = new MarketplaceElastic()
                    /* Marketplace id value is set and is transmitted over the wire as it is marked to be used for fetching images. */
                    .setId(hit.getId())
                    .setBusinessType(map.containsKey("BT") ? BusinessTypeEnum.valueOf(map.get("BT").toString()) : BusinessTypeEnum.ZZ)
                    .setCountryShortName(map.containsKey("CS") ? map.get("CS").toString() : "")
                    .setDescription(map.containsKey("DS") ? map.get("DS").toString() : "")
                    .setRating(map.containsKey("RA") ? map.get("RA").toString() : "")
                    .setGeoHash(map.containsKey("GH") ? map.get("GH").toString() : "")
                    .setViewCount(map.containsKey("VC") ? Integer.parseInt(map.get("VC").toString()) : 0)
                    .setCity(map.containsKey("MC") ? map.get("MC").toString() : "")
                    .setPostImages(map.containsKey("PI") ? (List<String>) map.get("PI") : new ArrayList<>())
                    .setProductPrice(map.containsKey("PP") ? map.get("PP").toString() : "NA")
                    .setTag(map.containsKey("TG") ? map.get("TG").toString() : "")
                    .setTitle(map.containsKey("TI") ? map.get("TI").toString() : "")
                    .setTown(map.containsKey("TO") ? map.get("TO").toString() : "")
                    .setFieldTags(map.containsKey("TS") ? ((List<String>) map.get("TS")).toArray(new String[0]) : new String[]{});

                marketplaceElastics.addMarketplaceElastic(marketplaceElastic);
            }
        }
    }
}
