package com.noqapp.service;

import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.repository.AdvertisementManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
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

    private int limit;
    private AdvertisementManager advertisementManager;

    @Autowired
    public AdvertisementService(
        @Value("${limit:5}")
        int limit,

        AdvertisementManager advertisementManager
    ) {
        this.limit = limit;
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

    public List<AdvertisementEntity> findAllMobileApprovedAdvertisements() {
        return advertisementManager.findAllMobileClientApprovedAdvertisements(limit);
    }

    public List<AdvertisementEntity> findAllMobileApprovedAdvertisements(Point point) {
        return advertisementManager.findAllMobileClientApprovedAdvertisements(point, 1000.0, limit);
    }

    public List<AdvertisementEntity> findAllMobileMerchantApprovedAdvertisements() {
        return advertisementManager.findAllMobileMerchantApprovedAdvertisements(limit);
    }

    public List<AdvertisementEntity> findAllMobileTVApprovedAdvertisements(String bizNameId) {
        return advertisementManager.findAllMobileTVApprovedAdvertisements(bizNameId, limit);
    }
}
