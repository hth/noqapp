package com.noqapp.health.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.noqapp.health.domain.ApiHealthNowEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.repository.ApiHealthNowManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;

/**
 * hitender
 * 11/20/17 2:05 AM
 */
class ApiHealthServiceTest {

    @Mock private ApiHealthNowManager apiHealthNowManager;
    private Duration duration;

    private ApiHealthService apiHealthService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        apiHealthService = new ApiHealthService(
            apiHealthNowManager
        );

        /* Mock final class. */
        duration = mock(Duration.class);
    }

    @Test
    void testInsert_long() {
        doNothing().when(apiHealthNowManager).save(any(ApiHealthNowEntity.class));
        apiHealthService.insert(
            "/insertLong",
            "insertLong",
            ApiHealthServiceTest.class.getName(),
            1L,
            HealthStatusEnum.G);
    }

    @Test
    void testInsert_duration() {
        doNothing().when(apiHealthNowManager).save(any(ApiHealthNowEntity.class));
        apiHealthService.insert(
            "/insertDuration",
            "insertDuration",
            ApiHealthServiceTest.class.getName(),
            duration,
            HealthStatusEnum.G);
    }
}