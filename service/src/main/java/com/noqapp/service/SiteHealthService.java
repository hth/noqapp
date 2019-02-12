package com.noqapp.service;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.health.domain.json.JsonSiteHealth;
import com.noqapp.health.domain.json.JsonSiteHealthService;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.repository.QueueManagerJDBC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 11/06/17 12:20 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class SiteHealthService {
    private static final Logger LOG = LoggerFactory.getLogger(SiteHealthService.class);

    private GenerateUserIdService generateUserIdService;
    private QueueManagerJDBC queueManagerJDBC;

    @Autowired
    public SiteHealthService(
        GenerateUserIdService generateUserIdService,
        QueueManagerJDBC queueManagerJDBC
    ) {
        this.generateUserIdService = generateUserIdService;
        this.queueManagerJDBC = queueManagerJDBC;
    }

    @Mobile
    public void doSiteHealthCheck(JsonSiteHealth jsonSiteHealth) {
        JsonSiteHealthService jsonSiteHealthService = new JsonSiteHealthService("sm");
        try {
            generateUserIdService.getLastGenerateUserId();
            jsonSiteHealthService.ended().setHealthStatus(HealthStatusEnum.G);
            jsonSiteHealth.increaseServiceUpCount();
        } catch (Exception e) {
            LOG.error("Failed Mongo connection reason={}", e.getLocalizedMessage(), e);
            jsonSiteHealthService.ended().setHealthStatus(HealthStatusEnum.F);
        }
        jsonSiteHealth.addJsonHealthServiceChecks(jsonSiteHealthService);

        jsonSiteHealthService = new JsonSiteHealthService("sr");
        try {
            queueManagerJDBC.isDBAlive();
            jsonSiteHealthService.ended().setHealthStatus(HealthStatusEnum.G);
            jsonSiteHealth.increaseServiceUpCount();
        } catch (Exception e) {
            LOG.error("Failed MySql reason={}", e.getLocalizedMessage(), e);
            jsonSiteHealthService.ended().setHealthStatus(HealthStatusEnum.F);
        }
        jsonSiteHealth.addJsonHealthServiceChecks(jsonSiteHealthService);
    }
}
