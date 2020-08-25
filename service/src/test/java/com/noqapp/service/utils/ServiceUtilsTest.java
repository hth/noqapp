package com.noqapp.service.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.types.QueueStatusEnum;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

        //Change to UTC time as the input is UTC based
        ZonedDateTime zonedServiceTime_UTC = zonedServiceTime.withZoneSameInstant(ZoneId.of("UTC"));

        //9:30
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusHours(9).plusMinutes(30);
        assertEquals("09:30 - 10:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //9:45
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("09:30 - 10:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //10:00
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("09:30 - 10:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //10:15
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("09:30 - 10:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //10:30
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("10:00 - 11:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //10:45
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("10:00 - 11:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //11:00
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("11:00 - 12:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //11:15
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("11:00 - 12:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //11:30
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("11:00 - 12:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //11:45
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("11:00 - 12:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //12:00
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("12:00 - 13:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //12:15
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("12:00 - 13:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //12:30
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("12:00 - 13:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //12:45
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("12:00 - 13:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //13:00
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("13:00 - 14:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //13:15
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("13:00 - 14:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //13:30
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("13:00 - 14:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //13:45
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("13:00 - 14:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //14:00
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("14:00 - 15:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //14:15
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("14:00 - 15:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //14:30
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("14:00 - 15:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //14:45
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("14:00 - 15:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //15:00
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("15:00 - 16:00", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //15:15
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("15:00 - 16:00 (closing)", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //15:30
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("15:00 - 16:00 (closing)", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //15:45
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(15);
        assertEquals("15:00 - 16:00 (closing)", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //15:50
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(5);
        assertEquals("15:00 - 16:00 (closing)", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));

        //16:00
        zonedServiceTime_UTC = zonedServiceTime_UTC.plusMinutes(10);
        assertEquals("15:00 - 16:00 (closing)", ServiceUtils.timeSlot(zonedServiceTime_UTC, zoneId, storeHour));
    }
}
