package com.noqapp.search.elastic.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.GenerateUserIds;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.domain.types.catgeory.RentalTypeEnum;
import com.noqapp.search.ITest;
import com.noqapp.search.elastic.domain.MarketplaceElastic;
import com.noqapp.search.elastic.domain.MarketplaceElasticList;
import com.noqapp.search.elastic.helper.GeoIP;
import com.noqapp.search.elastic.json.ElasticMarketplaceSearchSource;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
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
            3,
            elasticAdministrationService,
            restHighLevelClient
        );

        assertTrue(elasticAdministrationService.deleteAllIndices(), "Deleted successfully elastic index");
        elasticAdministrationService.addMapping(MarketplaceElastic.INDEX, MarketplaceElastic.TYPE);
        Random random = new Random();
        for (int i = 1; i <= 10; i++) {
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
                .setViewCount(0)
                .setExpressedInterestCount(random.ints(0, 10).findFirst().getAsInt())
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

    @AfterEach
    public void cleanUp() throws IOException {
        cleanUpIndex();
        getMongoTemplate().dropCollection(PropertyRentalEntity.class);
    }

    @Test
    void createMarketplaceSearchDSLQuery() throws IOException {
        GeoIP geoIp = new GeoIP("0.0.0.0", "Mumbai", Double.parseDouble("28.6858"), Double.parseDouble("77.231"));
        List<ElasticMarketplaceSearchSource> list = marketplaceSearchElasticService.createMarketplaceSearchDSLQuery("1_BE", geoIp.getGeoHash());
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void nearMeExcludedMarketTypes() throws IOException {
        GeoIP geoIp = new GeoIP("0.0.0.0", "Mumbai", Double.parseDouble("28.6858"), Double.parseDouble("77.231"));
        Collection<MarketplaceElastic> e;

        MarketplaceElasticList marketplaceElasticList = marketplaceSearchElasticService.nearMeExcludedMarketTypes(
            BusinessTypeEnum.excludePropertyRental(),
            BusinessTypeEnum.includePropertyRental(),
            BusinessTypeEnum.PR,
            geoIp.getGeoHash(),
            null,
            0
        );

        Assertions.assertEquals(3, marketplaceElasticList.getMarketplaceElastics().size());
        Collection<MarketplaceElastic> a = marketplaceElasticList.getMarketplaceElastics();
        e = new ArrayList<>(marketplaceElasticList.getMarketplaceElastics());

        marketplaceElasticList = marketplaceSearchElasticService.nearMeExcludedMarketTypes(
            BusinessTypeEnum.excludePropertyRental(),
            BusinessTypeEnum.includePropertyRental(),
            BusinessTypeEnum.PR,
            geoIp.getGeoHash(),
            null,
            marketplaceElasticList.getFrom()
        );

        Assertions.assertEquals(3, marketplaceElasticList.getMarketplaceElastics().size());
        Collection<MarketplaceElastic> b = marketplaceElasticList.getMarketplaceElastics();
        e.addAll(b);
        Assertions.assertEquals(6, e.size());

        marketplaceElasticList = marketplaceSearchElasticService.nearMeExcludedMarketTypes(
            BusinessTypeEnum.excludePropertyRental(),
            BusinessTypeEnum.includePropertyRental(),
            BusinessTypeEnum.PR,
            geoIp.getGeoHash(),
            null,
            marketplaceElasticList.getFrom()
        );

        Assertions.assertEquals(3, marketplaceElasticList.getMarketplaceElastics().size());
        Collection<MarketplaceElastic> c = marketplaceElasticList.getMarketplaceElastics();
        e.addAll(c);
        Assertions.assertEquals(9, e.size());

        marketplaceElasticList = marketplaceSearchElasticService.nearMeExcludedMarketTypes(
            BusinessTypeEnum.excludePropertyRental(),
            BusinessTypeEnum.includePropertyRental(),
            BusinessTypeEnum.PR,
            geoIp.getGeoHash(),
            null,
            marketplaceElasticList.getFrom()
        );

        Assertions.assertEquals(1, marketplaceElasticList.getMarketplaceElastics().size());
        Collection<MarketplaceElastic> d = marketplaceElasticList.getMarketplaceElastics();
        e.addAll(d);
        Assertions.assertEquals(10, e.size());
    }

    private void cleanUpIndex() throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(MarketplaceElastic.INDEX);
        request.setQuery(QueryBuilders.matchAllQuery());
        BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        Assertions.assertEquals(10, response.getStatus().getDeleted());
    }
}
