package com.noqapp.loader.scheduledtasks;

import static org.junit.Assert.assertNotEquals;

import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.loader.ITest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

/**
 * hitender
 * 9/25/20 11:37 AM
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArchiveAndResetITest extends ITest {

    private ArchiveAndReset archiveAndReset;

    @BeforeEach
    void setUp() {
        archiveAndReset = new ArchiveAndReset(
            "OFF",
            1,
            bizStoreManager,
            statsBizStoreDailyManager,
            queueManager,
            tokenQueueManager,
            queueManagerJDBC,
            statsCronService,
            bizService,
            computeNextRunService,
            purchaseOrderManager,
            purchaseOrderProductManager,
            purchaseOrderManagerJDBC,
            purchaseOrderProductManagerJDBC,
            fileService
        );
    }

    @Test
    void doArchiveAndReset_Success() {
        BizNameEntity bizName = bizService.findByPhone("9118000000041");
        List<BizStoreEntity> bizStores = bizService.getAllBizStores(bizName.getId());
        for (BizStoreEntity bizStore : bizStores) {
            bizStore.setQueueHistory(DateUtil.minusDays(bizStore.getQueueHistory(), 2))
                .setAvailableTokenCount(200)
                .setAverageServiceTime(Constants.MINUTES_IN_MILLISECOND);
            bizService.saveStore(bizStore, "Updated QH");
        }
        archiveAndReset.doArchiveAndReset();
        List<BizStoreEntity> bizStores_updated = bizService.getAllBizStores(bizName.getId());
        for (BizStoreEntity bizStore : bizStores_updated) {
            assertNotEquals(bizStore.getAverageServiceTime(), Constants.MINUTES_IN_MILLISECOND);
        }
    }
}
