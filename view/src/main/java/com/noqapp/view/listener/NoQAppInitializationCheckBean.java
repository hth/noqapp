package com.noqapp.view.listener;

import com.noqapp.common.config.FirebaseConfig;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.search.elastic.service.ElasticAdministrationService;
import com.noqapp.service.FtpService;
import com.noqapp.service.payment.PaymentGatewayService;

import com.maxmind.geoip2.DatabaseReader;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

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
public class NoQAppInitializationCheckBean {
    private static final Logger LOG = LoggerFactory.getLogger(NoQAppInitializationCheckBean.class);

    @Value("${ftp.location}")
    private String ftpLocation;

    private Environment environment;
    private DataSource dataSource;
    private FirebaseConfig firebaseConfig;
    private RestHighLevelClient restHighLevelClient;
    private ElasticAdministrationService elasticAdministrationService;
    private BizStoreElasticService bizStoreElasticService;
    private DatabaseReader databaseReader;
    private FtpService ftpService;
    private PaymentGatewayService paymentGatewayService;
    private StanfordCoreNLP stanfordCoreNLP;

    @Autowired
    public NoQAppInitializationCheckBean(
        Environment environment,
        DataSource dataSource,
        FirebaseConfig firebaseConfig,
        RestHighLevelClient restHighLevelClient,
        ElasticAdministrationService elasticAdministrationService,
        BizStoreElasticService bizStoreElasticService,
        DatabaseReader databaseReader,
        FtpService ftpService,
        PaymentGatewayService paymentGatewayService,
        StanfordCoreNLP stanfordCoreNLP
    ) {
        this.environment = environment;
        this.dataSource = dataSource;
        this.firebaseConfig = firebaseConfig;
        this.restHighLevelClient = restHighLevelClient;
        this.elasticAdministrationService = elasticAdministrationService;
        this.bizStoreElasticService = bizStoreElasticService;
        this.databaseReader = databaseReader;
        this.ftpService = ftpService;
        this.paymentGatewayService = paymentGatewayService;
        this.stanfordCoreNLP = stanfordCoreNLP;
    }

    @PostConstruct
    public void checkEnvironmentIfPresent() {
        if (StringUtils.isBlank(environment.getProperty("build.env"))) {
            LOG.error("Failed to find environment for build.env");
            throw new RuntimeException("Missing server environment info");
        }
        LOG.info("*************************************");
        LOG.info("Starting server for environment={}", environment.getProperty("build.env"));
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
        } else {
            throw new RuntimeException("Firebase could not be connected");
        }
        //LOG.info("Firebase connected");
    }

    @PostConstruct
    public void checkElasticConnection() {
        try {
            if (!restHighLevelClient.ping(RequestOptions.DEFAULT)) {
                LOG.error("Elastic could not be connected");
                throw new RuntimeException("Elastic could not be connected");
            }

            MainResponse mainResponse = restHighLevelClient.info(RequestOptions.DEFAULT);
            LOG.info("Elastic {} connected clusterName={} nodeName={}\n  build={}\n  clusterUuid={}\n",
                mainResponse.getVersion(),
                mainResponse.getClusterName(),
                mainResponse.getNodeName(),
                mainResponse.getBuild(),
                mainResponse.getClusterUuid());
        } catch (IOException e) {
            LOG.error("Elastic could not be connected");
            throw new RuntimeException("Elastic could not be connected");
        }
    }

    @PostConstruct
    public void checkElasticIndex() {
        LOG.info("Running on {}", environment.getProperty("thisis"));
        if (Objects.requireNonNull(environment.getProperty("thisis")).equalsIgnoreCase("loader")) {
            /* Delete older indices. */
            elasticAdministrationService.deleteAllPreviousIndices();
        }

        if (!elasticAdministrationService.doesIndexExists(BizStoreElastic.INDEX)) {
            LOG.info("Elastic Index={} not found. Building Indexes... please wait", BizStoreElastic.INDEX);
            boolean createdMappingSuccessfully = elasticAdministrationService.addMapping(
                BizStoreElastic.INDEX,
                BizStoreElastic.TYPE);

            if (createdMappingSuccessfully) {
                LOG.info("Created Index and Mapping successfully. Adding data to Index/Type");
                bizStoreElasticService.addAllBizStoreToElastic();
            }
        } else {
            LOG.info("Elastic Index={} found", BizStoreElastic.INDEX);
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
//        boolean cashfreeSuccess = paymentGatewayService.verifyCashfree();
        if (!true) {
            LOG.error("Cashfree Payment Gateway could not be verified");
            throw new RuntimeException("Cashfree Payment Gateway could not be verified");
        }
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

    @PreDestroy
    public void applicationDestroy() {
        LOG.info("Stopping Server for environment={}", environment.getProperty("build.env"));
        LOG.info("****************** STOPPED ******************");
    }
}
