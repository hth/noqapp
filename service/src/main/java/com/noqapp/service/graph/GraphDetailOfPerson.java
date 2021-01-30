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
        try {
            LOG.info("Graphing for qid={}", qid);
            PersonN4j personN4j = personN4jManager.findByQidWithQuery(qid, new Date());
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
        } catch (Exception e) {
            LOG.error("Failed graphing qid={} reason={}", qid, e.getLocalizedMessage(), e);
        }
    }

    private void populateForQid(String qid) {
        PersonN4j personN4j = graphQueue.graphUser(qid);
        graphBusinessCustomer.graphBusinessCustomer(personN4j);

        showResultFromGraphedPerson(personN4j);
    }

    private void showResultFromGraphedPerson(PersonN4j personN4j) {
        if (null == personN4j.getStoreN4j()) {
            LOG.debug("No history found for qid={}", personN4j.getQid());
            return;
        }

        List<StoreN4j> storeN4js = personN4jManager.findAllStoreVisitedByQid(personN4j.getQid());
        Set<String> bizNameIds = personN4jManager.findAllBusinessVisitedByQid(personN4j.getQid());
        Collection<BusinessCustomerN4j> customerAssociatedToBusinesses = businessCustomerN4jManager.findCustomerRegisteredToAllBusiness(personN4j.getQid());
        boolean hasAnomaly = graphBusinessCustomer.hasDataAnomaly(personN4j.getQid(), BusinessTypeEnum.CDQ);

        if (hasAnomaly) {
            LOG.warn("Data anomaly for person={} visits={} different stores that are owned by business={} of which customer is registered in business={} {}",
                personN4j.getQid(), storeN4js.size(),
                bizNameIds.size(),
                customerAssociatedToBusinesses.size(),
                customerAssociatedToBusinesses);

            for (BusinessCustomerN4j businessCustomerN4j : customerAssociatedToBusinesses) {
                AnomalyN4j anomalyN4j = new AnomalyN4j()
                    .setQid(personN4j.getQid())
                    .setBusinessCustomerN4j(businessCustomerN4j);
                anomalyN4jManager.save(anomalyN4j);

                personN4j.setAnomalyN4j(anomalyN4j);
                personN4jManager.save(personN4j);
            }
        } else {
            LOG.info("No anomaly for person={} visits={} different stores that are owned by business={} of which customer is registered in business={}",
                personN4j.getQid(), storeN4js.size(),
                bizNameIds.size(),
                customerAssociatedToBusinesses.size());
        }
    }
}
