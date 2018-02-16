package com.noqapp.repository;

import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.annotation.Mobile;

import java.time.DayOfWeek;
import java.util.List;

/**
 * User: hitender
 * Date: 6/13/17 6:42 AM
 */
public interface StoreHourManager extends RepositoryManager<StoreHourEntity> {

    void insertAll(List<StoreHourEntity> storeHours);

    void removeAll(String bizStoreId);

    StoreHourEntity findOne(String bizStoreId, DayOfWeek dayOfWeek);

    StoreHourEntity findOne(String bizStoreId, int dayOfWeek);

    List<StoreHourEntity> findAll(String bizStoreId);

    @Mobile
    StoreHourEntity modifyOne(
            String bizStoreId,
            DayOfWeek dayOfWeek,
            int tokenAvailableFrom,
            int startHour,
            int tokenNotAvailableFrom,
            int endHour,
            boolean preventJoining,
            boolean dayClosed,
            int delayedInMinutes
    );

    boolean resetStoreHour(String id);
}
