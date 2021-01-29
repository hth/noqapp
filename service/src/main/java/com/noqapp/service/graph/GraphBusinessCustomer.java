package com.noqapp.service.graph;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.neo4j.BizNameN4j;
import com.noqapp.domain.neo4j.BusinessCustomerN4j;
import com.noqapp.domain.neo4j.PersonN4j;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BusinessCustomerManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.neo4j.BizNameN4jManager;
import com.noqapp.repository.neo4j.BusinessCustomerN4jManager;
import com.noqapp.repository.neo4j.PersonN4jManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * hitender
 * 1/21/21 1:07 AM
 */
@Service
public class GraphBusinessCustomer {
    private static final Logger LOG = LoggerFactory.getLogger(GraphBusinessCustomer.class);

    private PersonN4jManager personN4jManager;
    private BizNameN4jManager bizNameN4jManager;
    private BusinessCustomerN4jManager businessCustomerN4jManager;

    private BusinessCustomerManager businessCustomerManager;
    private BizNameManager bizNameManager;
    private UserProfileManager userProfileManager;

    @Autowired
    public GraphBusinessCustomer(
        PersonN4jManager personN4jManager,
        BizNameN4jManager bizNameN4jManager,
        BusinessCustomerN4jManager businessCustomerN4jManager,

        BusinessCustomerManager businessCustomerManager,
        BizNameManager bizNameManager,
        UserProfileManager userProfileManager
    ) {
        this.personN4jManager = personN4jManager;
        this.bizNameN4jManager = bizNameN4jManager;
        this.businessCustomerN4jManager = businessCustomerN4jManager;

        this.businessCustomerManager = businessCustomerManager;
        this.bizNameManager = bizNameManager;
        this.userProfileManager = userProfileManager;
    }

    @Async
    void graphBusinessCustomer(String qid) {
        PersonN4j personN4j = personN4jManager.findByQid(qid);
        List<BusinessCustomerEntity> businessCustomers = businessCustomerManager.findAll(qid);
        for (BusinessCustomerEntity businessCustomer : businessCustomers) {
            BizNameEntity bizName = bizNameManager.getById(businessCustomer.getBizNameId());
            BizNameN4j bizNameN4j = new BizNameN4j()
                .setId(bizName.getId())
                .setCodeQR(bizName.getCodeQR())
                .setBusinessName(bizName.getBusinessName())
                .setBusinessType(bizName.getBusinessType());
            bizNameN4jManager.save(bizNameN4j);

            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
            BusinessCustomerN4j businessCustomerN4j = new BusinessCustomerN4j()
                .setBizNameN4j(bizNameN4j)
                .setName(userProfile.getName())
                .setBusinessCustomerId(businessCustomer.getBusinessCustomerId())
                .setQid(businessCustomer.getQueueUserId())
                .setLastAccessed(new Date());
            businessCustomerN4jManager.save(businessCustomerN4j);

            if (null != personN4j) {
                personN4j.setBusinessCustomerN4j(businessCustomerN4j);
                personN4jManager.save(personN4j);
            }
        }
    }

    /** Logs specific anomaly associated to business type. */
    public boolean hasDataAnomaly(String qid) {
        Map<BusinessTypeEnum, Set<String>> numberOfCustomerIds = changeBusinessCustomerToMap(businessCustomerN4jManager.findCustomerRegisteredToAllBusiness(qid));

        for (BusinessTypeEnum businessType : numberOfCustomerIds.keySet()) {
            switch (businessType) {
                case CD:
                case CDQ:
                    if (hasRuleValidationFailedForCDQ(qid, numberOfCustomerIds)) {
                        return true;
                    }

                    break;
                default:
                    //Do nothing
            }
        }

        return false;
    }

    /** Logs specific anomaly associated to business type. */
    public boolean hasDataAnomaly(String qid, BusinessTypeEnum businessType) {
        Collection<BusinessCustomerN4j> businessCustomerN4js = businessCustomerN4jManager.findCustomerRegisteredToSpecificBusinessType(qid, businessType);
        return hasRuleValidationFailedForCDQ(qid, changeBusinessCustomerToMap(businessCustomerN4js));
    }

    private boolean hasRuleValidationFailedForCDQ(String qid, Map<BusinessTypeEnum, Set<String>> numberOfCustomerIds) {
        Set<String> ids = numberOfCustomerIds.get(BusinessTypeEnum.CDQ);
        if (null == ids) {
            return false;
        }

        if (2 < ids.size()) {
            LOG.error("Data anomaly in businessType={} qid={} ids={}", BusinessTypeEnum.CDQ, qid, numberOfCustomerIds.get(BusinessTypeEnum.CDQ));
            return true;
        }

        long liquorCard = ids.stream().filter(id -> id.startsWith("L")).count();
        long groceryCard = ids.stream().filter(id -> id.startsWith("G")).count();

        /* Not more than one liquor card per account. */
        if (1 < liquorCard) {
            LOG.warn("Data anomaly in size businessType={} qid={} ids={}", BusinessTypeEnum.CDQ, qid, numberOfCustomerIds.get(BusinessTypeEnum.CDQ));
            return true;
        }

        /* Not more than one grocery card per account. */
        if (1 < groceryCard) {
            LOG.warn("Data anomaly in size businessType={} qid={} ids={}", BusinessTypeEnum.CDQ, qid, numberOfCustomerIds.get(BusinessTypeEnum.CDQ));
            return true;
        }

        return false;
    }

    /** Populate Map with BusinessType and set of customerIds. */
    private Map<BusinessTypeEnum, Set<String>> changeBusinessCustomerToMap(Collection<BusinessCustomerN4j> businessCustomerN4js) {
        return businessCustomerN4js.stream()
            .collect(
                Collectors.groupingBy(
                    businessCustomerN4j -> businessCustomerN4j.getBizNameN4j().getBusinessType(),
                    HashMap::new, Collectors.mapping(BusinessCustomerN4j::getBusinessCustomerId, Collectors.toSet())));
    }
}
