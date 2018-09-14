package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.site.JsonBusiness;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserManager;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: hitender
 * Date: 11/23/16 4:41 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BizService {
    private static final Logger LOG = LoggerFactory.getLogger(BizService.class);

    private double degreeInMiles;
    private double degreeInKilometers;

    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;
    private StoreHourManager storeHourManager;
    private TokenQueueService tokenQueueService;
    private QueueService queueService;
    private BusinessUserManager businessUserManager;
    private BusinessUserStoreManager businessUserStoreManager;
    private MailService mailService;
    private UserProfileManager userProfileManager;

    @Autowired
    public BizService(
            @Value("${degreeInMiles:69.172}")
            double degreeInMiles,

            @Value("${degreeInKilometers:111.321}")
            double degreeInKilometers,

            BizNameManager bizNameManager,
            BizStoreManager bizStoreManager,
            StoreHourManager storeHourManager,
            TokenQueueService tokenQueueService,
            QueueService queueService,
            BusinessUserManager businessUserManager,
            BusinessUserStoreManager businessUserStoreManager,
            MailService mailService,
            UserProfileManager userProfileManager
    ) {
        this.degreeInMiles = degreeInMiles;
        this.degreeInKilometers = degreeInKilometers;
        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
        this.storeHourManager = storeHourManager;
        this.tokenQueueService = tokenQueueService;
        this.queueService = queueService;
        this.businessUserManager = businessUserManager;
        this.businessUserStoreManager = businessUserStoreManager;
        this.mailService = mailService;
        this.userProfileManager = userProfileManager;
    }

    public BizNameEntity getByBizNameId(String bizId) {
        return bizNameManager.getById(bizId);
    }

    public void saveName(BizNameEntity bizName) {
        bizNameManager.save(bizName);
    }

    public BizStoreEntity getByStoreId(String storeId) {
        return bizStoreManager.getById(storeId);
    }

    public void deleteStore(String storeId) {
        BizStoreEntity bizStore = getByStoreId(storeId);
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(bizStore.getCodeQR());
        tokenQueueService.deleteHard(tokenQueue);
        long queuedRemoved = queueService.deleteByCodeQR(bizStore.getCodeQR());
        bizStoreManager.deleteHard(bizStore);
        storeHourManager.removeAll(storeId);
        long removedRecords = businessUserStoreManager.deleteAllManagingStore(storeId);
        LOG.info("Deleted Store id={} removed reference to number of people managing queue={} queuedRemoved={}",
                storeId,
                removedRecords,
                queuedRemoved);
    }

    public void saveStore(BizStoreEntity bizStore, String changeInitiateReason) {
        bizStoreManager.save(bizStore);
        try {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            changeInitiateReason = changeInitiateReason + ", modified by " + queueUser.getName();
            LOG.info("Changed bizStoreId={} name={} reason={}", bizStore.getId(), bizStore.getDisplayName(), changeInitiateReason);
        } catch (Exception e) {
            LOG.warn("QueueUser is null, check the call, bizStoreId={} name={} reason={} errorReason={}", bizStore.getId(), bizStore.getDisplayName(), changeInitiateReason, e.getLocalizedMessage(), e);
        }

        String finalChangeInitiateReason = changeInitiateReason;
        sendMailWhenStoreSettingHasChanged(bizStore.getId(), finalChangeInitiateReason);
    }

    @Mobile
    public void sendMailWhenStoreSettingHasChanged(String bizStoreId, String changeInitiateReason) {
        BizStoreEntity bizStore = getByStoreId(bizStoreId);
        bizStore.setStoreHours(findAllStoreHours(bizStore.getId()));

        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("changeInitiateReason", changeInitiateReason);
        rootMap.put("displayName", bizStore.getDisplayName());
        rootMap.put("remoteJoin", bizStore.isRemoteJoin() ? "Yes" : "No");
        rootMap.put("allowLoggedInUser", bizStore.isAllowLoggedInUser() ? "Yes" : "No");
        rootMap.put("availableTokenCount", bizStore.getAvailableTokenCount() == 0 ? "Unlimited" : bizStore.getAvailableTokenCount() + " tokens");
        //rootMap.put("temporaryClosed", bizStore.isTemporaryClosed());
        rootMap.put("famousFor", StringUtils.isBlank(bizStore.getFamousFor()) ? "N/A" : bizStore.getFamousFor());

        for (StoreHourEntity storeHour : bizStore.getStoreHours()) {
            Map<String, Object> storeHoursAsMap = new LinkedHashMap<>();
            if (storeHour.isDayClosed()) {
                storeHoursAsMap.put("Is closed for the day? ", storeHour.isDayClosed() || storeHour.isTempDayClosed() ? "Yes" : "No");
            } else {
                storeHoursAsMap.put("Issue token from: ", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getTokenAvailableFrom(DayOfWeek.of(storeHour.getDayOfWeek()))));
                storeHoursAsMap.put("Stop issuing token after: ", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getTokenNotAvailableFrom(DayOfWeek.of(storeHour.getDayOfWeek()))));
                storeHoursAsMap.put("Queue start time: ", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getStartHour(DayOfWeek.of(storeHour.getDayOfWeek()))));
                storeHoursAsMap.put("Queue close time: ", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getEndHour(DayOfWeek.of(storeHour.getDayOfWeek()))));

                if (storeHour.isTempDayClosed()) {
                    storeHoursAsMap.put("Temporary closed today: ", "Yes");
                    rootMap.put("closedForToday",  "Yes (For " + DayOfWeek.of(storeHour.getDayOfWeek()).name() + ")");
                }
            }
            rootMap.put(DayOfWeek.of(storeHour.getDayOfWeek()).name(), storeHoursAsMap);
        }

        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.findAllManagingStoreWithUserLevel(bizStoreId, UserLevelEnum.S_MANAGER);
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(businessUserStore.getQueueUserId());

            mailService.sendAnyMail(
                userProfile.getEmail(),
                userProfile.getName(),
                bizStore.getDisplayName() + ": Queue changes confirmation",
                rootMap,
                "mail/changedStoreSetting.ftl");
        }

        List<BusinessUserEntity> businessUsers = businessUserManager.getAllForBusiness(bizStore.getBizName().getId(), UserLevelEnum.M_ADMIN);
        for (BusinessUserEntity businessUser : businessUsers) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(businessUser.getQueueUserId());

            mailService.sendAnyMail(
                userProfile.getEmail(),
                userProfile.getName(),
                bizStore.getDisplayName() + ": Queue changes confirmation",
                rootMap,
                "mail/changedStoreSetting.ftl");
        }
    }

    public Set<BizStoreEntity> bizSearch(String businessName, String bizAddress, String bizPhone) {
        Set<BizStoreEntity> bizStoreEntities = new HashSet<>();

        if (StringUtils.isNotBlank(businessName)) {
            List<BizNameEntity> bizNameEntities = bizNameManager.findAllBizWithMatchingName(businessName);
            for (BizNameEntity bizName : bizNameEntities) {
                List<BizStoreEntity> bizStores = bizStoreManager.findAllWithStartingAddressStartingPhone(
                        bizAddress,
                        bizPhone,
                        bizName);
                bizStoreEntities.addAll(bizStores);
            }
        } else {
            List<BizStoreEntity> bizStores = bizStoreManager.findAllWithStartingAddressStartingPhone(
                    bizAddress,
                    bizPhone,
                    null);
            bizStoreEntities.addAll(bizStores);
        }
        return bizStoreEntities;
    }

    public void deleteBizStore(BizStoreEntity bizStore) {
        bizStoreManager.deleteHard(bizStore);
    }

    public void deleteBizName(BizNameEntity bizName) {
        bizNameManager.deleteHard(bizName);
    }

    public BizNameEntity findByPhone(String phone) {
        return bizNameManager.findByPhone(phone);
    }

    public BizStoreEntity findOneBizStore(String bizNameId) {
        return bizStoreManager.findOne(bizNameId);
    }

    public long getCountOfStore(String bizNameId) {
        return bizStoreManager.getCountOfStore(bizNameId);
    }

    public List<BizStoreEntity> getAllBizStores(String bizNameId) {
        return bizStoreManager.getAllBizStores(bizNameId);
    }

    @Mobile
    public List<BizStoreEntity> getAllBizStoresMatchingAddress(String bizStoreAddress, String bizNameId) {
        return bizStoreManager.getAllBizStoresMatchingAddress(bizStoreAddress, bizNameId);
    }

    public BizStoreEntity findByCodeQR(String codeQR) {
        return bizStoreManager.findByCodeQR(codeQR);
    }

    @Mobile
    public boolean isValidCodeQR(String codeQR) {
        return Validate.isValidObjectId(codeQR) && bizStoreManager.isValidCodeQR(codeQR);
    }

    @Mobile
    public boolean isValidBizNameCodeQR(String codeQR) {
        return Validate.isValidObjectId(codeQR) && bizNameManager.isValidCodeQR(codeQR);
    }

    public void insertAll(List<StoreHourEntity> storeHours) {
        storeHourManager.insertAll(storeHours);
    }

    public void removeAll(String bizStoreId) {
        storeHourManager.removeAll(bizStoreId);
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

    public List<BizNameEntity> findByInviteeCode(String inviteCode) {
        return bizNameManager.findByInviteeCode(inviteCode);
    }

    @Mobile
    public void updateBizStoreAvailableTokenCount(int availableTokenCount, String codeQR) {
        bizStoreManager.updateBizStoreAvailableTokenCount(availableTokenCount, codeQR);
    }

    public Map<String, Long> countCategoryUse(Set<String> categories, String bizNameId) {
        Map<String, Long> maps = new HashMap<>();
        for (String bizCategoryId : categories) {
            maps.put(bizCategoryId, bizStoreManager.countCategoryUse(bizCategoryId, bizNameId));
        }

        return maps;
    }

    public List<BizStoreEntity> getBizStoresByCategory(String bizCategoryId, String bizNameId) {
        return bizStoreManager.getBizStoresByCategory(bizCategoryId, bizNameId);
    }

    public BizNameEntity findBizNameByCodeQR(String codeQR) {
        return bizNameManager.findByCodeQR(codeQR);
    }

    private boolean doesBusinessWebLocationExists(String webLocation, String bizId) {
        return bizNameManager.doesWebLocationExists(webLocation, bizId);
    }

    private boolean doesStoreWebLocationExists(String webLocation, String storeId) {
        return bizStoreManager.doesWebLocationExists(webLocation, storeId);
    }

    public String buildWebLocationForStore(
            String area,
            String town,
            String stateShortName,
            String countryShortNameStore,
            String name,
            String displayName,
            String storeId
    ) {
        String webLocation = computeWebLocationForStore(
                area,
                town,
                stateShortName,
                countryShortNameStore,
                name,
                displayName);

        while (doesStoreWebLocationExists(webLocation, storeId)) {
            webLocation = CommonUtil.replaceLast(webLocation, "/", "/" + RandomString.newInstance(3).nextString() + "/");
        }

        return webLocation;
    }

    public String buildWebLocationForBiz(
            String town,
            String stateShortName,
            String countryShortName,
            String name,
            String bizId
    ) {
        String webLocation = computeWebLocationForBiz(
                town,
                stateShortName,
                countryShortName,
                name);

        while (doesBusinessWebLocationExists(webLocation, bizId)) {
            webLocation = CommonUtil.replaceLast(webLocation, "/", "/" + RandomString.newInstance(3).nextString() + "/");
        }

        return webLocation;
    }

    private String computeWebLocationForStore(
            String area,
            String town,
            String stateShortName,
            String countryShortNameStore,
            String name,
            String displayName
    ) {
        try {
            String areaString = StringUtils.isNotBlank(area) ? area.trim().toLowerCase().replace(" ", "-") : "-";
            String townString = StringUtils.isNotBlank(town) ? town.trim().toLowerCase().replace(" ", "-") : "-";
            String stateShortNameString = StringUtils.isNotBlank(stateShortName) ? stateShortName.trim().toLowerCase() : "-";

            /*
             * Note: Same Display Name at same location will generate same webLocation.
             * You might need to redo this with some randomness in URL.
             */
            String webLocation = "/"
                    + countryShortNameStore.toLowerCase()
                    + "/"
                    + name.replaceAll("[^a-zA-Z]+", "-").toLowerCase().trim()
                    + "/"
                    + areaString
                    + "-"
                    + townString
                    + "-"
                    + stateShortNameString
                    + "/"
                    + displayName.replaceAll("[^a-zA-Z]+", "-").toLowerCase().trim();

            return webLocationSanitize(webLocation);
        } catch (Exception e) {
            LOG.error("Failed creating Web Location for store at town={} stateShortName={}", town, stateShortName);
            throw e;
        }
    }

    private String computeWebLocationForBiz(
            String town,
            String stateShortName,
            String countryShortName,
            String name
    ) {
        try {
            String townString = StringUtils.isNotBlank(town) ? town.trim().toLowerCase().replace(" ", "-") : "-";
            String stateShortNameString = StringUtils.isNotBlank(stateShortName) ? stateShortName.trim().toLowerCase() : "-";

            /*
             * Note: Same Display Name at same location will generate same webLocation.
             * You might need to redo this with some randomness in URL.
             */
            String webLocation = "/"
                    + countryShortName.toLowerCase()
                    + "/"
                    + townString
                    + "-"
                    + stateShortNameString
                    + "/"
                    + name.trim().toLowerCase().replaceAll("[^a-zA-Z]", "-");

            return webLocationSanitize(webLocation);
        } catch (Exception e) {
            LOG.error("Failed creating Web Location for store at town={} stateShortName={}", town, stateShortName);
            throw e;
        }
    }

    private String webLocationSanitize(String webLocation) {
        /*
         * Since empty townString and stateShortNameString can contain '-',
         * hence replacing two consecutive '-' with a blank and little more sanitation.
         */
        webLocation = webLocation.replaceAll("--", "").replaceAll("/-/", "/");
        if (webLocation.endsWith("-")) {
            webLocation = webLocation.substring(0, webLocation.length() - 1);
        }

        return webLocation;
    }

    public List<JsonBusiness> findDistinctBizWithMatchingName(String businessName) {
        List<JsonBusiness> jsonBusinesses = new ArrayList<>();
        List<BizNameEntity> bizNames = bizNameManager.findAllBizWithMatchingName(businessName);
        for (BizNameEntity bizName : bizNames) {
            JsonBusiness jsonBusiness = new JsonBusiness()
                    .setBizId(bizName.getId())
                    .setBizName(bizName.getBusinessName());

            jsonBusinesses.add(jsonBusiness);
        }

        return jsonBusinesses;
    }

    public BizNameEntity findAllBizWithMatchingName(String bizName) {
        List<BizNameEntity> bizNames = bizNameManager.findAllBizWithMatchingName(bizName);
        if(bizNames.isEmpty()) {
            return null;
        }

        return bizNames.get(0);
    }

    public boolean resetTemporarySettingsOnStoreHour(String id) {
        LOG.debug("StoreHour id={}", id);
        return storeHourManager.resetTemporarySettingsOnStoreHour(id);
    }

    public StoreHourEntity modifyOne(StoreHourEntity storeHour) {
        return storeHourManager.modifyOne(
            storeHour.getBizStoreId(),
            DayOfWeek.of(storeHour.getDayOfWeek()),
            storeHour.getTokenAvailableFrom(),
            storeHour.getStartHour(),
            storeHour.getTokenNotAvailableFrom(),
            storeHour.getEndHour(),
            storeHour.isDayClosed(),
            storeHour.isTempDayClosed(),
            storeHour.isPreventJoining(),
            storeHour.getDelayedInMinutes()
        );
    }

    @Mobile
    public void setScheduleTaskId(String codeQR, String id) {
        Assert.hasText(id, "Should not be blank");
        bizStoreManager.setScheduleTaskId(codeQR, id);
    }
}
