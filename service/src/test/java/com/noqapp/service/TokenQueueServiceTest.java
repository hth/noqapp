package com.noqapp.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.exceptions.ExpectedServiceBeyondStoreClosingHour;
import com.noqapp.service.utils.ServiceUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
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
    @Mock private FirebaseService firebaseService;
    @Mock private ApiHealthService apiHealthService;

    private TokenQueueService tokenQueueService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        tokenQueueService = new TokenQueueService(
            1,
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
            firebaseService,
            apiHealthService
        );
    }

    @Test
    void computeExpectedServiceBeginTime() {
        BizStoreEntity bizStore = BizStoreEntity.newInstance().setBizName(BizNameEntity.newInstance(UUID.randomUUID().toString()).setBusinessName("Business Name"));
        when(bizStoreManager.getById(anyString())).thenReturn(bizStore);
        StoreHourEntity storeHour = new StoreHourEntity(UUID.randomUUID().toString(), LocalDate.now().getDayOfWeek().getValue());
        storeHour.setStartHour(930)
            .setEndHour(1600)
            .setLunchTimeStart(1300)
            .setLunchTimeEnd(1400);

        String[] basedOnTimeZones = {"America/New_York", "America/Los_Angeles", "Pacific/Honolulu"};
        for (String basedOnTimeZone : basedOnTimeZones) {
            ZoneId zone = ZoneId.of(basedOnTimeZone);

            Map<String, String> timeSlots = new LinkedHashMap<>();
            Assertions.assertThrows(ExpectedServiceBeyondStoreClosingHour.class, () -> {
                for (int i = 1; i < 100; i++) {
                    try {
                        ZonedDateTime expectedServiceBegin_UTC = tokenQueueService.computeExpectedServiceBeginTime(
                            300000,
                            zone,
                            storeHour,
                            i
                        );
                        String timeSlot = ServiceUtils.timeSlot(expectedServiceBegin_UTC, zone, storeHour);
                        timeSlots.put(String.valueOf(i), timeSlot);
                    } catch (ExpectedServiceBeyondStoreClosingHour e) {
                        System.err.println("Can service " + --i);
                        throw e;
                    }
                }
            });

            for (String key : timeSlots.keySet()) {
                System.out.println("Expected Service: for token " + key + ", time slot = " + timeSlots.get(key));
            }
        }
    }
}
