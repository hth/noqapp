package com.noqapp.health.service;

import com.noqapp.health.domain.ApiHealthContinuousEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.repository.ApiHealthContinuousManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * hitender
 * 11/20/17 2:05 AM
 */
class ApiHealthServiceTest {

    @Mock private ApiHealthContinuousManager apiHealthContinuousManager;
    private Duration duration;

    private ApiHealthService apiHealthService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        apiHealthService = new ApiHealthService(
            apiHealthContinuousManager
        );

        /* Mock final class. */
        duration = mock(Duration.class);
    }

    @Test
    void testInsert_long() {
        doNothing().when(apiHealthContinuousManager).save(any(ApiHealthContinuousEntity.class));
        apiHealthService.insert(
                "/insertLong",
                "insertLong",
                ApiHealthServiceTest.class.getName(),
                1L,
                HealthStatusEnum.G);
    }

    @Test
    void testInsert_duration() {
        doNothing().when(apiHealthContinuousManager).save(any(ApiHealthContinuousEntity.class));
        apiHealthService.insert(
                "/insertDuration",
                "insertDuration",
                ApiHealthServiceTest.class.getName(),
                duration,
                HealthStatusEnum.G);
    }
}