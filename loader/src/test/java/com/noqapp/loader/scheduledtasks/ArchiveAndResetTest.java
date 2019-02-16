package com.noqapp.loader.scheduledtasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.PurchaseOrderProductManagerJDBC;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.ScheduledTaskManager;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.BizService;
import com.noqapp.service.StatsCronService;

import org.joda.time.DateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2019-02-15 14:54
 */
class ArchiveAndResetTest {
    private ArchiveAndReset archiveAndReset;

    @Mock private BizStoreManager bizStoreManager;
    @Mock private StatsBizStoreDailyManager statsBizStoreDailyManager;
    @Mock private QueueManager queueManager;
    @Mock private TokenQueueManager tokenQueueManager;
    @Mock private QueueManagerJDBC queueManagerJDBC;
    @Mock private StatsCronService statsCronService;
    @Mock private BizService bizService;
    @Mock private ScheduledTaskManager scheduledTaskManager;
    @Mock private PurchaseOrderManager purchaseOrderManager;
    @Mock private PurchaseOrderProductManager purchaseOrderProductManager;
    @Mock private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    @Mock private PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;

    private String codeQR = CommonUtil.generateHexFromObjectId();
    private String bizStoreId = CommonUtil.generateHexFromObjectId();
    private String bizNameId = CommonUtil.generateHexFromObjectId();

    private List<QueueEntity> queues = null;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        archiveAndReset = new ArchiveAndReset(
            "OFF",
            0,
            bizStoreManager,
            statsBizStoreDailyManager,
            queueManager,
            tokenQueueManager,
            queueManagerJDBC,
            statsCronService,
            bizService,
            scheduledTaskManager,
            purchaseOrderManager,
            purchaseOrderProductManager,
            purchaseOrderManagerJDBC,
            purchaseOrderProductManagerJDBC
        );

        QueueEntity a = new QueueEntity();
        a.setQueueUserState(QueueUserStateEnum.S);
        a.setServiceBeginTime(DateUtil.now().minusMinutes(30).toDate());
        a.setServiceEndTime(DateUtil.now().minusMinutes(20).toDate());

        QueueEntity b = new QueueEntity();
        b.setQueueUserState(QueueUserStateEnum.S);
        b.setServiceBeginTime(DateUtil.now().minusMinutes(20).toDate());
        b.setServiceEndTime(DateUtil.now().minusMinutes(10).toDate());

        QueueEntity c = new QueueEntity();
        c.setQueueUserState(QueueUserStateEnum.S);
        c.setServiceBeginTime(DateUtil.now().minusMinutes(10).toDate());
        c.setServiceEndTime(DateUtil.now().minusMinutes(0).toDate());

        queues = new LinkedList<QueueEntity>() {{
            add(a);
            add(b);
            add(c);
        }};
    }

    @Test
    void computeBeginAndEndTimeOfService() {
        StatsBizStoreDailyEntity statsBizStoreDaily = new StatsBizStoreDailyEntity();
        statsBizStoreDaily.setBizStoreId(bizStoreId);
        statsBizStoreDaily.setBizNameId(bizNameId);
        statsBizStoreDaily.setCodeQR(codeQR);

        DateTime last60Minutes = DateUtil.now().minusMinutes(60);
        List<StoreHourEntity> storeHours = new ArrayList<StoreHourEntity>() {{
            for(int i = 1; i < 8; i ++) {
                add(new StoreHourEntity(bizStoreId, i)
                    .setStartHour(last60Minutes.getHourOfDay())
                    .setEndHour(last60Minutes.getHourOfDay() + 60)
                    .setTokenAvailableFrom(last60Minutes.getHourOfDay())
                    .setTokenNotAvailableFrom(last60Minutes.getHourOfDay() + 60));
            }
        }};

        BizStoreEntity bizStore = BizStoreEntity.newInstance();
        bizStore.setCodeQR(codeQR);
        bizStore.setStoreHours(storeHours);
        bizStore.setId(bizStoreId);
        bizStore.setTimeZone(ZoneOffset.UTC.getId());
        when(bizStoreManager.findByCodeQR(anyString())).thenReturn(bizStore);

        archiveAndReset.computeBeginAndEndTimeOfService(
            queues,
            statsBizStoreDaily
        );

        LocalTime lastTime = LocalTime.parse(statsBizStoreDaily.getLastServicedOrSkipped(), DateTimeFormatter.ofPattern("HHmm"));
        LocalTime firstTime = LocalTime.parse(statsBizStoreDaily.getFirstServicedOrSkipped(), DateTimeFormatter.ofPattern("HHmm"));
        assertEquals(30, ChronoUnit.MINUTES.between(firstTime, lastTime));
    }
}
