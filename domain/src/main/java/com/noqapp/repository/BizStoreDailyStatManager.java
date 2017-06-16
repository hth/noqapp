package com.noqapp.repository;

import com.noqapp.domain.BizStoreDailyStatEntity;

/**
 * User: hitender
 * Date: 6/16/17 4:48 AM
 */
public interface BizStoreDailyStatManager extends RepositoryManager<BizStoreDailyStatEntity> {

    float computeRatingForEachQueue(String bizStoreId);

    float computeRatingForBiz(String bizNameId);
}
