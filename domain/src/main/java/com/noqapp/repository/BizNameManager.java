package com.noqapp.repository;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DataVisibilityEnum;
import com.noqapp.domain.types.PaymentPermissionEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/23/16 4:43 PM
 */
public interface BizNameManager extends RepositoryManager<BizNameEntity> {

    BizNameEntity getById(String id);

    BizNameEntity noName();

    /**
     * Find one Biz Name for the supplied value for the column businessName
     *
     * @param phone
     * @return
     */
    BizNameEntity findByPhone(String phone);

    /**
     * Find all the Business with businessName
     *
     * @param businessName
     * @return
     */
    List<BizNameEntity> findAllBizWithMatchingName(String businessName);

    /**
     * This method is replacement for the method listed in the link below as it reduces a step to
     * list business names as string.
     * <p>
     * TODO Needs to be tested for result and speed
     * <p>
     * {@link #findAllBizWithMatchingName}
     * {@link #findAllDistinctBizStr}
     *
     * @param businessName
     * @return
     */
    Set<String> findDistinctBizWithMatchingName(String businessName);

    /**
     * Find all the Business with businessName. Mostly used for Ajax call listing.
     *
     * @param businessName
     * @return
     */
    Set<String> findAllDistinctBizStr(String businessName);

    List<BizNameEntity> findAll(int skip, int limit);

    List<BizNameEntity> findByInviteeCode(String inviteCode);

    BizNameEntity findByCodeQR(String codeQR);

    boolean isValidCodeQR(String codeQR);

    boolean doesWebLocationExists(String webLocation, String id);

    Stream<BizNameEntity> findByBusinessType(BusinessTypeEnum businessType);

    /** Find all businesses in a particular timezone. */
    Stream<BizNameEntity> findAll(String timeZone);

    void updateDataVisibility(Map<String, DataVisibilityEnum> dataVisibilities, String id);

    void updatePaymentPermission(Map<String, PaymentPermissionEnum> paymentPermissions, String id);

    void changeBizNameBusinessType(String id, BusinessTypeEnum existingBusinessType, BusinessTypeEnum migrateToBusinessType);
}

