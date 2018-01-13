package com.noqapp.repository;

import com.mongodb.client.DistinctIterable;
import com.noqapp.domain.BizNameEntity;

import java.util.List;
import java.util.Set;

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
}

