package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserManager;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
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
    private StatsBizStoreDailyManager statsBizStoreDailyManager;
    private MailService mailService;
    private BusinessUserManager businessUserManager;
    private UserProfileManager userProfileManager;

    @Autowired
    public BusinessStatsMail(
        @Value("${MailProcess.emailSwitch}")
        String emailSwitch,

        BizNameManager bizNameManager,
        BizStoreManager bizStoreManager,
        StatsBizStoreDailyManager statsBizStoreDailyManager,
        MailService mailService,
        BusinessUserManager businessUserManager,
        UserProfileManager userProfileManager
    ) {
        this.emailSwitch = emailSwitch;

        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
        this.statsBizStoreDailyManager = statsBizStoreDailyManager;
        this.mailService = mailService;
        this.businessUserManager = businessUserManager;
        this.userProfileManager = userProfileManager;
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

        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 11);
        List<String> zones = getAllTimeZones(date);
        Date since = DateUtil.midnight(DateUtil.getDateMinusDay(1));
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

                        for (StatsBizStoreDailyEntity statsBizStoreDaily : statsBizStores) {
                            BizStoreEntity bizStore = bizStoreManager.getById(statsBizStoreDaily.getBizStoreId());
                            LOG.info("{} {} {} since={}",
                                bizStore.getDisplayName(),
                                bizStore.getBizName().getBusinessName(),
                                statsBizStoreDaily.getTotalCustomerRated(),
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
                        rootMap.put("day", DateUtil.dateToString(since, DateUtil.DTF_DD_MMM_YYYY));
                        rootMap.put("businessName", businessName);
                        rootMap.put("totalClient", totalClient);
                        rootMap.put("totalServiced", totalServiced);
                        rootMap.put("totalNoShow", totalNoShow);
                        rootMap.put("totalAbort", totalAbort);
                        rootMap.put("clientsPreviouslyVisitedThisBusiness", clientsPreviouslyVisitedThisBusiness);
                        rootMap.put("newCustomer", totalClient - clientsPreviouslyVisitedThisBusiness);
                        rootMap.put("totalRating", totalRating);
                        rootMap.put("totalCustomerRated", totalCustomerRated);
                        rootMap.put("totalHoursSaved", totalHoursSaved);

                        List<BusinessUserEntity> businessUsers = businessUserManager.getAllForBusiness(bizName.getId(), UserLevelEnum.M_ADMIN);
                        for (BusinessUserEntity businessUser : businessUsers) {
                            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(businessUser.getQueueUserId());
                            mailService.sendAnyMail(userProfile.getEmail(), userProfile.getName(), businessName + " Daily Summary", rootMap, "stats/admin-overview.ftl");
                        }
                    } catch (Exception e) {
                        LOG.error("Failed sending stat bizName id={} name={} reason={}", bizName.getId(), bizName.getBusinessName(), e.getLocalizedMessage(), e);
                    }
                });
            }
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
}
