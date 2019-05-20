package com.noqapp.service;

import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.repository.AdvertisementManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-16 19:40
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class AdvertisementService {

    private AdvertisementManager advertisementManager;

    @Autowired
    public AdvertisementService(AdvertisementManager advertisementManager) {
        this.advertisementManager = advertisementManager;
    }

    public void save(AdvertisementEntity advertisement) {
        advertisementManager.save(advertisement);
    }

    public List<AdvertisementEntity> findApprovalPendingAdvertisements() {
        return advertisementManager.findApprovalPendingAdvertisements();
    }

    public List<AdvertisementEntity> findAllAdvertisements(String bizNameId) {
        return advertisementManager.findAllAdvertisements(bizNameId);
    }

    public AdvertisementEntity findAdvertisementById(String advertisementId) {
        return advertisementManager.findById(advertisementId);
    }

    public long findApprovalPendingAdvertisementCount() {
        return advertisementManager.findApprovalPendingAdvertisementCount();
    }
}
