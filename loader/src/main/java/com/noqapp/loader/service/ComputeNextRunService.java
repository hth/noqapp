package com.noqapp.loader.service;

import static com.noqapp.common.utils.DateUtil.DAY.TOMORROW;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.ScheduledTaskEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.repository.ScheduledTaskManager;
import com.noqapp.service.BizService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
 * hitender
 * 10/5/20 8:07 PM
 */
@Service
public class ComputeNextRunService {
    private static final Logger LOG = LoggerFactory.getLogger(ComputeNextRunService.class);

    private ScheduledTaskManager scheduledTaskManager;
    private BizService bizService;

    @Autowired
    public ComputeNextRunService(ScheduledTaskManager scheduledTaskManager, BizService bizService) {
        this.scheduledTaskManager = scheduledTaskManager;
        this.bizService = bizService;
    }

    public ZonedDateTime setupStoreForTomorrow(BizStoreEntity bizStore) {
        StoreHourEntity tomorrow = populateStoreHour(bizStore);

        TimeZone timeZone = TimeZone.getTimeZone(bizStore.getTimeZone());
        /* When closed set hour to 23 and minute to 59. */
        int hourOfDay = tomorrow.isDayClosed() || tomorrow.isTempDayClosed() ? 23 : tomorrow.storeClosingHourOfDay();
        int minuteOfDay = tomorrow.isDayClosed() || tomorrow.isTempDayClosed() ? 59 : tomorrow.storeClosingMinuteOfDay();
        LOG.info("Tomorrow Closing dayOfWeek={} Hour={} Minutes={} id={}", DayOfWeek.of(tomorrow.getDayOfWeek()), hourOfDay, minuteOfDay, tomorrow.getId());
        return DateUtil.computeNextRunTimeAtUTC(timeZone, hourOfDay, minuteOfDay, TOMORROW);
    }

    /**
     * This method is run from two places as archiveAndReset starts the process of scheduling and MoveScheduledAppointmentToWalkin
     * takes over for next run. Archive reset might over write the same at every run but duplicate update is acceptable for now.
     * To make it single run, a date needs to be set when Walkin Appointment feature is turned ON. //TODO fix this when there is time
     */
    public ZonedDateTime setupTokenAvailableForTomorrow(BizStoreEntity bizStore) {
        StoreHourEntity tomorrow = populateStoreHour(bizStore);

        TimeZone timeZone = TimeZone.getTimeZone(bizStore.getTimeZone());
        /* When closed set hour to 23 and minute to 59. */
        int hourOfDay = tomorrow.isDayClosed() || tomorrow.isTempDayClosed() ? 23 : tomorrow.storeTokenAvailableFromHourOfDay();
        int minuteOfDay = tomorrow.isDayClosed() || tomorrow.isTempDayClosed() ? 59 : tomorrow.storeTokenAvailableFromMinuteOfDay();
        LOG.info("Tomorrow token available from dayOfWeek={} Hour={} Minutes={} id={}", DayOfWeek.of(tomorrow.getDayOfWeek()), hourOfDay, minuteOfDay, tomorrow.getId());
        return DateUtil.computeNextRunTimeAtUTC(timeZone, hourOfDay, minuteOfDay, TOMORROW);
    }

    private StoreHourEntity populateStoreHour(BizStoreEntity bizStore) {
        DayOfWeek dayOfWeek = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId()).getDayOfWeek();
        StoreHourEntity tomorrow = bizStore.getStoreHours().get(CommonUtil.getNextDayOfWeek(dayOfWeek).getValue() - 1);
        if (StringUtils.isNotBlank(bizStore.getScheduledTaskId())) {
            populateForScheduledTask(bizStore, tomorrow);
        }
        return tomorrow;
    }

    private void populateForScheduledTask(BizStoreEntity bizStore, StoreHourEntity storeHour) {
        ScheduledTaskEntity scheduledTask = scheduledTaskManager.findOneById(bizStore.getScheduledTaskId());
        Date from = DateUtil.convertToDate(scheduledTask.getFrom(), bizStore.getTimeZone());
        Date until = DateUtil.convertToDate(scheduledTask.getUntil(), bizStore.getTimeZone());
        if (DateUtil.isThisDayBetween(from, until, TOMORROW, ZoneId.of(bizStore.getTimeZone()))) {
            switch (scheduledTask.getScheduleTask()) {
                case CLOSE:
                    storeHour.setTempDayClosed(true);
                    break;
                default:
                    throw new UnsupportedOperationException("Reached Unsupported Condition");
            }

            bizService.modifyOne(storeHour);
        } else {
            Date today = DateUtil.dateAtTimeZone(bizStore.getTimeZone());
            if (today.after(until)) {
                /* Remove schedule only when today is after set until schedule. */
                LOG.info("Removing schedule displayName={} today={} until={}", bizStore.getDisplayName(), today, until);
                bizService.unsetScheduledTask(bizStore.getId(), bizStore.getCodeQR());
                scheduledTaskManager.inActive(bizStore.getScheduledTaskId());
            }
        }
    }
}
