package com.noqapp.view.listener;

import com.noqapp.domain.elastic.BizStoreElasticEntity;
import com.noqapp.service.ElasticAdministrationService;
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

    @Autowired
    public NoQAppInitializationCheckBean(
            DataSource dataSource,
            FirebaseConfig firebaseConfig,
            RestHighLevelClient restHighLevelClient,
            ElasticAdministrationService elasticAdministrationService
    ) {
        this.dataSource = dataSource;
        this.firebaseConfig = firebaseConfig;
        this.restHighLevelClient = restHighLevelClient;
        this.elasticAdministrationService = elasticAdministrationService;
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
        if (!elasticAdministrationService.doesIndexExists(BizStoreElasticEntity.INDEX)) {
            LOG.info("Elastic Index={} not found. Building Indexes... please wait", BizStoreElasticEntity.INDEX);
            elasticAdministrationService.addAllBizStoreToElastic();
        } else {
            LOG.info("Elastic Index={} found", BizStoreElasticEntity.INDEX);
        }
    }
}
