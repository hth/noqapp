package com.noqapp.repository.market;

import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.ValidateStatusEnum;
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

    Stream<HouseholdItemEntity> findAllWithStream();

    HouseholdItemEntity findOneByIdAndExpressInterest(String qid, String id);

    HouseholdItemEntity findOneByIdAndViewCount(String id);

    List<HouseholdItemEntity> findAllPendingApproval();

    long findAllPendingApprovalCount();

    HouseholdItemEntity changeStatus(String marketplaceId, ValidateStatusEnum validateStatus, Date publishUntil, String qid);
}
