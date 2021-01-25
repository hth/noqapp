package com.noqapp.service.graph;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.neo4j.BusinessCustomerN4j;
import com.noqapp.domain.neo4j.PersonN4j;
import com.noqapp.domain.neo4j.StoreN4j;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.neo4j.BusinessCustomerN4jManager;
import com.noqapp.repository.neo4j.PersonN4jManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * hitender
 * 1/19/21 6:31 PM
 */
@Service
public class GraphDetailOfPerson {
    private static final Logger LOG = LoggerFactory.getLogger(GraphDetailOfPerson.class);

    private PersonN4jManager personN4jManager;
    private BusinessCustomerN4jManager businessCustomerN4jManager;

    private GraphQueue graphQueue;
    private GraphBusinessCustomer graphBusinessCustomer;

    private UserProfileManager userProfileManager;

    public GraphDetailOfPerson(
        PersonN4jManager personN4jManager,
        BusinessCustomerN4jManager businessCustomerN4jManager,

        GraphQueue graphQueue,
        GraphBusinessCustomer graphBusinessCustomer,

        UserProfileManager userProfileManager
    ) {
        this.personN4jManager = personN4jManager;
        this.businessCustomerN4jManager = businessCustomerN4jManager;
        this.graphQueue = graphQueue;
        this.graphBusinessCustomer = graphBusinessCustomer;
        this.userProfileManager = userProfileManager;
    }

    @Mobile
    @Async
    public void graphPerson(String qid) {
        graphQueue.graphUser(qid);
        graphBusinessCustomer.graphBusinessCustomer(qid);

        showResultFromGraphedPerson(qid);
    }

    private void showResultFromGraphedPerson(String qid) {
        PersonN4j personN4j = personN4jManager.findByQidWithQuery(qid);
        if (null == personN4j) {
            LOG.warn("No history found for qid={}", qid);
            return;
        }

        List<StoreN4j> storeN4js = personN4jManager.findAllStoreVisitedByQid(qid);
        Set<String> bizNameIds = personN4jManager.findAllBusinessVisitedByQid(qid);
        Collection<BusinessCustomerN4j> customerAssociatedToBusinesses = businessCustomerN4jManager.findCustomerRegisteredToAllBusiness(qid);
        boolean hasAnomaly = graphBusinessCustomer.hasDataAnomaly(qid, BusinessTypeEnum.CDQ);

        LOG.info("Person={} visits={} different stores " +
                "\n that are owned by business={}" +
                "\n of which customer is registered in business={}" +
                "\n found any data anomaly {}",
            personN4j.getQid(), storeN4js.size(),
            bizNameIds.size(),
            customerAssociatedToBusinesses.size(),
            hasAnomaly);
    }
}
