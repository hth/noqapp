package com.token.repository;

import com.token.domain.BizNameEntity;

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
     * @param businessName
     * @return
     */
    BizNameEntity findOneByName(String businessName);

    /**
     * Find all the Business with businessName
     *
     * @param businessName
     * @return
     */
    List<BizNameEntity> findAllBizWithMatchingName(String businessName);

    /**
     * Find all the Business with businessName. Mostly used for Ajax call listing.
     *
     * @param businessName
     * @return
     */
    Set<String> findAllDistinctBizStr(String businessName);

    List<BizNameEntity> findAll(int skip, int limit);
}

