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

    private int showLimit;
    private int advtDistanceLimit;
    private AdvertisementManager advertisementManager;

    @Autowired
    public AdvertisementService(
        @Value("${AdvertisementService.showLimit:5}")
        int showLimit,

        @Value("${AdvertisementService.advtDistanceLimit:40}")
        int advtDistanceLimit,

        AdvertisementManager advertisementManager
    ) {
        this.showLimit = showLimit;
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

    public List<AdvertisementEntity> findAllMobileApprovedAdvertisements(Point point) {
        return advertisementManager.findAllMobileClientApprovedAdvertisements(point, advtDistanceLimit, showLimit);
    }

    public List<AdvertisementEntity> findAllMobileMerchantApprovedAdvertisements() {
        return advertisementManager.findAllMobileMerchantApprovedAdvertisements(showLimit);
    }

    public List<AdvertisementEntity> findAllMobileTVApprovedAdvertisements(String bizNameId) {
        return advertisementManager.findAllMobileTVApprovedAdvertisements(bizNameId, showLimit);
    }
}
