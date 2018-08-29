package com.noqapp.common.utils;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.PeriodType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * User: hitender
 * Date: 11/18/16 6:08 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class DateUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    private static final int MINUTE_IN_SECONDS = 60;
    private static final int HOUR_IN_SECONDS = MINUTE_IN_SECONDS * MINUTE_IN_SECONDS;
    public static final int HOURS = 24;
    public static final int DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
    private static final DateTimeFormatter DTF_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
    public static final DateTimeFormatter DTF_DD_MMM_YYYY = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.US);
    public static final SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final SimpleDateFormat SDF_MMM_YYYY = new SimpleDateFormat("MMM, yyyy", Locale.US);
    public static final Pattern DOB_PATTERN = Pattern.compile("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");

    private DateUtil() {
    }

    /**
     * Converts java.util.Date to Joda DateTime.
     *
     * @param date
     * @return
     */
    public static DateTime toDateTime(Date date) {
        return new DateTime(date);
    }

    /** @return DateTime of type Joda Time. */
    public static DateTime now() {
        return DateTime.now();
    }

    public static Date nowDate() {
        return now().toDate();
    }

    public static Date nowMidnightDate() {
        return midnight(now()).toDate();
    }

    public static DateTime startOfYear() {
        return now().withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
    }

    public static DateTime midnight(DateTime dateTime) {
        return dateTime.withTimeAtStartOfDay();
    }

    public static Date midnight(Date date) {
        return midnight(new DateTime(date)).toDate();
    }

    public static long getDuration(Date begin, Date end) {
        return Duration.between(begin.toInstant(), end.toInstant()).getSeconds();
    }

    public static Date getDateMinusMinutes(int minutes) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(minutes);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date getDateMinusDay(long days) {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(days);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Gets current time on UTC. This is required when setting up cron task as server time is set on UTC.
     *
     * @return
     */
    public static Date getUTCDate() {
        return new DateTime(DateTimeZone.UTC).toLocalDateTime().toDate();
    }

    /**
     * Inclusive of the days the campaign is going to run.
     *
     * @param start
     * @param end
     * @return
     */
    public static int getDaysBetween(String start, String end) {
        Assert.isTrue(StringUtils.isNotBlank(start), "Start date string is null");
        Assert.isTrue(StringUtils.isNotBlank(end), "End date string is null");
        return getDaysBetween(convertToDate(start), convertToDate(end));
    }

    public static Date convertToDate(String date) {
        return convertToDate(date, DTF_YYYY_MM_DD);
    }

    private static Date convertToDate(String date, DateTimeFormatter dateTimeFormatter) {
        return convertToDate(LocalDate.parse(date, dateTimeFormatter));
    }

    private static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    private static Date convertToDateTime(String date, DateTimeFormatter dateTimeFormatter) {
        return convertToDateTime(LocalDateTime.parse(date, dateTimeFormatter));
    }

    public static Date convertToDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    public static String dateToString(Date date) {
        return dateToString(date, DTF_YYYY_MM_DD);
    }

    public static String dateToString(Date date, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.format(date.toInstant().atZone(ZoneOffset.UTC));
    }

    /**
     * Inclusive of the days the campaign is going to run.
     *
     * @param start
     * @param end
     * @return
     */
    public static int getDaysBetween(Date start, Date end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        Interval interval = new Interval(start.getTime(), end.getTime());
        return interval.toPeriod(PeriodType.days()).getDays();
    }

    public static int getYearsBetween(Date start, Date end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        Interval interval = new Interval(start.getTime(), end.getTime());
        return interval.toPeriod(PeriodType.years()).getYears();
    }

    public static int getMillisBetween(Date start, Date end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        Interval interval = new Interval(start.getTime(), end.getTime());
        return interval.toPeriod(PeriodType.millis()).getMillis();
    }

    public static int getSecondsBetween(Date start, Date end) {
        return getMillisBetween(start, end) / 1000;
    }

    public static int getMinuteBetween(Date start, Date end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        Interval interval = new Interval(start.getTime(), end.getTime());
        return interval.toPeriod(PeriodType.minutes()).getMinutes();
    }

    public static Date plusDays(int days) {
        return Date.from(LocalDate.now().plusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
