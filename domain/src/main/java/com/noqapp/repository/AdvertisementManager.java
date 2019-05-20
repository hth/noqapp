package com.noqapp.repository;

import com.noqapp.domain.AdvertisementEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-16 13:32
 */
public interface AdvertisementManager extends RepositoryManager<AdvertisementEntity> {
    List<AdvertisementEntity> findAllAdvertisements(String bizNameId);

    List<AdvertisementEntity> findApprovalPendingAdvertisements();

    AdvertisementEntity findById(String advertisementId);

    long findApprovalPendingAdvertisementCount();

    List<AdvertisementEntity> findAllMobileClientApprovedAdvertisements(int limit);
    List<AdvertisementEntity> findAllMobileMerchantApprovedAdvertisements(int limit);
    List<AdvertisementEntity> findAllMobileTVApprovedAdvertisements(int limit);
}
