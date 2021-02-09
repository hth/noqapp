package com.noqapp.service.graph;

import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.neo4j.AnomalyN4j;
import com.noqapp.domain.neo4j.BusinessCustomerN4j;
import com.noqapp.domain.neo4j.PersonN4j;
import com.noqapp.domain.neo4j.StoreN4j;
import com.noqapp.domain.neo4j.queryresult.BusinessDistanceFromUserLocation;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.neo4j.AnomalyN4jManager;
import com.noqapp.repository.neo4j.BusinessCustomerN4jManager;
import com.noqapp.repository.neo4j.PersonN4jManager;
import com.noqapp.service.BizService;

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

    private BizService bizService;
    private QueueManagerJDBC queueManagerJDBC;

    public GraphDetailOfPerson(
        PersonN4jManager personN4jManager,
        BusinessCustomerN4jManager businessCustomerN4jManager,
        AnomalyN4jManager anomalyN4jManager,

        GraphQueue graphQueue,
        GraphBusinessCustomer graphBusinessCustomer,

        BizService bizService,
        QueueManagerJDBC queueManagerJDBC
    ) {
        this.personN4jManager = personN4jManager;
        this.businessCustomerN4jManager = businessCustomerN4jManager;
        this.anomalyN4jManager = anomalyN4jManager;

        this.graphQueue = graphQueue;
        this.graphBusinessCustomer = graphBusinessCustomer;

        this.bizService = bizService;
        this.queueManagerJDBC = queueManagerJDBC;
    }

    @Mobile
    @Async
    public void graphPerson(String qid) {
        try {
            PersonN4j personN4j = personN4jManager.findByQidWithQuery(qid, new Date());
            if (null == personN4j) {
                LOG.info("Graphing for qid={}", qid);
                populateForQid(qid);
            } else if (24 < DateUtil.getHoursBetween(DateUtil.asLocalDateTime(personN4j.getLastAccessed()))) {
                long count = personN4jManager.detachAndDelete(qid);
                LOG.info("Graph obsolete for qid={} deleted {} before re-creating", qid, count);
                populateForQid(qid);
            }
        } catch (Exception e) {
            LOG.error("Failed graphing qid={} reason={}", qid, e.getLocalizedMessage(), e);
        }
    }

    private void populateForQid(String qid) {
        PersonN4j personN4j = graphQueue.graphUser(qid);
        graphBusinessCustomer.graphBusinessCustomer(personN4j);

        if (personN4j.getStoreN4js().isEmpty()) {
            LOG.debug("No history found for qid={}", personN4j.getQid());
            return;
        }

        runAnalysis(personN4j);
    }

    private void runAnalysis(PersonN4j personN4j) {
        String logMe = checkForAnomaly(personN4j);
        BusinessDistanceFromUserLocation businessDistanceFromUserLocation = findBusinessVisitedThatIsDeemedTooFar(personN4j);
        if (null != businessDistanceFromUserLocation) {
            BizNameEntity bizName = bizService.getByBizNameId(businessDistanceFromUserLocation.getBizNameId());
            QueueEntity queue = queueManagerJDBC.findClientVisitedLatestStore(personN4j.getBizNameId(), personN4j.getQid());
            logMe += String.format("Moved qid=%s found bizNameId=%s bizName=%s lastVisited=%s", personN4j.getQid(), personN4j.getBizNameId(), bizName.getBusinessName(), queue.getCreated());
        }

        LOG.info("{}", logMe);
    }

    /** Check anomaly in data created by user which has broken business rule. */
    private String checkForAnomaly(PersonN4j personN4j) {
        List<StoreN4j> storeN4js = personN4jManager.findAllStoreVisitedByQid(personN4j.getQid());
        Set<String> bizNameIds = personN4jManager.findAllBusinessVisitedByQid(personN4j.getQid());
        Collection<BusinessCustomerN4j> customerAssociatedToBusinesses = businessCustomerN4jManager.findCustomerRegisteredToAllBusiness(personN4j.getQid());
        boolean hasAnomaly = graphBusinessCustomer.hasDataAnomaly(personN4j.getQid(), BusinessTypeEnum.CDQ);

        String logMe;
        if (hasAnomaly) {
            logMe = String.format("Data anomaly for person=%s visits=%s different stores that are owned by business=%s of which customer is registered in business=%s %s;",
                personN4j.getQid(), storeN4js.size(),
                bizNameIds.size(),
                customerAssociatedToBusinesses.size(),
                customerAssociatedToBusinesses);

            AnomalyN4j anomalyN4j = new AnomalyN4j().setQid(personN4j.getQid());
            anomalyN4jManager.save(anomalyN4j);

            personN4j.setAnomalyN4j(anomalyN4j);
            personN4jManager.save(personN4j);
        } else {
            logMe = String.format("No anomaly for person=%s visits=%s different stores that are owned by business=%s of which customer is registered in business=%s;",
                personN4j.getQid(), storeN4js.size(),
                bizNameIds.size(),
                customerAssociatedToBusinesses.size());
        }

        return logMe;
    }

    /** Figures out if user has moved. */
    private BusinessDistanceFromUserLocation findBusinessVisitedThatIsDeemedTooFar(PersonN4j personN4j) {
        Set<BusinessDistanceFromUserLocation> businessDistanceFromUserLocations = personN4jManager.visitedBusinessDistance(personN4j.getQid());
        return businessDistanceFromUserLocations.stream().filter(x -> x.getTravelDistance() > Constants.HUNDRED_KMS_IN_METERS).findAny().orElse(null);
    }
}
