package com.noqapp.service.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.types.QueueStatusEnum;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * hitender
 * 6/3/20 11:42 PM
 */
class ServiceUtilsTest {

    @Test
    void calculateEstimatedWaitTime() {
        String estimatedWaitTime = ServiceUtils.calculateEstimatedWaitTime(
            3_00_000, //5 minutes
            5,
            QueueStatusEnum.N,
            1300,
            "Asia/Calcutta"
        );
        assertEquals("Approx 25 minutes", estimatedWaitTime);
    }

    @Test
    void timeSlot() {
        StoreHourEntity storeHour = new StoreHourEntity(UUID.randomUUID().toString(), LocalDate.now().getDayOfWeek().getValue());
        storeHour.setStartHour(930)
            .setEndHour(1600)
            .setLunchTimeStart(1300)
            .setLunchTimeEnd(1400);

        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zonedServiceTime = ZonedDateTime.of(
            2020, 1, 1,
            0, 0, 0, 0,
            zoneId);

        zonedServiceTime = zonedServiceTime.plusHours(9).plusMinutes(30);
        Date date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("09:15 - 10:15", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //9:45
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("09:15 - 10:15", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //10:00
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("09:30 - 10:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //10:15
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("09:30 - 10:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //10:30
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("10:00 - 11:00", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //10:45
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("10:30 - 11:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //11:00
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("10:30 - 11:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //11:15
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("10:30 - 11:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //11:30
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("11:00 - 12:00", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //11:45
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("11:30 - 12:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //12:00
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("11:30 - 12:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //12:15
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("11:30 - 12:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //12:30
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("12:00 - 13:00", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //12:45
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("12:30 - 13:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //13:00
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("12:30 - 13:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //13:15
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("12:30 - 13:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //13:30
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("13:00 - 14:00", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //13:45
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("13:30 - 14:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //14:00
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("13:30 - 14:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //14:15
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("13:30 - 14:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //14:30
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("14:00 - 15:00", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //14:45
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("14:30 - 15:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //15:00
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("14:30 - 15:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //15:15
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("14:30 - 15:30", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //15:30
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("15:00 - 16:00", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //15:45
        zonedServiceTime = zonedServiceTime.plusMinutes(15);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("15:00 - 16:00 (store closes)", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //15:50
        zonedServiceTime = zonedServiceTime.plusMinutes(5);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("15:00 - 16:00 (store closes)", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));

        //16:00
        zonedServiceTime = zonedServiceTime.plusMinutes(10);
        date = DateUtil.asDate(zonedServiceTime.toLocalDateTime());
        assertEquals("15:00 - 16:00 (store closes)", ServiceUtils.timeSlot(date, "America/Los_Angeles", storeHour));
    }
}
