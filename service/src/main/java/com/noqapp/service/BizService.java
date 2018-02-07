package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizCategoryEntity;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.site.JsonBusiness;
import com.noqapp.repository.BizCategoryManager;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.StoreHourManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private BizCategoryManager bizCategoryManager;
    private TokenQueueService tokenQueueService;
    private QueueService queueService;
    private BusinessUserStoreManager businessUserStoreManager;

    @Autowired
    public BizService(
            @Value("${degreeInMiles:69.172}")
            double degreeInMiles,

            @Value("${degreeInKilometers:111.321}")
            double degreeInKilometers,

            BizNameManager bizNameManager,
            BizStoreManager bizStoreManager,
            StoreHourManager storeHourManager,
            BizCategoryManager bizCategoryManager,
            TokenQueueService tokenQueueService,
            QueueService queueService,
            BusinessUserStoreManager businessUserStoreManager
    ) {
        this.degreeInMiles = degreeInMiles;
        this.degreeInKilometers = degreeInKilometers;
        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
        this.storeHourManager = storeHourManager;
        this.bizCategoryManager = bizCategoryManager;
        this.tokenQueueService = tokenQueueService;
        this.queueService = queueService;
        this.businessUserStoreManager = businessUserStoreManager;
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

    public void saveStore(BizStoreEntity bizStore) {
        bizStoreManager.save(bizStore);
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

    @Mobile
    public List<BizCategoryEntity> getBusinessCategories(String bizNameId) {
        return bizCategoryManager.getByBizNameId(bizNameId);
    }

    public Map<String, String> getBusinessCategoriesAsMap(String bizNameId) {
        List<BizCategoryEntity> bizCategories = getBusinessCategories(bizNameId);

        Map<String, String> bizCategoriesAsMap = new LinkedHashMap<>();
        for (BizCategoryEntity bizCategory : bizCategories) {
            bizCategoriesAsMap.put(bizCategory.getId(), bizCategory.getCategoryName());
        }

        return bizCategoriesAsMap;
    }

    public void addCategory(String categoryName, String bizNameId) {
        if (!existCategory(categoryName, bizNameId)) {
            BizCategoryEntity bizCategory = new BizCategoryEntity()
                    .setBizNameId(bizNameId)
                    .setCategoryName(categoryName);
            bizCategoryManager.save(bizCategory);
        }
    }

    public boolean existCategory(String categoryName, String bizNameId) {
        return bizCategoryManager.existCategory(categoryName, bizNameId);
    }

    public BizCategoryEntity findByBizCategoryId(String id) {
        return bizCategoryManager.findById(id);
    }

    public String getNameOfCategory(String bizCategoryId) {
        String categoryName = null;
        BizCategoryEntity bizCategory = findByBizCategoryId(bizCategoryId);
        if (null != bizCategory) {
            categoryName = bizCategory.getCategoryName();
        }
        return categoryName;
    }

    public void updateBizCategoryName(String bizCategoryId, String categoryName) {
        bizCategoryManager.updateBizCategoryName(bizCategoryId, categoryName);
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
            String town,
            String stateShortName,
            String countryShortNameStore,
            String name,
            String displayName,
            String storeId
    ) {
        String webLocation = computeWebLocationForStore(
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
            String town,
            String stateShortName,
            String countryShortNameStore,
            String name,
            String displayName
    ) {
        try {
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
}
