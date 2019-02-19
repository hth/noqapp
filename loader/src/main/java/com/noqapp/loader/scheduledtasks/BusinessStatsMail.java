package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserManager;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.MailService;
import com.noqapp.service.StatsCronService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * hitender
 * 8/28/18 7:27 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class BusinessStatsMail {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessStatsMail.class);

    private String emailSwitch;

    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;
    private StoreHourManager storeHourManager;
    private StatsBizStoreDailyManager statsBizStoreDailyManager;
    private MailService mailService;
    private BusinessUserManager businessUserManager;
    private BusinessUserStoreManager businessUserStoreManager;
    private UserProfileManager userProfileManager;
    private StatsCronService statsCronService;

    @Autowired
    public BusinessStatsMail(
        @Value("${MailProcess.emailSwitch}")
        String emailSwitch,

        BizNameManager bizNameManager,
        BizStoreManager bizStoreManager,
        StoreHourManager storeHourManager,
        StatsBizStoreDailyManager statsBizStoreDailyManager,
        MailService mailService,
        BusinessUserManager businessUserManager,
        BusinessUserStoreManager businessUserStoreManager,
        UserProfileManager userProfileManager,
        StatsCronService statsCronService
    ) {
        this.emailSwitch = emailSwitch;

        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
        this.storeHourManager = storeHourManager;
        this.statsBizStoreDailyManager = statsBizStoreDailyManager;
        this.mailService = mailService;
        this.businessUserManager = businessUserManager;
        this.businessUserStoreManager = businessUserStoreManager;
        this.userProfileManager = userProfileManager;
        this.statsCronService = statsCronService;
    }

    /**
     * Delivers daily stats through email.
     */
    @Scheduled(fixedDelayString = "${loader.BusinessStatsMail.businessStatusMail}")
    public void businessStatusMail() {
        StatsCronEntity statsCron = new StatsCronEntity(
            BusinessStatsMail.class.getName(),
            "businessStatusMail",
            emailSwitch);

        if ("OFF".equalsIgnoreCase(emailSwitch)) {
            return;
        }

        AtomicInteger mailSentCount = new AtomicInteger();
        AtomicInteger businessCount = new AtomicInteger();
        try {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, 7);
            List<String> zones = getAllTimeZones(date);
            Date since = DateUtil.midnight(DateUtil.getDateMinusDay(1));
            DayOfWeek dayOfWeek = DateUtil.getDayOfWeekFromDate(since);
            for (String zone : zones) {
                try (Stream<BizNameEntity> stream = bizNameManager.findAll(zone)) {
                    stream.iterator().forEachRemaining(bizName -> {
                        String businessName = bizName.getBusinessName();
                        int totalClient = 0;
                        int totalServiced = 0;
                        int totalNoShow = 0;
                        int totalAbort = 0;
                        int clientsPreviouslyVisitedThisBusiness = 0;
                        int totalRating = 0;
                        int totalCustomerRated = 0;
                        long totalHoursSaved = 0;
                        Map<String, String> timeOfServices = new HashMap<>();

                        businessCount.getAndIncrement();
                        try {
                            List<StatsBizStoreDailyEntity> statsBizStores = statsBizStoreDailyManager.findStores(bizName.getId(), since);
                            String storeName;
                            int storeTotalClient;
                            int storeTotalServiced;
                            int storeTotalNoShow;
                            int storeTotalAbort;
                            int storeClientsPreviouslyVisitedThisBusiness;
                            int storeTotalRating;
                            int storeTotalCustomerRated;
                            long storeTotalHoursSaved;
                            String firstServicedOrSkipped;
                            String lastServicedOrSkipped;

                            for (StatsBizStoreDailyEntity statsBizStoreDaily : statsBizStores) {
                                BizStoreEntity bizStore = bizStoreManager.getById(statsBizStoreDaily.getBizStoreId());
                                LOG.info("Loaded stats for bizStore={} businessName={} totalCustomerRated={} clients={} since={}",
                                    bizStore.getDisplayName(),
                                    bizStore.getBizName().getBusinessName(),
                                    statsBizStoreDaily.getTotalCustomerRated(),
                                    statsBizStoreDaily.getTotalClient(),
                                    since);

                                storeName = bizStore.getDisplayName();
                                storeTotalClient = statsBizStoreDaily.getTotalClient();
                                storeTotalServiced = statsBizStoreDaily.getTotalServiced();
                                storeTotalNoShow = statsBizStoreDaily.getTotalNoShow();
                                storeTotalAbort = statsBizStoreDaily.getTotalAbort();
                                storeClientsPreviouslyVisitedThisBusiness = statsBizStoreDaily.getClientsPreviouslyVisitedThisStore();
                                storeTotalRating = statsBizStoreDaily.getTotalRating();
                                storeTotalCustomerRated = statsBizStoreDaily.getTotalCustomerRated();
                                storeTotalHoursSaved = statsBizStoreDaily.getTotalHoursSaved();
                                firstServicedOrSkipped = statsBizStoreDaily.getFirstServicedOrSkipped() == null
                                    ? "N/A"
                                    : statsBizStoreDaily.getFirstServicedOrSkipped();
                                lastServicedOrSkipped = statsBizStoreDaily.getLastServicedOrSkipped() == null
                                    ? "N/A"
                                    : statsBizStoreDaily.getLastServicedOrSkipped();

                                /* Add details when data is not null. */
                                if (null != statsBizStoreDaily.getFirstServicedOrSkipped() || null != statsBizStoreDaily.getLastServicedOrSkipped()) {
                                    StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), dayOfWeek);
                                    timeOfServices.put(
                                        storeName,
                                        computeBeforeAfterSchedule(storeHour.getStartHour(), firstServicedOrSkipped, true)
                                            + " - "
                                            + computeBeforeAfterSchedule(storeHour.getEndHour(), lastServicedOrSkipped, false));
                                }

                                if (storeTotalClient > 0) {
                                    Map<String, Object> rootMap = new HashMap<>();
                                    rootMap.put("day", DateUtil.dateToString_UTC(since, DateUtil.DTF_DD_MMM_YYYY));
                                    rootMap.put("businessName", storeName);
                                    rootMap.put("totalClient", storeTotalClient);
                                    rootMap.put("totalServiced", storeTotalServiced);
                                    rootMap.put("totalNoShow", storeTotalNoShow);
                                    rootMap.put("totalAbort", storeTotalAbort);
                                    rootMap.put("clientsPreviouslyVisitedThisBusiness", storeClientsPreviouslyVisitedThisBusiness);
                                    /* Previously visited could be more than total client as few of them has already aborted. Hence less than zero possible. */
                                    if (storeTotalClient - storeClientsPreviouslyVisitedThisBusiness <= 0) {
                                        LOG.warn("Check storeName={} tc={} ts={} tn={} ta={} cpv={} id={}",
                                            storeName,
                                            storeTotalClient,
                                            storeTotalServiced,
                                            storeTotalNoShow,
                                            storeTotalAbort,
                                            storeClientsPreviouslyVisitedThisBusiness,
                                            bizStore.getId());
                                        rootMap.put("newCustomer", 0);
                                    } else {
                                        rootMap.put("newCustomer", storeTotalClient - storeClientsPreviouslyVisitedThisBusiness);
                                    }
                                    rootMap.put("totalRating", storeTotalRating);
                                    rootMap.put("totalCustomerRated", storeTotalCustomerRated);
                                    rootMap.put("totalHoursSaved", storeTotalHoursSaved / (60 * 1000));
                                    rootMap.put("timeOfService", formattedTime(firstServicedOrSkipped) + " - " + formattedTime(lastServicedOrSkipped));

                                    List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.findAllManagingStoreWithUserLevel(bizStore.getId(), UserLevelEnum.S_MANAGER);
                                    LOG.info("Found business users size={} {} storeTotalClient={}", businessUserStores.size(), businessUserStores, storeTotalClient);
                                    for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
                                        mailSentCount.getAndIncrement();

                                        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(businessUserStore.getQueueUserId());
                                        mailService.sendAnyMail(
                                            userProfile.getEmail(),
                                            userProfile.getName(),
                                            storeName + " Daily Summary",
                                            rootMap,
                                            "stats/admin-overview.ftl");
                                    }
                                }

                                totalClient = totalClient + storeTotalClient;
                                totalServiced = totalServiced + storeTotalServiced;
                                totalNoShow = totalNoShow + storeTotalNoShow;
                                totalAbort = totalAbort + storeTotalAbort;
                                clientsPreviouslyVisitedThisBusiness = clientsPreviouslyVisitedThisBusiness + storeClientsPreviouslyVisitedThisBusiness;
                                totalRating = totalRating + storeTotalRating;
                                totalCustomerRated = totalCustomerRated + storeTotalCustomerRated;
                                totalHoursSaved = totalHoursSaved + storeTotalHoursSaved;
                            }

                            Map<String, Object> rootMap = new HashMap<>();
                            rootMap.put("day", DateUtil.dateToString_UTC(since, DateUtil.DTF_DD_MMM_YYYY));
                            rootMap.put("businessName", businessName);
                            rootMap.put("totalClient", totalClient);
                            rootMap.put("totalServiced", totalServiced);
                            rootMap.put("totalNoShow", totalNoShow);
                            rootMap.put("totalAbort", totalAbort);
                            rootMap.put("clientsPreviouslyVisitedThisBusiness", clientsPreviouslyVisitedThisBusiness);
                            /* Previously visited could be more than total client as few of them has already aborted. Hence less than zero possible. */
                            if (totalClient - clientsPreviouslyVisitedThisBusiness <= 0) {
                                LOG.warn("Check storeName={} tc={} ts={} tn={} ta={} cpv={} id={}",
                                    businessName,
                                    totalClient,
                                    totalServiced,
                                    totalNoShow,
                                    totalAbort,
                                    clientsPreviouslyVisitedThisBusiness,
                                    bizName.getId());
                                rootMap.put("newCustomer", 0);
                            } else {
                                rootMap.put("newCustomer", totalClient - clientsPreviouslyVisitedThisBusiness);
                            }
                            rootMap.put("totalRating", totalRating);
                            rootMap.put("totalCustomerRated", totalCustomerRated);
                            rootMap.put("totalHoursSaved", totalHoursSaved/(60 * 1000));
                            rootMap.put("timeOfServices", timeOfServices);

                            List<BusinessUserEntity> businessUsers = businessUserManager.getAllForBusiness(bizName.getId(), UserLevelEnum.M_ADMIN);
                            for (BusinessUserEntity businessUser : businessUsers) {
                                mailSentCount.getAndIncrement();

                                UserProfileEntity userProfile = userProfileManager.findByQueueUserId(businessUser.getQueueUserId());
                                mailService.sendAnyMail(
                                    userProfile.getEmail(),
                                    userProfile.getName(),
                                    businessName + " Daily Summary",
                                    rootMap,
                                    "stats/admin-overview.ftl");
                            }
                        } catch (Exception e) {
                            LOG.error("Failed sending stat bizName id={} name={} reason={}", bizName.getId(), bizName.getBusinessName(), e.getLocalizedMessage(), e);
                        }
                    });
                }
            }
        } catch (Exception e) {
            LOG.error("Failed sending business status mail to admins reason={}", e.getLocalizedMessage(), e);
        } finally {
            statsCron.addStats("sentMail", mailSentCount.get());
            statsCron.addStats("businessCount", businessCount.get());
            statsCronService.save(statsCron);

            /* Without if condition its too noisy. */
            LOG.info("Business Status Mail sentMail={} businessCount={}", mailSentCount.get(), businessCount.get());

        }
    }

    private String formattedTime(String timeAsString) {
        try {
            if (timeAsString.equalsIgnoreCase("N/A")) {
                return "N/A";
            }
            int time = Integer.parseInt(timeAsString);
            return DateFormatter.convertMilitaryTo12HourFormat(time);
        } catch (Exception e) {
            LOG.warn("Failed formatting timeAsString={}", timeAsString);
            return "N/A";
        }
    }

    /** Load time zone which matches the time set. */
    private List<String> getAllTimeZones(Calendar date) {
        List<String> ret = new ArrayList<>();
        String[] timezones = TimeZone.getAvailableIDs();
        for (String timezone : timezones) {
            Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone(timezone));
            Integer zoneHour = currentTime.get(Calendar.HOUR_OF_DAY);
            Integer dateHour = date.get(Calendar.HOUR_OF_DAY);
            if (zoneHour.equals(dateHour)) {
                ret.add(timezone);
            }
        }
        return ret;
    }

    private String computeBeforeAfterSchedule(int expected, String actual, boolean arrival) {
        String text = arrival ? "Arrived" : "Departure";
        try {
            if (actual.equalsIgnoreCase("N/A")) {
                return "Schedule [" + DateFormatter.convertMilitaryTo12HourFormat(expected) + "] " +
                    "[" + text + ": " + "N/A" + "] (--)";
            }

            int act = Integer.valueOf(actual);
            if (act < expected) {
                return "Schedule [" + DateFormatter.convertMilitaryTo12HourFormat(expected) + "] " +
                    "[" + text + ": " + DateFormatter.convertMilitaryTo12HourFormat(act) + "] (Early)";
            } else if (act > expected) {
                return "Schedule [" + DateFormatter.convertMilitaryTo12HourFormat(expected) + "] " +
                    "[" + text + ": " + DateFormatter.convertMilitaryTo12HourFormat(act) + "] (Late)";
            } else {
                return "Schedule [" + DateFormatter.convertMilitaryTo12HourFormat(expected) + "] " +
                    "[" + text + ": " + DateFormatter.convertMilitaryTo12HourFormat(act) + "] (On time)";
            }
        } catch (Exception e) {
            LOG.warn("Failed computeBeforeAfterSchedule expected={} actual={} arrival={}", expected, actual, arrival);
            return "Schedule [" + DateFormatter.convertMilitaryTo12HourFormat(expected) + "] " +
                "[" + text + ": " + "N/A" + "] (--)";
        }
    }
}
