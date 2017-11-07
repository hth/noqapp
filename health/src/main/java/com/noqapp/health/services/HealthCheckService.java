package com.noqapp.health.services;

import com.noqapp.health.domain.json.JsonHealthCheck;
import com.noqapp.health.domain.json.JsonHealthServiceCheck;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.service.GenerateUserIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public HealthCheckService(
            GenerateUserIdService generateUserIdService,
            QueueManagerJDBC queueManagerJDBC
    ) {
        this.generateUserIdService = generateUserIdService;
        this.queueManagerJDBC = queueManagerJDBC;
    }

    public void doHealthCheck(JsonHealthCheck jsonHealthCheck) {
        JsonHealthServiceCheck jsonHealthServiceCheck = new JsonHealthServiceCheck("sm");
        try {
            generateUserIdService.getLastGenerateUserId();
            jsonHealthServiceCheck.ended().setHealthStatus(HealthStatusEnum.G);
            jsonHealthCheck.increaseServiceUpCount();
        } catch (Exception e) {
            LOG.error("Failed Mongo connection reason={}", e.getLocalizedMessage(), e);
            jsonHealthServiceCheck.ended().setHealthStatus(HealthStatusEnum.B);
        }
        jsonHealthCheck.addJsonHealthServiceChecks(jsonHealthServiceCheck);

        jsonHealthServiceCheck = new JsonHealthServiceCheck("sr");
        try {
            queueManagerJDBC.isDBAlive();
            jsonHealthServiceCheck.ended().setHealthStatus(HealthStatusEnum.G);
            jsonHealthCheck.increaseServiceUpCount();
        } catch (Exception e) {
            LOG.error("Failed MySql reason={}", e.getLocalizedMessage(), e);
            jsonHealthServiceCheck.ended().setHealthStatus(HealthStatusEnum.B);
        }
        jsonHealthCheck.addJsonHealthServiceChecks(jsonHealthServiceCheck);
    }
}
