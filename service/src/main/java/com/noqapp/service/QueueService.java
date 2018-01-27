package com.noqapp.service;

import java.util.List;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 8/27/17 11:49 AM
 */
@Service
public class QueueService {
    private static final Logger LOG = LoggerFactory.getLogger(QueueService.class);

    private QueueManager queueManager;
    private QueueManagerJDBC queueManagerJDBC;

    @Autowired
    public QueueService(QueueManager queueManager, QueueManagerJDBC queueManagerJDBC) {
        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
    }

    @Mobile
    public List<QueueEntity> findAllQueuedByQid(String qid) {
        return queueManager.findAllQueuedByQid(qid);
    }

    @Mobile
    public List<QueueEntity> findAllNotQueuedByQid(String qid) {
        return queueManager.findAllNotQueuedByQid(qid);
    }

    @Mobile
    public List<QueueEntity> findAllNotQueuedByDid(String did) {
        return queueManager.findAllNotQueuedByDid(did);
    }

    @Mobile
    public List<QueueEntity> getByQid(String qid) {
        return queueManagerJDBC.getByQid(qid);
    }

    @Mobile
    public List<QueueEntity> getByDid(String did) {
        return queueManagerJDBC.getByDid(did);
    }

    public List<QueueEntity> findAllHistoricalQueue(String qid) {
        List<QueueEntity> queues = findAllNotQueuedByQid(qid);
        queues.addAll(getByQid(qid));
        return queues;
    }

    public long deleteByCodeQR(String codeQR) {
        return queueManager.deleteByCodeQR(codeQR);
    }

    public long getPreviouslyVisitedClientCount(String codeQR) {
        return queueManager.previouslyVisitedClientCount(codeQR);
    }

    public long getNewVisitClientCount(String codeQR) {
        return queueManager.newVisitClientCount(codeQR);
    }
}
