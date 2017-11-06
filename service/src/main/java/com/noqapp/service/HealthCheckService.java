package com.noqapp.service;

import com.noqapp.domain.json.JsonHealthCheck;
import com.noqapp.repository.QueueManagerJDBC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * User: hitender
 * Date: 11/06/17 12:20 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class HealthCheckService {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckService.class);

    private GenerateUserIdService generateUserIdService;
    private QueueManagerJDBC queueManagerJDBC;

    private enum HealthStatus {GOOD, BAD}

    @Autowired
    public HealthCheckService(
            GenerateUserIdService generateUserIdService,
            QueueManagerJDBC queueManagerJDBC
    ) {
        this.generateUserIdService = generateUserIdService;
        this.queueManagerJDBC = queueManagerJDBC;
    }

    public String doHealthCheck() {
        JsonHealthCheck jsonHealthCheck = new JsonHealthCheck();
        jsonHealthCheck.setWeb(HealthStatus.GOOD.name()).increaseHealth();

        try {
            generateUserIdService.getLastGenerateUserId();
            jsonHealthCheck.setMongo(HealthStatus.GOOD.name()).increaseHealth();
        } catch (Exception e) {
            LOG.error("Failed Mongo connection reason={}", e.getLocalizedMessage(), e);
            jsonHealthCheck.setMongo(HealthStatus.BAD.name());
        }

        try {
            queueManagerJDBC.isDBAlive();
            jsonHealthCheck.setMysql(HealthStatus.GOOD.name()).increaseHealth();
        } catch (SQLException e) {
            LOG.error("Failed MySql sqlState={} errorCode={}", e.getSQLState(), e.getErrorCode(), e);
            jsonHealthCheck.setMysql(HealthStatus.BAD.name());
        }

        return jsonHealthCheck.asJson();
    }
}
