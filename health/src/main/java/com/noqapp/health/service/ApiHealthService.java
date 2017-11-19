package com.noqapp.health.service;

import com.noqapp.health.domain.ApiHealthContinuousEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.repository.ApiHealthContinuousManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * User: hitender
 * Date: 11/07/17 11:01 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ApiHealthService {
    private static final Logger LOG = LoggerFactory.getLogger(ApiHealthService.class);

    private ApiHealthContinuousManager apiHealthContinuousManager;

    private ExecutorService service;

    @Autowired
    public ApiHealthService(ApiHealthContinuousManager apiHealthContinuousManager) {
        this.apiHealthContinuousManager = apiHealthContinuousManager;

        this.service = newCachedThreadPool();
    }

    public void insert(String apiName, String methodName, String clazzName, long duration, HealthStatusEnum healthStatus) {
        service.submit(() -> invokeThreadToInsert(apiName, methodName, clazzName, duration, healthStatus));
    }

    public void insert(String apiName, String methodName, String clazzName, Duration duration, HealthStatusEnum healthStatus) {
        service.submit(() -> invokeThreadToInsert(apiName, methodName, clazzName, duration.toMillis(), healthStatus));
    }

    private void invokeThreadToInsert(String apiName, String methodName, String clazzName, long duration, HealthStatusEnum healthStatus) {
        LOG.info("{} {} {} {} ms", apiName, methodName, healthStatus, duration);
        apiHealthContinuousManager.save(
                new ApiHealthContinuousEntity()
                        .setApi(apiName)
                        .setMethodName(methodName)
                        .setClazzName(clazzName)
                        .setDuration(duration)
                        .setHealthStatus(healthStatus)
        );
    }
}
