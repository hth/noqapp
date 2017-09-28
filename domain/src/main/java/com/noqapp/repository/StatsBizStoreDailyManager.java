package com.noqapp.repository;

import com.noqapp.domain.StatsBizStoreDailyEntity;

/**
 * User: hitender
 * Date: 6/16/17 4:48 AM
 */
public interface StatsBizStoreDailyManager extends RepositoryManager<StatsBizStoreDailyEntity> {

    StatsBizStoreDailyEntity computeRatingForEachQueue(String bizStoreId);

    float computeRatingForBiz(String bizNameId);
}
