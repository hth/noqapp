package com.noqapp.repository.market;

import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.domain.types.catgeory.MarketplaceRejectReasonEnum;
import com.noqapp.repository.RepositoryManager;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * hitender
 * 1/11/21 12:51 AM
 */
public interface PropertyRentalManager extends RepositoryManager<PropertyRentalEntity> {
    List<PropertyRentalEntity> findByQid(String queueUserId);

    PropertyRentalEntity findOneById(String id);

    PropertyRentalEntity findOneById(String qid, String id);

    Stream<PropertyRentalEntity> findAllWithStream();

    PropertyRentalEntity findOneByIdAndExpressInterestWithViewCount(String qid, String id);

    PropertyRentalEntity findOneByIdAndViewCount(String id);

    List<PropertyRentalEntity> findAllPendingApproval();

    List<PropertyRentalEntity> findAllPendingApprovalWithoutImage();

    long findAllPendingApprovalCount();

    PropertyRentalEntity changeStatus(
        String marketplaceId,
        ValidateStatusEnum validateStatus,
        MarketplaceRejectReasonEnum marketplaceRejectReason,
        Date publishUntil,
        String validatedByQid);

    PropertyRentalEntity pushImage(String id, String postImage);

    PropertyRentalEntity popImage(String id);
}
