package com.noqapp.service.graph;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.neo4j.LocationN4j;
import com.noqapp.domain.neo4j.PersonN4j;
import com.noqapp.domain.neo4j.StoreN4j;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.InviteManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.neo4j.PersonN4jManager;
import com.noqapp.repository.neo4j.StoreN4jManager;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private InviteManager inviteManager;

    @Autowired
    public GraphQueue(
        StoreN4jManager storeN4jManager,
        PersonN4jManager personN4jManager,

        QueueManager queueManager,
        QueueManagerJDBC queueManagerJDBC,
        UserProfileManager userProfileManager,
        BizStoreManager bizStoreManager,
        InviteManager inviteManager
    ) {
        this.storeN4jManager = storeN4jManager;
        this.personN4jManager = personN4jManager;

        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.userProfileManager = userProfileManager;
        this.bizStoreManager = bizStoreManager;
        this.inviteManager = inviteManager;
    }

    public PersonN4j graphUser(String qid) {
        List<QueueEntity> queues = queueManager.findAllQueuedByQid(qid);
        List<QueueEntity> queueHistory = queueManagerJDBC.getByQid(qid);
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);

        PersonN4j personN4j = new PersonN4j()
            .setQid(qid)
            .setName(userProfile.getName())
            .setLastAccessed(new Date())
            .setPoints(inviteManager.computePoints(qid));

        String codeQR = queueManagerJDBC.clientLatestVisit(qid);
        if (StringUtils.isNotBlank(codeQR)) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
            personN4j
                .setBizNameId(bizStore.getBizName().getId())
                .setStoreCodeQR(bizStore.getCodeQR())
                .setLongitude(bizStore.getCoordinate()[0])
                .setLatitude(bizStore.getCoordinate()[1]);
        }
        personN4jManager.save(personN4j);

        graphQueue(queues, personN4j);
        graphQueue(queueHistory, personN4j);

        return personN4j;
    }

    private void graphQueue(List<QueueEntity> queues, PersonN4j personN4j) {
        for (QueueEntity queue : queues) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(queue.getCodeQR());
            if (null != bizStore) {
                StoreN4j storeN4j = StoreN4j.populate(bizStore);
                storeN4jManager.save(storeN4j);

                personN4j.addStoreN4j(storeN4j);
            }
        }

        if (!queues.isEmpty()) {
            personN4jManager.save(personN4j);
        }
    }
}
