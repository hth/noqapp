package com.noqapp.common.utils;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
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

    public static final int MINUTES_IN_MILLISECONDS = 60_000;
    public static final int MINUTE_IN_SECONDS = 60;
    private static final int HOUR_IN_SECONDS = MINUTE_IN_SECONDS * MINUTE_IN_SECONDS;
    public static final int HOURS = 24;
    public static final int DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final DateTimeFormatter DTF_ISO = DateTimeFormatter.ofPattern(ISO8601_FMT, Locale.US);
    public static final DateTimeFormatter DTF_YYYY_MM_DD = DateTimeFormatter.ofPattern(YYYY_MM_DD, Locale.US);
    public static final DateTimeFormatter DTF_DD_MMM_YYYY = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.US);
    public static final DateTimeFormatter DTF_MMMM_DD_YYYY = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.US);
    public static final DateTimeFormatter DTF_DD_MMM_YYYY_HH_MM = DateTimeFormatter.ofPattern("dd MMM, yyyy hh:mm a", Locale.US);
    private static final DateTimeFormatter DTF_YYYY_MM_DD_KK_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm");
    public static final DateTimeFormatter DTF_HH_MM_SS_SSS = DateTimeFormatter.ofPattern("HHmmssSSS", Locale.US);

    public static final Pattern DOB_PATTERN = Pattern.compile("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");

    public enum DAY {TODAY, TOMORROW}

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

    public static Date nextDay(Date date, String timeZone) {
        return Date.from(date.toInstant().atZone(ZoneId.of(timeZone)).plusDays(1).minusSeconds(1).toInstant());
    }

    public static long getDuration(Date begin, Date end) {
        return Duration.between(begin.toInstant(), end.toInstant()).getSeconds();
    }

    public static long getDurationInHours(Date begin, Date end) {
        return Duration.between(begin.toInstant(), end.toInstant()).getSeconds();
    }

    public static Date getDateMinusMinutes(int minutes) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(minutes);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date sinceBeginningOfThisMonth() {
        return Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date sinceOneYearAgo() {
        return Date.from(LocalDate.now().minusMonths(12).withDayOfMonth(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /** Gets current time on UTC. This is required when setting up cron task as server time is set on UTC. */
    public static Date getUTCDate() {
        return new DateTime(DateTimeZone.UTC).toLocalDateTime().toDate();
    }

    /** Gets current day of week on UTC. */
    public static int getUTCDayOfWeek() {
        return new DateTime(DateTimeZone.UTC).getDayOfWeek();
    }

    public static DayOfWeek getDayOfWeekFromDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        return localDate.getDayOfWeek();
    }

    public static DayOfWeek getDayOfWeekFromDate(String day) {
        return LocalDate.parse(day).getDayOfWeek();
    }

    public static LocalTime getTimeAtTimeZone(String forTimeZone) {
        TimeZone timeZone = StringUtils.isBlank(forTimeZone) ? TimeZone.getTimeZone(ZoneId.systemDefault()) : TimeZone.getTimeZone(forTimeZone);
        return LocalTime.now(timeZone.toZoneId());
    }

    /**
     * Inclusive of the days the campaign is going to run.
     *
     * @param start
     * @param end
     * @return
     */
    @SuppressWarnings("unused")
    public static long getDaysBetween_UTC(String start, String end) {
        Assert.isTrue(StringUtils.isNotBlank(start), "Start date string is null");
        Assert.isTrue(StringUtils.isNotBlank(end), "End date string is null");
        return getDaysBetween(convertToDate(start, ZoneOffset.UTC), convertToDate(end, ZoneOffset.UTC));
    }

    public static Date convertToDate(String date, String timeZone) {
        Assert.notNull(timeZone, "timeZone cannot be null");
        return convertToDate(date, DTF_YYYY_MM_DD, ZoneId.of(timeZone));
    }

    public static Date convertToDate(String date, ZoneId zoneId) {
        Assert.notNull(zoneId, "ZoneId cannot be null");
        return convertToDate(date, DTF_YYYY_MM_DD, zoneId);
    }

    private static Date convertToDate(String date, DateTimeFormatter dateTimeFormatter, ZoneId zoneId) {
        return convertToDate(LocalDate.parse(date, dateTimeFormatter), zoneId);
    }

    private static Date convertToDate(LocalDate localDate, ZoneId zoneId) {
        return Date.from(localDate.atStartOfDay(zoneId).toInstant());
    }

    /* Used in computed if now is after the time. */
    @SuppressWarnings("unused")
    public static Date convertToDateTime(String date, String timeZone) {
        Assert.notNull(timeZone, "timeZone cannot be null");
        return convertToDateTime(date, DTF_YYYY_MM_DD, ZoneId.of(timeZone));
    }

    private static Date convertToDateTime(String date, ZoneId zoneId) {
        Assert.notNull(zoneId, "ZoneId cannot be null");
        return convertToDateTime(date, DTF_YYYY_MM_DD, zoneId);
    }

    private static Date convertToDateTime(String date, DateTimeFormatter dateTimeFormatter, ZoneId zoneId) {
        return convertToDateTime(LocalDateTime.parse(date, dateTimeFormatter), zoneId.getRules().getOffset(Instant.now()));
    }

    public static Date convertToDateTime_UTC(LocalDateTime localDateTime) {
        return convertToDateTime(localDateTime, ZoneOffset.UTC);
    }

    private static Date convertToDateTime(LocalDateTime localDateTime, ZoneOffset zoneOffset) {
        return Date.from(localDateTime.toInstant(zoneOffset));
    }

    public static String dateToString(Date date) {
        return dateToString_UTC(date, DTF_YYYY_MM_DD);
    }

    public static Date convertDateStringOf_YYYY_MM_DD_ToDate(String date) {
        return DateUtil.asDate(convertDateStringOf_YYYY_MM_DD_ToLocalDate(date));
    }

    public static LocalDate convertDateStringOf_YYYY_MM_DD_ToLocalDate(String date) {
        return LocalDate.from(DTF_YYYY_MM_DD.parse(date));
    }

    public static String dateToISO_8601(Date date) {
        return DateFormatUtils.format(date, ISO8601_FMT, TimeZone.getTimeZone("UTC"));
    }

    public static String dateToString_UTC(Date date, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.format(date.toInstant().atZone(ZoneOffset.UTC));
    }

    public static String convertDateToStringOf_DTF_DD_MMM_YYYY(Date date, String timeZone) {
        return DTF_DD_MMM_YYYY.format(date.toInstant().atZone(ZoneId.of(timeZone)));
    }

    public static String convertDateToStringOf_DTF_MMMM_DD_YYYY(LocalDate date) {
        return DTF_MMMM_DD_YYYY.format(date);
    }

    /** Date should have timezone when converting to Instant or use LocalDate. */
    public static String convertDateToStringOf_DTF_MMMM_DD_YYYY(Date date) {
        return convertDateToStringOf_DTF_MMMM_DD_YYYY(DateUtil.asLocalDate(date));
    }

    public static long getDaysBetween(Date start) {
        Assert.notNull(start, "Start date is null");
        return getDaysBetween(DateUtil.asLocalDate(start), DateUtil.asLocalDate(new Date()));
    }

    /** Inclusive of the days the campaign is going to run. */
    public static long getDaysBetween(Date start, Date end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        return getDaysBetween(DateUtil.asLocalDate(start), DateUtil.asLocalDate(end));
    }

    public static long getDaysBetween(LocalDate start, LocalDate end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        return ChronoUnit.DAYS.between(start, end);
    }

    public static long getMinutesBetween(LocalDateTime start, LocalDateTime end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static long getHoursBetween(LocalDateTime start, LocalDateTime end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        return ChronoUnit.HOURS.between(start, end);
    }

    public static long getHoursBetween(LocalDateTime start) {
        Assert.notNull(start, "Start date is null");
        return ChronoUnit.HOURS.between(start, LocalDateTime.now());
    }

    public static long getMonthsBetween(LocalDate start, LocalDate end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        return ChronoUnit.MONTHS.between(start, end);
    }

    public static long getYearsBetween(LocalDate start, LocalDate end) {
        Assert.notNull(start, "Start date is null");
        Assert.notNull(end, "End date is null");
        return ChronoUnit.YEARS.between(start, end);
    }

    public static Date minusMinutes(long minutes) {
        Instant instant = new Date().toInstant().minus(minutes, ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date minusHours(long hours) {
        Instant instant = new Date().toInstant().minus(hours, ChronoUnit.HOURS).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date minusDays(long days) {
        return minusDays(new Date(), days);
    }

    public static Date minusDays(Date date, long days) {
        Instant instant = date.toInstant().minus(days, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date plusDays(int days) {
        LocalDateTime tomorrowMidnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).plusDays(days);
        return Date.from(tomorrowMidnight.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isThisDayBetween(Date fromDay, Date untilDay, DAY day, ZoneId zoneId) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), zoneId);
        /* To get until date as YYYY-MM-DD 11:59 PM. */
        Instant untilInstant = untilDay.toInstant()
            .plus(1, ChronoUnit.DAYS)
            .minus(1, ChronoUnit.MINUTES);
        Date untilEndOfDay = Date.from(untilInstant);
        LOG.info("isThisDayBetween from={} until={} DAY={}", fromDay, untilEndOfDay, day);
        switch (day) {
            case TOMORROW:
                return isThisDayBetween(midnight(Date.from(zonedDateTime.toInstant().plus(1, ChronoUnit.DAYS))), fromDay, untilEndOfDay);
            case TODAY:
            default:
                return isThisDayBetween(Date.from(zonedDateTime.toInstant()), fromDay, untilEndOfDay);
        }
    }

    public static boolean isThisDayBetween(Date thisDay, Date fromDay, Date untilDay) {
        LOG.info("isThisDayBetween thisDay={} fromDay={} untilDay={}", thisDay, fromDay, untilDay);
        return !thisDay.before(fromDay) && !thisDay.after(untilDay);
    }

    private static Date dateAtTimeZone(ZoneId zoneId) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), zoneId);
        LOG.info("UTC={} Zone={} TimeAtZone={}", LocalDateTime.now(), zoneId.getId(), zonedDateTime);
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date dateAtTimeZone(String timeZone) {
        return dateAtTimeZone(ZoneId.of(timeZone));
    }

    /** Compute UTC based DateTime. */
    public static ZonedDateTime computeNextRunTimeAtUTC(TimeZone timeZone, int hourOfDay, int minuteOfDay, DAY day) {
        try {
            Assert.notNull(timeZone, "TimeZone should not be null");
            String str = DateUtil.getZonedDateTimeAtUTC().format(DTF_YYYY_MM_DD) + String.format(" %02d", hourOfDay) + String.format(":%02d", minuteOfDay);
            /* Compute next run. New Date technically gives us today's run date. */
            LocalDateTime localDateTime = LocalDateTime.parse(str, DTF_YYYY_MM_DD_KK_MM);
            switch (day) {
                case TOMORROW:
                    localDateTime = localDateTime.plusDays(1);
                    break;
                case TODAY:
                default:
                    //Do nothing
            }
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, timeZone.toZoneId());

            /* Note: Nothing is UTC when converted to date. Hence the System time should always be on UTC. */
            return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            LOG.error("Failed to compute next run time reason={}", e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime();
    }

    public static ZonedDateTime convertToLocalDateTime(Date dateToConvert, String timeZone) {
        return dateToConvert.toInstant()
            .atZone(ZoneId.of(timeZone));
    }

    public static int getMonthFromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date startOfMonth(Date date, String timeZone) {
        ZonedDateTime zdt = date.toInstant().atZone(ZoneId.of(timeZone));
        YearMonth ym = YearMonth.from(zdt);
        LocalDate first = ym.atDay(1);
        return Date.from(first.atStartOfDay(ZoneId.of(timeZone)).toInstant());
    }

    public static Date endOfMonth(Date date, String timeZone) {
        ZonedDateTime zdt = date.toInstant().atZone(ZoneId.of(timeZone));
        YearMonth ym = YearMonth.from(zdt);
        LocalDate last = ym.atEndOfMonth();
        return Date.from(last.atStartOfDay(ZoneId.of(timeZone)).plusDays(1).minusSeconds(1).toInstant());
    }

    public static Date convertFromISODate(String isoFormattedDateString) {
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(isoFormattedDateString);
        return Date.from(Instant.from(ta));
    }

    public static ZonedDateTime getZonedDateTimeAtUTC() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public static String getLocalDateTimeToISODate(ZonedDateTime expectedServiceBegin, String timeZone) {
        return expectedServiceBegin.withZoneSameInstant(ZoneId.of(timeZone)).format(DateUtil.DTF_ISO);
    }

    public static String getLocalDateTimeToISODate(ZonedDateTime expectedServiceBegin) {
        return getLocalDateTimeToISODate(expectedServiceBegin, "UTC");
    }
}
