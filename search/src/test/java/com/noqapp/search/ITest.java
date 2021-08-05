package com.noqapp.search;

import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.health.repository.ApiHealthNowManager;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.market.HouseholdItemManager;
import com.noqapp.repository.market.HouseholdItemManagerImpl;
import com.noqapp.repository.market.PropertyRentalManager;
import com.noqapp.repository.market.PropertyRentalManagerImpl;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.search.elastic.domain.MarketplaceElastic;
import com.noqapp.search.elastic.repository.MarketplaceElasticManager;
import com.noqapp.search.elastic.repository.MarketplaceElasticManagerImpl;
import com.noqapp.search.elastic.service.ElasticAdministrationService;

import org.springframework.mock.env.MockEnvironment;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockitoAnnotations;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import okhttp3.OkHttpClient;

import java.util.Properties;
import java.util.UUID;

/**
 * hitender
 * 8/4/21 2:45 PM
 */
public class ITest extends RealMongoForITest {
    protected String did;
    protected String didClient1;
    protected String didClient2;
    protected String didQueueSupervisor;
    protected String fcmToken;
    protected String model;
    protected String osVersion;
    protected String appVersion;
    protected String deviceType;

    protected PropertyRentalManager propertyRentalManager;
    protected HouseholdItemManager householdItemManager;
    protected MarketplaceElasticManager<MarketplaceElastic> marketplaceElasticManager;
    protected ApiHealthNowManager apiHealthNowManager;

    protected ElasticAdministrationService elasticAdministrationService;
    protected ElasticsearchClientConfiguration elasticsearchClientConfiguration;
    protected ApiHealthService apiHealthService;

    private StanfordCoreNLP stanfordCoreNLP;
    private MaxentTagger maxentTagger;
    private MockEnvironment mockEnvironment;

    @BeforeAll
    public void globalISetup() {
        MockitoAnnotations.openMocks(this);

        did = UUID.randomUUID().toString();
        didClient1 = UUID.randomUUID().toString();
        didClient2 = UUID.randomUUID().toString();
        didQueueSupervisor = UUID.randomUUID().toString();
        mockEnvironment = new MockEnvironment();
        mockEnvironment.setProperty("build.env", "sandbox");

        Properties nlpProperties = new Properties();
        nlpProperties.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        stanfordCoreNLP = new StanfordCoreNLP(nlpProperties);

        fcmToken = UUID.randomUUID().toString();
        deviceType = DeviceTypeEnum.A.getName();
        model = "Model";
        osVersion = "OS-Version";
        appVersion = "1.3.150";

        propertyRentalManager = new PropertyRentalManagerImpl(getMongoTemplate());
        householdItemManager = new HouseholdItemManagerImpl(getMongoTemplate());

        apiHealthService = new ApiHealthService(apiHealthNowManager);

        marketplaceElasticManager = new MarketplaceElasticManagerImpl(getRestHighLevelClient());

        elasticsearchClientConfiguration = new ElasticsearchClientConfiguration();

        elasticAdministrationService = new ElasticAdministrationService(
            "localhost",
            HTTP_TEST_PORT,
            new OkHttpClient.Builder().build(),
            apiHealthService,
            elasticsearchClientConfiguration
        );
    }


}
