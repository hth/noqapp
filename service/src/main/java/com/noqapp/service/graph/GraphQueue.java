package com.noqapp.service.graph;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.neo4j.PersonN4j;
import com.noqapp.domain.neo4j.StoreN4j;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.neo4j.PersonN4jManager;
import com.noqapp.repository.neo4j.StoreN4jManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 1/21/21 6:12 PM
 */
@Service
public class GraphQueue {

    private StoreN4jManager storeN4jManager;
    private PersonN4jManager personN4jManager;

    private QueueManager queueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private UserProfileManager userProfileManager;
    private BizStoreManager bizStoreManager;

    @Autowired
    public GraphQueue(
        StoreN4jManager storeN4jManager,
        PersonN4jManager personN4jManager,

        QueueManager queueManager,
        QueueManagerJDBC queueManagerJDBC,
        UserProfileManager userProfileManager,
        BizStoreManager bizStoreManager
    ) {
        this.storeN4jManager = storeN4jManager;
        this.personN4jManager = personN4jManager;
        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.userProfileManager = userProfileManager;
        this.bizStoreManager = bizStoreManager;
    }

    public void graphUser(String qid) {
        List<QueueEntity> queues = queueManager.findAllQueuedByQid(qid);
        List<QueueEntity> queueHistory = queueManagerJDBC.getByQid(qid);
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);

        graphQueue(queues, userProfile);
        graphQueue(queueHistory, userProfile);
    }

    private void graphQueue(List<QueueEntity> queues, UserProfileEntity userProfile) {
        for (QueueEntity queue : queues) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(queue.getCodeQR());
            if (null != bizStore) {
                StoreN4j storeN4j = StoreN4j.populate(bizStore);
                storeN4jManager.save(storeN4j);
                queue.setCustomerName(userProfile.getName());
                personN4jManager.save(PersonN4j.populate(queue, storeN4j));
            }
        }
    }
}
