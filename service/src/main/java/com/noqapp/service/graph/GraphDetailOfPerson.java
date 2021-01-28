package com.noqapp.service.graph;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.neo4j.AnomalyN4j;
import com.noqapp.domain.neo4j.BusinessCustomerN4j;
import com.noqapp.domain.neo4j.PersonN4j;
import com.noqapp.domain.neo4j.StoreN4j;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.repository.neo4j.AnomalyN4jManager;
import com.noqapp.repository.neo4j.BusinessCustomerN4jManager;
import com.noqapp.repository.neo4j.PersonN4jManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
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
    private AnomalyN4jManager anomalyN4jManager;

    private GraphQueue graphQueue;
    private GraphBusinessCustomer graphBusinessCustomer;

    public GraphDetailOfPerson(
        PersonN4jManager personN4jManager,
        BusinessCustomerN4jManager businessCustomerN4jManager,
        AnomalyN4jManager anomalyN4jManager,

        GraphQueue graphQueue,
        GraphBusinessCustomer graphBusinessCustomer
    ) {
        this.personN4jManager = personN4jManager;
        this.businessCustomerN4jManager = businessCustomerN4jManager;
        this.anomalyN4jManager = anomalyN4jManager;

        this.graphQueue = graphQueue;
        this.graphBusinessCustomer = graphBusinessCustomer;
    }

    @Mobile
    @Async
    public void graphPerson(String qid) {
        PersonN4j personN4j = personN4jManager.findByQid(qid);
        if (null == personN4j) {
            populateForQid(qid);
        } else if (24 < DateUtil.getHoursBetween(DateUtil.asLocalDateTime(personN4j.getLastAccessed()))) {
            if (null != personN4j.getAnomalyN4j()) {
                anomalyN4jManager.delete(personN4j.getAnomalyN4j());
            }
            personN4jManager.delete(personN4j);
            long deletedBusinessCustomerCount = businessCustomerN4jManager.deleteByQid(qid);
            LOG.info("Graph obsolete for qid={} deleted before re-creating {}", qid, deletedBusinessCustomerCount);

            populateForQid(qid);
        }
    }

    private void populateForQid(String qid) {
        graphQueue.graphUser(qid);
        graphBusinessCustomer.graphBusinessCustomer(qid);

        showResultFromGraphedPerson(qid);
    }

    private void showResultFromGraphedPerson(String qid) {
        PersonN4j personN4j = personN4jManager.findByQidWithQuery(qid, new Date());
        if (null == personN4j) {
            LOG.warn("No history found for qid={}", qid);
            return;
        }

        List<StoreN4j> storeN4js = personN4jManager.findAllStoreVisitedByQid(qid);
        Set<String> bizNameIds = personN4jManager.findAllBusinessVisitedByQid(qid);
        Collection<BusinessCustomerN4j> customerAssociatedToBusinesses = businessCustomerN4jManager.findCustomerRegisteredToAllBusiness(qid);
        boolean hasAnomaly = graphBusinessCustomer.hasDataAnomaly(qid, BusinessTypeEnum.CDQ);

        if (hasAnomaly) {
            LOG.warn("Data anomaly for person={} visits={} different stores that are owned by business={} of which customer is registered in business={} [{}]",
                personN4j.getQid(), storeN4js.size(),
                bizNameIds.size(),
                customerAssociatedToBusinesses.size(),
                customerAssociatedToBusinesses);

            AnomalyN4j anomalyN4j = new AnomalyN4j()
                .setQid(qid)
                .setBusinessType(BusinessTypeEnum.CDQ);
            anomalyN4jManager.save(anomalyN4j);

            personN4j.setAnomalyN4j(anomalyN4j);
            personN4jManager.save(personN4j);
        } else {
            LOG.info("Person={} visits={} different stores that are owned by business={} of which customer is registered in business={}",
                personN4j.getQid(), storeN4js.size(),
                bizNameIds.size(),
                customerAssociatedToBusinesses.size());
        }
    }
}
