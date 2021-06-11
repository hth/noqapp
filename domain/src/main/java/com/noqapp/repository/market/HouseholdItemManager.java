package com.noqapp.repository.market;

import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.repository.RepositoryManager;

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

    HouseholdItemEntity findOneByIdAndExpressInterest(String id);

    List<HouseholdItemEntity> findAllPendingApproval();

    long findAllPendingApprovalCount();
}
