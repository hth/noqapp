package com.noqapp.service.market;

import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.repository.market.HouseholdItemManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 2/25/21 6:19 PM
 */
@Service
public class HouseholdItemService {
    private static final Logger LOG = LoggerFactory.getLogger(HouseholdItemService.class);

    private HouseholdItemManager householdItemManager;

    @Autowired
    public HouseholdItemService(HouseholdItemManager householdItemManager) {
        this.householdItemManager = householdItemManager;
    }

    public void save(HouseholdItemEntity householdItem) {
        householdItemManager.save(householdItem);
    }

    public List<HouseholdItemEntity> findPostedProperties(String queueUserId) {
        return householdItemManager.findByQid(queueUserId);
    }

    public List<HouseholdItemEntity> findAllPendingApproval() {
        return householdItemManager.findAllPendingApproval();
    }

    public HouseholdItemEntity findOneById(String id) {
        return householdItemManager.findOneById(id);
    }

    public HouseholdItemEntity findOneByIdAndExpressInterest(String id) {
        return householdItemManager.findOneByIdAndExpressInterest(id);
    }

    public long findAllPendingApprovalCount() {
        return householdItemManager.findAllPendingApprovalCount();
    }
}
