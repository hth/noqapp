package com.noqapp.repository;

import com.noqapp.domain.StatsBizStoreDailyEntity;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 6/16/17 4:48 AM
 */
public interface StatsBizStoreDailyManager extends RepositoryManager<StatsBizStoreDailyEntity> {

    StatsBizStoreDailyEntity computeRatingForEachQueue(String bizStoreId);

    float computeRatingForBiz(String bizNameId);

    /** Find all stores stats created since this day. */
    List<StatsBizStoreDailyEntity> findStores(String bizNameId, Date since);

    StatsBizStoreDailyEntity repeatAndNewCustomers(String codeQR);

    List<StatsBizStoreDailyEntity> lastTwelveMonthVisits(String codeQR);
}
