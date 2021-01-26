package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.repository.neo4j.BizNameN4jManager;
import com.noqapp.repository.neo4j.BusinessCustomerN4jManager;
import com.noqapp.repository.neo4j.PersonN4jManager;
import com.noqapp.repository.neo4j.StoreN4jManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 1/25/21 1:19 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class GraphDBCleanup {
    private static final Logger LOG = LoggerFactory.getLogger(GraphDBCleanup.class);

    private PersonN4jManager personN4jManager;
    private BizNameN4jManager bizNameN4jManager;
    private StoreN4jManager storeN4jManager;
    private BusinessCustomerN4jManager businessCustomerN4jManager;

    @Autowired
    public GraphDBCleanup(
        PersonN4jManager personN4jManager,
        BizNameN4jManager bizNameN4jManager,
        StoreN4jManager storeN4jManager,
        BusinessCustomerN4jManager businessCustomerN4jManager
    ) {
        this.personN4jManager = personN4jManager;
        this.bizNameN4jManager = bizNameN4jManager;
        this.storeN4jManager = storeN4jManager;
        this.businessCustomerN4jManager = businessCustomerN4jManager;
    }

    @Scheduled(cron = "${loader.GraphDBCleanup.cleanupSinceNotAccessed}")
    public void cleanupSinceNotAccessed() {
        long countBusinessCustomer = businessCustomerN4jManager.deleteNotAccessedSince(DateUtil.minusDays(1));
        long countPerson = personN4jManager.deleteNotAccessedSince(DateUtil.minusDays(1));

        businessCustomerN4jManager.deleteAll();
        personN4jManager.deleteAll();
        storeN4jManager.deleteAll();
        bizNameN4jManager.deleteAll();
        LOG.info("Deleted non accessed GraphDB {} {}", countBusinessCustomer, countPerson);
    }

    @Scheduled(cron = "${loader.GraphDBCleanup.deleteAll}")
    public void deleteAll() {
        businessCustomerN4jManager.deleteAll();
        personN4jManager.deleteAll();
        storeN4jManager.deleteAll();
        bizNameN4jManager.deleteAll();
        LOG.info("Deleted all from GraphDB");
    }
}
