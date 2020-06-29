package com.noqapp.service;

import static org.junit.jupiter.api.Assertions.*;

import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.TokenQueueManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * hitender
 * 6/27/20 12:24 PM
 */
class TokenQueueServiceTest {

    @Mock private TokenQueueManager tokenQueueManager;
    @Mock private FirebaseMessageService firebaseMessageService;
    @Mock private QueueManager queueManager;
    @Mock private AccountService accountService;
    @Mock private RegisteredDeviceManager registeredDeviceManager;
    @Mock private QueueManagerJDBC queueManagerJDBC;
    @Mock private StoreHourManager storeHourManager;
    @Mock private BizStoreManager bizStoreManager;
    @Mock private BusinessCustomerService businessCustomerService;
    @Mock private TextToSpeechService textToSpeechService;
    @Mock private ApiHealthService apiHealthService;

    private TokenQueueService tokenQueueService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        tokenQueueService = new TokenQueueService(
            tokenQueueManager,
            firebaseMessageService,
            queueManager,
            accountService,
            registeredDeviceManager,
            queueManagerJDBC,
            storeHourManager,
            bizStoreManager,
            businessCustomerService,
            textToSpeechService,
            apiHealthService
        );
    }

    @Test
    void computeExpectedServiceBeginTime() {
        StoreHourEntity storeHour = new StoreHourEntity(UUID.randomUUID().toString(), LocalDate.now().getDayOfWeek().getValue());
        storeHour.setStartHour(930)
            .setEndHour(1600)
            .setLunchTimeStart(1300)
            .setLunchTimeEnd(1400);

        for (int i = 1; i < 200; i++) {
            Date expectedServiceBegin = tokenQueueService.computeExpectedServiceBeginTime(
                300000,
                ZoneId.of("Asia/Calcutta"),
                storeHour,
                new TokenQueueEntity().setLastNumber(i).setCurrentlyServing(0)
            );

            System.out.println("Expected Service: " + expectedServiceBegin);
        }
    }
}
