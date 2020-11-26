package com.noqapp.service;

import static com.noqapp.common.utils.DateUtil.MINUTES_IN_MILLISECONDS;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.helper.QueueSupervisor;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.repository.QueueManager;
import com.noqapp.service.utils.ServiceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.servlet.Registration;

/**
 * hitender
 * 11/25/20 4:39 PM
 */
@DisplayName("JoinAbort API")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("api")
class JoinAbortServiceITest extends ITest {
    private static final Logger LOG = LoggerFactory.getLogger(JoinAbortServiceITest.class);

    private JoinAbortService joinAbortService;

    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        joinAbortService = new JoinAbortService(
            2,
            deviceService,
            tokenQueueService,
            purchaseOrderService,
            queueManager,
            purchaseOrderProductService,
            bizService,
            businessCustomerService,
            firebaseMessageService,
            storeHourService
        );
    }

    @Test
    void abort() throws JsonProcessingException {
        BizNameEntity bizName = bizService.findByPhone("9118000000041");
        BizStoreEntity bizStore = bizService.findOneBizStore(bizName.getId());
        bizStore.setAverageServiceTime(600000).setAvailableTokenCount(20);
        bizStore.setTimeZone("America/New_York");
        bizService.saveStore(bizStore, "Changed AST");

        StoreHourEntity storeHour = storeHourService.getStoreHours(bizStore.getCodeQR(), bizStore);
        storeHour.setStartHour(930)
            .setEndHour(1600)
            .setLunchTimeStart(1300)
            .setLunchTimeEnd(1400)
            .setTokenAvailableFrom(100)
            .setTokenNotAvailableFrom(1930)
            .setDayClosed(false)
            .setTempDayClosed(false)
            .setPreventJoining(false);
        storeHourManager.save(storeHour);
        long averageServiceTime = ServiceUtils.computeAverageServiceTime(storeHour, bizStore.getAvailableTokenCount());
        bizService.updateStoreTokenAndServiceTime(bizStore.getCodeQR(), averageServiceTime, bizStore.getAvailableTokenCount());

        List<String> mails = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            String phone = "+91" + StringUtils.leftPad(String.valueOf(i), 10, '0');
            String name = RandomString.newInstance(6).nextString().toLowerCase();

            accountService.createNewAccount(
                phone,
                name,
                "",
                name + "@r.com",
                "2000-12-12",
                GenderEnum.M,
                "IN",
                "Asia/Calcutta",
                "password",
                "",
                true,
                false
            );

            mails.add(name + "@r.com");
        }

        Map<String, String> display = new LinkedHashMap<>();
        for (String mail : mails) {
            UserAccountEntity userAccount = userAccountManager.findByUserId(mail);

            businessCustomerService.addAuthorizedUserForDoingBusiness("G" + StringUtils.leftPad(String.valueOf(userAccount.getQueueUserId()), 18, '0'), bizStore.getBizName().getId(), userAccount.getQueueUserId());
            businessCustomerService.addAuthorizedUserForDoingBusiness("L" + StringUtils.leftPad(String.valueOf(userAccount.getQueueUserId()), 18, '0'), bizStore.getBizName().getId(), userAccount.getQueueUserId());

            String jsonToken_String = joinAbortService.joinQueue(
                bizStore.getCodeQR(),
                UUID.randomUUID().toString(),
                userAccount.getQueueUserId(),
                null,
                bizStore.getAverageServiceTime(),
                TokenServiceEnum.C
            ).asJson();

            //{"error":{"reason":"CSD Liquor for Ex-Servicemen has not started. Please correct time on your device.","systemErrorCode":"4071","systemError":"DEVICE_TIMEZONE_OFF"}}
            //{"error":{"reason":"CSD Liquor for Ex-Servicemen token limit for the day has reached.","systemErrorCode":"4309","systemError":"QUEUE_TOKEN_LIMIT"}}
            JsonToken jsonToken = new ObjectMapper().readValue(jsonToken_String, JsonToken.class);
            if (0 != jsonToken.getToken()) {
                display.put(String.valueOf(jsonToken.getToken()), jsonToken.getTimeSlotMessage());
                LOG.info("Joined queue {} : {}", jsonToken.getToken(), jsonToken);
            }
        }

        Map<String, Integer> count = new HashMap<>();
        for (String key : display.keySet()) {
            LOG.info("{} {}", key, display.get(key));

            if (count.containsKey(display.get(key))) {
                count.put(display.get(key), count.get(display.get(key)) + 1);
            } else {
                count.put(display.get(key), 1);
            }
        }

        for (String key : count.keySet()) {
            System.out.println(key + " " + count.get(key));
        }
        System.out.println("averageServiceTime=" + new BigDecimal(averageServiceTime).divide(new BigDecimal(MINUTES_IN_MILLISECONDS), MathContext.DECIMAL64) + " minutes per user");
        assertEquals(20, display.size(), "Number of token issued must be equal");

        JsonQueuePersonList jsonQueuePersonListBefore = queueService.findAllClient(bizStore.getCodeQR());
        for (JsonQueuedPerson jsonQueuedPerson : jsonQueuePersonListBefore.getQueuedPeople()) {
            LOG.info("Before {} {}", jsonQueuedPerson.getToken(), jsonQueuedPerson.getTimeSlotMessage());
        }

        QueueEntity queue1 = queueManager.findOne(bizStore.getCodeQR(), 6);
        joinAbortService.abort(queue1.getId(), bizStore.getCodeQR());

        QueueEntity queue2 = queueManager.findOne(bizStore.getCodeQR(), 10);
        joinAbortService.abort(queue2.getId(), bizStore.getCodeQR());

        QueueEntity queue3 = queueManager.findOne(bizStore.getCodeQR(), 12);
        joinAbortService.abort(queue3.getId(), bizStore.getCodeQR());

        QueueEntity queue4 = queueManager.findOne(bizStore.getCodeQR(), 13);
        joinAbortService.abort(queue4.getId(), bizStore.getCodeQR());

        QueueEntity queue5 = queueManager.findOne(bizStore.getCodeQR(), 14);
        joinAbortService.abort(queue5.getId(), bizStore.getCodeQR());

        await().atMost(2, MINUTES).until(isTheAvailableTokenIncreased(bizStore.getCodeQR()));

        List<String> mailAdditional = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            String phone = "+91" + StringUtils.leftPad(String.valueOf(i+21), 10, '0');
            String name = RandomString.newInstance(6).nextString().toLowerCase();

            accountService.createNewAccount(
                phone,
                name,
                "",
                name + "@r.com",
                "2000-12-12",
                GenderEnum.M,
                "IN",
                "Asia/Calcutta",
                "password",
                "",
                true,
                false
            );

            mailAdditional.add(name + "@r.com");
        }

        for (String mail : mailAdditional) {
            UserAccountEntity userAccount = userAccountManager.findByUserId(mail);

            businessCustomerService.addAuthorizedUserForDoingBusiness("G" + StringUtils.leftPad(String.valueOf(userAccount.getQueueUserId()), 18, '0'), bizStore.getBizName().getId(), userAccount.getQueueUserId());
            businessCustomerService.addAuthorizedUserForDoingBusiness("L" + StringUtils.leftPad(String.valueOf(userAccount.getQueueUserId()), 18, '0'), bizStore.getBizName().getId(), userAccount.getQueueUserId());

            String jsonToken_String = joinAbortService.joinQueue(
                bizStore.getCodeQR(),
                UUID.randomUUID().toString(),
                userAccount.getQueueUserId(),
                null,
                bizStore.getAverageServiceTime(),
                TokenServiceEnum.C
            ).asJson();

            //{"error":{"reason":"CSD Liquor for Ex-Servicemen has not started. Please correct time on your device.","systemErrorCode":"4071","systemError":"DEVICE_TIMEZONE_OFF"}}
            //{"error":{"reason":"CSD Liquor for Ex-Servicemen token limit for the day has reached.","systemErrorCode":"4309","systemError":"QUEUE_TOKEN_LIMIT"}}
            JsonToken jsonToken = new ObjectMapper().readValue(jsonToken_String, JsonToken.class);
            if (0 != jsonToken.getToken()) {
                display.put(String.valueOf(jsonToken.getToken()), jsonToken.getTimeSlotMessage());
                LOG.info("Joined queue {} : {}", jsonToken.getToken(), jsonToken);
            }
        }

        JsonQueuePersonList jsonQueuePersonList = queueService.findAllClient(bizStore.getCodeQR());
        display("After" , jsonQueuePersonList);
        display("Before", jsonQueuePersonListBefore);
    }

    private void display(String label, JsonQueuePersonList jsonQueuePersonList) {
        List<JsonQueuedPerson> jsonQueuedPeoples = jsonQueuePersonList.getQueuedPeople();
        jsonQueuedPeoples.sort(comparing(JsonQueuedPerson::getToken));
        for (JsonQueuedPerson jsonQueuedPerson : jsonQueuedPeoples) {
            LOG.info("{} {} {} {}", label, jsonQueuedPerson.getQueueUserState(), jsonQueuedPerson.getToken(), jsonQueuedPerson.getTimeSlotMessage());
        }
    }

    private Callable<Boolean> isTheAvailableTokenIncreased(String codeQR) {
        return () -> {
            return bizService.findByCodeQR(codeQR).realAvailableToken() > 20; // The condition that must be fulfilled
        };
    }
}
