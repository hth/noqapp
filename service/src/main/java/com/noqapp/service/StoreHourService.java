package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonHour;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.repository.StoreHourManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * hitender
 * 11/24/20 5:07 PM
 */
@Service
public class StoreHourService {
    private static final Logger LOG = LoggerFactory.getLogger(StoreHourService.class);

    private StoreHourManager storeHourManager;

    @Autowired
    public StoreHourService(StoreHourManager storeHourManager) {
        this.storeHourManager = storeHourManager;
    }

    @Mobile
    public StoreHourEntity findStoreHour(String bizStoreId, int dayOfWeek) {
        return storeHourManager.findOne(bizStoreId, dayOfWeek);
    }

    @Mobile
    public StoreHourEntity findStoreHour(String bizStoreId, DayOfWeek dayOfWeek) {
        return storeHourManager.findOne(bizStoreId, dayOfWeek);
    }

    public List<StoreHourEntity> findAllStoreHours(String bizStoreId) {
        return storeHourManager.findAll(bizStoreId);
    }

    public List<JsonHour> findAllStoreHoursAsJson(BizStoreEntity bizStore) {
        List<JsonHour> jsonHours = new LinkedList<>();

        if (null != bizStore) {
            List<StoreHourEntity> storeHours = findAllStoreHours(bizStore.getId());
            for (StoreHourEntity storeHour : storeHours) {
                JsonHour jsonHour = new JsonHour()
                    .setDayOfWeek(storeHour.getDayOfWeek())
                    .setTokenAvailableFrom(storeHour.getTokenAvailableFrom())
                    .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
                    .setStartHour(storeHour.getStartHour())
                    .setAppointmentStartHour(bizStore.getAppointmentState() == AppointmentStateEnum.O ? 0 : storeHour.getAppointmentStartHour())
                    .setEndHour(storeHour.getEndHour())
                    .setAppointmentEndHour(bizStore.getAppointmentState() == AppointmentStateEnum.O ? 0 : storeHour.getAppointmentEndHour())
                    .setLunchTimeStart(storeHour.getLunchTimeStart())
                    .setLunchTimeEnd(storeHour.getLunchTimeEnd())
                    .setPreventJoining(storeHour.isPreventJoining())
                    .setDayClosed(storeHour.isDayClosed() || storeHour.isTempDayClosed())
                    .setDelayedInMinutes(storeHour.getDelayedInMinutes());
                jsonHours.add(jsonHour);
            }
        }

        return jsonHours;
    }

    //TODO instead send all the hours of the store and let App figure out which one to show.
    public StoreHourEntity getStoreHours(String codeQR, BizStoreEntity bizStore) {
        DayOfWeek dayOfWeek = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId()).getDayOfWeek();
        LOG.debug("codeQR={} dayOfWeek={}", codeQR, dayOfWeek);

        StoreHourEntity storeHour = findStoreHour(bizStore.getId(), dayOfWeek);
        LOG.debug("StoreHour={}", storeHour);
        return storeHour;
    }
}
