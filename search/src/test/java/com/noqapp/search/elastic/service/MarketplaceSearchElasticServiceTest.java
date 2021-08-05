package com.noqapp.search.elastic.service;

import static org.junit.jupiter.api.Assertions.*;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.GenerateUserIds;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.domain.types.catgeory.RentalTypeEnum;
import com.noqapp.repository.market.PropertyRentalManager;
import com.noqapp.search.ITest;
import com.noqapp.search.elastic.domain.MarketplaceElastic;
import com.noqapp.search.elastic.domain.MarketplaceElasticList;
import com.noqapp.search.elastic.helper.GeoIP;

import org.apache.cxf.Bus;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

/**
 * hitender
 * 8/4/21 2:44 PM
 */
@DisplayName("Marketplace Elastic Search")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("api")
class MarketplaceSearchElasticServiceTest extends ITest {

    protected MarketplaceElasticService marketplaceElasticService;
    protected MarketplaceSearchElasticService marketplaceSearchElasticService;

    @BeforeEach
    void setUp() {
        marketplaceElasticService = new MarketplaceElasticService(
            propertyRentalManager,
            householdItemManager,
            marketplaceElasticManager,
            apiHealthService
        );

        marketplaceSearchElasticService = new MarketplaceSearchElasticService(
            elasticAdministrationService,
            restHighLevelClient
        );

        assertTrue(elasticAdministrationService.deleteAllIndices(), "Deleted successfully elastic index");
        elasticAdministrationService.addMapping(MarketplaceElastic.INDEX, MarketplaceElastic.TYPE);
        for (int i = 1; i <= 100; i++) {
            MarketplaceEntity marketplaceEntity = new PropertyRentalEntity()
                .setRentalType(RentalTypeEnum.A)
                .setBathroom(1)
                .setBedroom(1)
                .setCarpetArea(1000)
                .setQueueUserId(String.valueOf(GenerateUserIds.STARTING_USER_ID))
                .setBusinessType(BusinessTypeEnum.PR)
                .setCoordinate(new double[]{77.231, 28.6858})
                .setProductPrice(String.valueOf(10000))
                .setTitle("This is title " + i)
                .setDescription("This is description " + i)
                .setPostImages(Set.of(CommonUtil.generateHexFromObjectId()))
                .setTags("1_BE 4_BR 3000_CA A_RT")
                .setLikeCount(0)
                .setExpressedInterestCount(0)
                .setAddress("Mumbai")
                .setCity("Mumbai")
                .setTown("Mumbai")
                .setCountryShortName("IN")
                .setLandmark("Near Station " + i)
                .setPublishUntil(DateUtil.plusDays(2))
                .setValidateByQid(String.valueOf(GenerateUserIds.STARTING_USER_ID))
                .setValidateStatus(ValidateStatusEnum.A)
                .setIpAddress("0.0.0." + i);

            propertyRentalManager.save((PropertyRentalEntity) marketplaceEntity);
        }

        marketplaceElasticService.addAllMarketplaceToElastic();
    }

    @AfterAll
    public void tearDown() {
        assertTrue(elasticAdministrationService.deleteAllIndices(), "Deleted successfully elastic index");
    }

    @Test
    void createMarketplaceSearchDSLQuery() {
//        marketplaceSearchElasticService.createMarketplaceSearchDSLQuery()
    }

    @Test
    void nearMeExcludedMarketTypes() {
        GeoIP geoIp = new GeoIP("0.0.0.0", "Mumbai", Double.parseDouble("28.6858"), Double.parseDouble("77.231"));

        MarketplaceElasticList marketplaceElasticList = marketplaceSearchElasticService.nearMeExcludedMarketTypes(
            BusinessTypeEnum.excludePropertyRental(),
            BusinessTypeEnum.includePropertyRental(),
            BusinessTypeEnum.PR,
            geoIp.getGeoHash(),
            null
        );

        Assertions.assertTrue(marketplaceElasticList.getSize() > 0);
    }
}
