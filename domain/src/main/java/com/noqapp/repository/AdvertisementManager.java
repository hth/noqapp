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

    /**
     * Note: Limit TV advertisment to just the business owner of the TV.
     */
    List<AdvertisementEntity> findAllMobileTVApprovedAdvertisements(String bizNameId, int limit);
}
