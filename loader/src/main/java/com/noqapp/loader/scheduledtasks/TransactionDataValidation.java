package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.QueueManagerJDBC;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-03 08:48
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class TransactionDataValidation {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionDataValidation.class);

    private QueueManagerJDBC queueManagerJDBC;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;

    @Autowired
    public TransactionDataValidation(
        QueueManagerJDBC queueManagerJDBC,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC
    ) {
        this.queueManagerJDBC = queueManagerJDBC;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
    }

    @Scheduled(fixedDelayString = "${loader.BusinessStatsMail.businessStatusMail}")
    public void validateQueueTransactionData() {
        List<String> anomalies = new LinkedList<>();
        Date since = DateUtil.minusDays(5);
        List<QueueEntity> queues = queueManagerJDBC.findAfterCreateDate(since);
        for (QueueEntity queue : queues) {
            try {
                if (StringUtils.isNotBlank(queue.getTransactionId())) {
                    PurchaseOrderEntity purchaseOrder = purchaseOrderManagerJDBC.findOrderByTransactionId(queue.getQueueUserId(), queue.getTransactionId());
                    if (null == purchaseOrder) {
                        purchaseOrder = purchaseOrderManagerJDBC.findOrderByTransactionId(queue.getTransactionId());
                        LOG.error("{} qid {} and queue qid {}", queue.getTransactionId(), purchaseOrder.getQueueUserId(), queue.getQueueUserId());
                        anomalies.add(queue.getTransactionId() + " :Found: " + purchaseOrder.getQueueUserId() + " :Expected: " + queue.getQueueUserId());
                    }
                }
            } catch (Exception e) {
                LOG.error("On validating queue transaction data {}", e.getLocalizedMessage(), e);
                anomalies.add(queue.getTransactionId() + " :Found: :Expected: " + queue.getQueueUserId());
            }
        }

        for (String found : anomalies) {
            LOG.error("{}", found);
        }
        LOG.info("Found anomalies {}", anomalies.size());
    }
}
