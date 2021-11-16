package com.noqapp.repository.market;

import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.domain.types.catgeory.MarketplaceRejectReasonEnum;
import com.noqapp.repository.RepositoryManager;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * hitender
 * 2/25/21 1:45 PM
 */
public interface HouseholdItemManager extends RepositoryManager<HouseholdItemEntity> {
    List<HouseholdItemEntity> findByQid(String queueUserId);

    HouseholdItemEntity findOneById(String id);

    HouseholdItemEntity findOneById(String qid, String id);

    Stream<HouseholdItemEntity> findAllWithStream();

    HouseholdItemEntity findOneByIdAndExpressInterestWithViewCount(String qid, String id);

    HouseholdItemEntity findOneByIdAndViewCount(String id);

    List<HouseholdItemEntity> findAllPendingApproval();

    List<HouseholdItemEntity> findAllPendingApprovalWithoutImage();

    long findAllPendingApprovalCount();

    HouseholdItemEntity changeStatus(
        String marketplaceId,
        ValidateStatusEnum validateStatus,
        MarketplaceRejectReasonEnum marketplaceRejectReason,
        Date publishUntil,
        String qid);

    void pushImage(String id, String postImage);

    void popImage(String id);
}
