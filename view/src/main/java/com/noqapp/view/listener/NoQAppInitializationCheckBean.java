package com.noqapp.view.listener;

import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.search.elastic.service.ElasticAdministrationService;
import com.noqapp.common.config.FirebaseConfig;
import com.noqapp.common.utils.CommonUtil;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * User: hitender
 * Date: 3/12/17 12:31 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class NoQAppInitializationCheckBean {
    private static final Logger LOG = LoggerFactory.getLogger(NoQAppInitializationCheckBean.class);

    private DataSource dataSource;
    private FirebaseConfig firebaseConfig;
    private RestHighLevelClient restHighLevelClient;
    private ElasticAdministrationService elasticAdministrationService;
    private BizStoreElasticService bizStoreElasticService;

    @Autowired
    public NoQAppInitializationCheckBean(
            DataSource dataSource,
            FirebaseConfig firebaseConfig,
            RestHighLevelClient restHighLevelClient,
            ElasticAdministrationService elasticAdministrationService,
            BizStoreElasticService bizStoreElasticService) {
        this.dataSource = dataSource;
        this.firebaseConfig = firebaseConfig;
        this.restHighLevelClient = restHighLevelClient;
        this.elasticAdministrationService = elasticAdministrationService;
        this.bizStoreElasticService = bizStoreElasticService;
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
            if (!restHighLevelClient.ping(CommonUtil.getMeSomeHeader())) {
                LOG.error("Elastic could not be connected");
                throw new RuntimeException("Elastic could not be connected");
            }

            MainResponse mainResponse = restHighLevelClient.info(CommonUtil.getMeSomeHeader());
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
        //Remove index
        elasticAdministrationService.deleteAllIndices();
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
            doSearch();
        }
    }

    //DELETE ME, for test
    private void doSearch() {
        bizStoreElasticService.createBizStoreSearchDSLQuery("Ganesh");
        bizStoreElasticService.createBizStoreSearchDSLQuery("Vashi", "te7ut0u5zy9c");
    }
}
