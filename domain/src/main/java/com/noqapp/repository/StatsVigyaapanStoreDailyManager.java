package com.noqapp.repository;

import com.noqapp.domain.StatsVigyaapanStoreDailyEntity;

/**
 * hitender
 * 2018-12-20 10:53
 */
public interface StatsVigyaapanStoreDailyManager extends RepositoryManager<StatsVigyaapanStoreDailyEntity> {

    void tagAsDisplayed(String bizStoreId, int dayOfWeek);
}
