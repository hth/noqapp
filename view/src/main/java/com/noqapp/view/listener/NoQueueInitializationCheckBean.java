package com.noqapp.view.listener;

import com.noqapp.common.config.FirebaseConfig;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreSpatialElastic;
import com.noqapp.search.elastic.domain.IncidentEventElastic;
import com.noqapp.search.elastic.domain.MarketplaceElastic;
import com.noqapp.search.elastic.domain.PurchaseOrderElastic;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.search.elastic.service.ElasticAdministrationService;
import com.noqapp.search.elastic.service.IncidentEventElasticService;
import com.noqapp.search.elastic.service.MarketplaceElasticService;
import com.noqapp.search.elastic.service.PurchaseOrderElasticService;
import com.noqapp.service.FtpService;
import com.noqapp.service.SmsService;
import com.noqapp.service.payment.PaymentGatewayService;

import com.maxmind.geoip2.DatabaseReader;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

/**
 * User: hitender
 * Date: 3/12/17 12:31 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
@PropertySource("classpath:build-info.properties")
public class NoQueueInitializationCheckBean {
    private static final Logger LOG = LoggerFactory.getLogger(NoQueueInitializationCheckBean.class);

    @Value("${ftp.location}")
    private String ftpLocation;

    private DataSource dataSource;
    private FirebaseConfig firebaseConfig;
    private RestHighLevelClient restHighLevelClient;
    private ElasticAdministrationService elasticAdministrationService;
    private BizStoreElasticService bizStoreElasticService;
    private MarketplaceElasticService marketplaceElasticService;
    private IncidentEventElasticService incidentEventElasticService;
    private PurchaseOrderElasticService purchaseOrderElasticService;

    private JmsTemplate jmsMailSignUpTemplate;
    private DatabaseReader databaseReader;
    private FtpService ftpService;
    private PaymentGatewayService paymentGatewayService;
    private StanfordCoreNLP stanfordCoreNLP;
    private SmsService smsService;
    private LettuceConnectionFactory lettuceConnectionFactory;

    private String buildEnvironment;
    private String thisIs;

    @Autowired
    public NoQueueInitializationCheckBean(
        Environment environment,
        DataSource dataSource,
        FirebaseConfig firebaseConfig,
        RestHighLevelClient restHighLevelClient,
        ElasticAdministrationService elasticAdministrationService,
        BizStoreElasticService bizStoreElasticService,
        MarketplaceElasticService marketplaceElasticService,
        IncidentEventElasticService incidentEventElasticService,
        PurchaseOrderElasticService purchaseOrderElasticService,

        @Qualifier("jmsMailSignUpTemplate")
        JmsTemplate jmsMailSignUpTemplate,
        DatabaseReader databaseReader,
        FtpService ftpService,
        PaymentGatewayService paymentGatewayService,
        StanfordCoreNLP stanfordCoreNLP,
        SmsService smsService,
        LettuceConnectionFactory lettuceConnectionFactory
    ) {
        this.buildEnvironment = environment.getProperty("build.env");
        this.thisIs = environment.getProperty("thisis");

        this.dataSource = dataSource;
        this.firebaseConfig = firebaseConfig;
        this.restHighLevelClient = restHighLevelClient;
        this.elasticAdministrationService = elasticAdministrationService;
        this.bizStoreElasticService = bizStoreElasticService;
        this.marketplaceElasticService = marketplaceElasticService;
        this.incidentEventElasticService = incidentEventElasticService;
        this.purchaseOrderElasticService = purchaseOrderElasticService;

        this.jmsMailSignUpTemplate = jmsMailSignUpTemplate;
        this.databaseReader = databaseReader;
        this.ftpService = ftpService;
        this.paymentGatewayService = paymentGatewayService;
        this.stanfordCoreNLP = stanfordCoreNLP;
        this.smsService = smsService;
        this.lettuceConnectionFactory = lettuceConnectionFactory;
    }

    @PostConstruct
    public void checkEnvironmentIfPresent() {
        if (StringUtils.isBlank(buildEnvironment)) {
            LOG.error("Failed to find environment for build.env");
            throw new RuntimeException("Missing server environment info");
        }
        LOG.info("*************************************");
        LOG.info("Starting server for environment={}", buildEnvironment);
    }

    @PostConstruct
    public void checkRDBConnection() throws SQLException {
        if (this.dataSource.getConnection().isClosed()) {
            LOG.error("RBS could not be connected");
            throw new RuntimeException("RDB could not be connected");
        }
        LOG.info("RDB connected");
    }

    @PostConstruct
    public void hasAccessToFileSystem() {
        if (!ftpService.exist()) {
            /* Check if access set correctly for the user and remote location exists. */
            LOG.error("Cannot access file system directory, location={}", ftpLocation);
            throw new RuntimeException("File server could not be connected");
        }
        LOG.info("Found and has access, to remote ftp directory={}\n", ftpLocation);
    }

    @PostConstruct
    public void checkFirebaseConnection() {
        if (null == firebaseConfig.getFirebaseAuth()) {
            LOG.error("Firebase could not be connected");
            throw new RuntimeException("Firebase could not be connected");
        }
        LOG.info("Firebase connected");
    }

    @PostConstruct
    public void checkElasticConnection() {
        try {
            if (!restHighLevelClient.ping(RequestOptions.DEFAULT)) {
                LOG.error("Elastic could not be connected");
                throw new RuntimeException("Elastic could not be connected");
            }

            MainResponse mainResponse = restHighLevelClient.info(RequestOptions.DEFAULT);
            LOG.info("Elastic connected clusterName={} nodeName={}\n  build={}\n  clusterUuid={}\n  luceneVersion={}\n",
                mainResponse.getClusterName(),
                mainResponse.getNodeName(),
                mainResponse.getVersion().getNumber(),
                mainResponse.getClusterUuid(),
                mainResponse.getVersion().getLuceneVersion());
        } catch (IOException e) {
            LOG.error("Elastic could not be connected");
            throw new RuntimeException("Elastic could not be connected");
        }
    }

    @PostConstruct
    public void checkElasticIndex() {
        LOG.info("Running on {}", thisIs);
        if (Objects.requireNonNull(thisIs).equalsIgnoreCase("loader")) {
            /* Delete older indices. */
            elasticAdministrationService.deleteAllPreviousIndices();
        }

        if (!elasticAdministrationService.doesIndexExists(BizStoreElastic.INDEX)) {
            LOG.info("Elastic Index={} not found. Building Indexes... please wait", BizStoreElastic.INDEX);
            boolean createdBizStoreMappingSuccessfully = elasticAdministrationService.addMapping(
                BizStoreElastic.INDEX,
                BizStoreElastic.TYPE);

            boolean createdSpatialMappingSuccessfully = elasticAdministrationService.addMapping(
                BizStoreSpatialElastic.INDEX,
                BizStoreSpatialElastic.TYPE);

            if (createdBizStoreMappingSuccessfully && createdSpatialMappingSuccessfully) {
                LOG.info("Created Index and Mapping {} successfully. Adding data to Index/Type", BizStoreElastic.INDEX);
                bizStoreElasticService.addAllBizStoreToElastic();
            }
        } else {
            LOG.info("Elastic Index={} found", BizStoreElastic.INDEX);
        }

        if (!elasticAdministrationService.doesIndexExists(MarketplaceElastic.INDEX)) {
            LOG.info("Elastic Index={} not found. Building Indexes... please wait", MarketplaceElastic.INDEX);
            boolean createdMarketplaceMappingSuccessfully = elasticAdministrationService.addMapping(
                MarketplaceElastic.INDEX,
                MarketplaceElastic.TYPE);

            if (createdMarketplaceMappingSuccessfully) {
                LOG.info("Created Index and Mapping {} successfully. Adding data to Index/Type", MarketplaceElastic.INDEX);
                marketplaceElasticService.addAllMarketplaceToElastic();
            }
        } else {
            LOG.info("Elastic Index={} found", MarketplaceElastic.INDEX);
        }

        if (!elasticAdministrationService.doesIndexExists(IncidentEventElastic.INDEX)) {
            LOG.info("Elastic Index={} not found. Building Indexes... please wait", IncidentEventElastic.INDEX);
            boolean createdIncidentEventMappingSuccessfully = elasticAdministrationService.addMapping(
                IncidentEventElastic.INDEX,
                IncidentEventElastic.TYPE);

            if (createdIncidentEventMappingSuccessfully) {
                LOG.info("Created Index and Mapping {} successfully. Adding data to Index/Type", IncidentEventElastic.INDEX);
                incidentEventElasticService.addAllIncidentEventToElastic();
            }
        } else {
            LOG.info("Elastic Index={} found", IncidentEventElastic.INDEX);
        }

        if (!elasticAdministrationService.doesIndexExists(PurchaseOrderElastic.INDEX)) {
            LOG.info("Elastic Index={} not found. Building Indexes... please wait", PurchaseOrderElastic.INDEX);
            boolean createdPurchaseOrderMappingSuccessfully = elasticAdministrationService.addMapping(
                PurchaseOrderElastic.INDEX,
                PurchaseOrderElastic.TYPE);

            if (createdPurchaseOrderMappingSuccessfully) {
                LOG.info("Created Index and Mapping {} successfully. Adding data to Index/Type", PurchaseOrderElastic.INDEX);
                purchaseOrderElasticService.addAllPurchaseOrderToElastic();
            }
        } else {
            LOG.info("Elastic Index={} found", PurchaseOrderElastic.INDEX);
        }
    }

    @PostConstruct
    public void checkJMS() {
        if (Objects.requireNonNull(jmsMailSignUpTemplate.getDefaultDestinationName()).endsWith("jms.mail.signup")) {
            LOG.info("ActiveMQ JMS running destinationName={}", jmsMailSignUpTemplate.getDefaultDestinationName());
        } else {
            LOG.error("Failed connecting ActiveMQ JMS");
            throw new RuntimeException("ActiveMQ could not be connected");
        }
    }

    @PostConstruct
    public void checkGeoLite() {
        LOG.info("{} major={} minor={}\n  buildDate={}\n  ipVersion={}\n ",
            databaseReader.getMetadata().getDatabaseType(),
            databaseReader.getMetadata().getBinaryFormatMajorVersion(),
            databaseReader.getMetadata().getBinaryFormatMinorVersion(),
            databaseReader.getMetadata().getBuildDate(),
            databaseReader.getMetadata().getIpVersion());
    }

    @PostConstruct
    public void checkPaymentGateway() {
        boolean cashfreeSuccess = paymentGatewayService.verifyCashfree();
        if (!cashfreeSuccess) {
            LOG.error("Cashfree Payment Gateway could not be verified");
            throw new RuntimeException("Cashfree Payment Gateway could not be verified");
        }
    }

    @PostConstruct
    public void checkPayoutPaymentGateway() {
//        boolean cashfreeSuccess = paymentGatewayService.verifyCashfreePayout();
//        if (!cashfreeSuccess) {
//            LOG.error("Cashfree Payout could not be verified");
//            throw new RuntimeException("Cashfree Payout could not be verified");
//        }
    }

    @PostConstruct
    public void checkNLP() {
        String text = "NoQueue is now up and running with sentiments that are ";
        Annotation annotation = stanfordCoreNLP.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            LOG.info("{} {}", sentence, sentiment);
        }
    }

    @PostConstruct
    public void checkAvailableSMS() {
        int availableSMS = smsService.findAvailableSMS();
        if (0 == availableSMS) {
            LOG.error("SMS balance is now {}. Will no longer send SMS", availableSMS);
        } else {
            LOG.info("SMS available {}", availableSMS);
        }
    }

    @PreDestroy
    public void applicationDestroy() {
        try {
            lettuceConnectionFactory.getConnection().flushAll();
            LOG.info("Flushed all db from redis");
        } catch (Exception e) {
            LOG.error("Failed flushing redis all database reason={}", e.getLocalizedMessage(), e);
        }
        LOG.info("Stopping Server for environment={}", buildEnvironment);
        LOG.info("****************** STOPPED ******************");
    }
}
