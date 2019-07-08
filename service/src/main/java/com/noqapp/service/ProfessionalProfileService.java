package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonProfessionalProfile;
import com.noqapp.domain.json.JsonReviewList;
import com.noqapp.domain.json.tv.JsonProfessionalProfileTV;
import com.noqapp.domain.json.tv.JsonProfessionalProfileTVList;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.CommonStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.ProfessionalProfileManager;
import com.noqapp.repository.UserProfileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * hitender
 * 5/31/18 11:03 AM
 */
@Service
public class ProfessionalProfileService {
    private static final Logger LOG = LoggerFactory.getLogger(ProfessionalProfileService.class);

    public enum POPULATE_PROFILE {SELF, PUBLIC, TV}

    private ReviewService reviewService;
    private ProfessionalProfileManager professionalProfileManager;
    private UserProfileManager userProfileManager;
    private BusinessUserStoreManager businessUserStoreManager;
    private BizStoreManager bizStoreManager;

    @Autowired
    public ProfessionalProfileService(
        ReviewService reviewService,
        ProfessionalProfileManager professionalProfileManager,
        UserProfileManager userProfileManager,
        BusinessUserStoreManager businessUserStoreManager,
        BizStoreManager bizStoreManager
    ) {
        this.reviewService = reviewService;
        this.professionalProfileManager = professionalProfileManager;
        this.userProfileManager = userProfileManager;
        this.businessUserStoreManager = businessUserStoreManager;
        this.bizStoreManager = bizStoreManager;
    }

    /** Create professional profile or activate existing profile if marked deleted. */
    public void createProfessionalProfile(String qid) {
        ProfessionalProfileEntity professionalProfile = professionalProfileManager.findOne(qid);
        if (null == professionalProfile) {
            professionalProfile = new ProfessionalProfileEntity(qid, CommonUtil.generateHexFromObjectId());
            professionalProfileManager.save(professionalProfile);
        } else if (professionalProfile.isDeleted()) {
            /* Remove soft delete when this person is added again to some hospital. */
            professionalProfileManager.removeMarkedAsDeleted(qid);
        }
    }

    /** Delete valid only when license field empty or education field is empty. */
    public CommonStatusEnum softDeleteProfessionalProfileProfile(String qid) {
        if (professionalProfileManager.existsQid(qid)) {
            ProfessionalProfileEntity professionalProfile = professionalProfileManager.findOne(qid);
            if ((null == professionalProfile.getLicenses() || professionalProfile.getLicenses().isEmpty())
                && (null == professionalProfile.getEducation() || professionalProfile.getEducation().isEmpty())) {
                professionalProfile.markAsDeleted();
                professionalProfileManager.save(professionalProfile);
                return CommonStatusEnum.SUCCESS;
            } else {
                LOG.warn("Skip deleting professional profile qid={}", qid);
                return CommonStatusEnum.FAILURE;
            }
        }
        return CommonStatusEnum.FAILURE;
    }

    private ProfessionalProfileEntity findByWebProfileId(String webProfileId) {
        return professionalProfileManager.findByWebProfileId(webProfileId);
    }

    public ProfessionalProfileEntity findByQid(String qid) {
        return professionalProfileManager.findOne(qid);
    }

    public ProfessionalProfileEntity findByQidAndRemoveAnySoftDelete(String qid) {
        ProfessionalProfileEntity professionalProfile = findByQid(qid);
        if (null != professionalProfile && professionalProfile.isDeleted()) {
            professionalProfile = professionalProfileManager.removeMarkedAsDeleted(qid);
        }

        return professionalProfile;
    }

    public void save(ProfessionalProfileEntity professionalProfile) {
        professionalProfileManager.save(professionalProfile);
    }

    @Mobile
    public JsonProfessionalProfile findByWebProfileIdAsJson(String webProfileId) {
        ProfessionalProfileEntity professionalProfile = findByWebProfileId(webProfileId);
        return getJsonProfessionalProfile(professionalProfile, POPULATE_PROFILE.PUBLIC);
    }

    @Mobile
    public JsonProfessionalProfile getJsonProfessionalProfile(String qid, POPULATE_PROFILE populateProfile) {
        ProfessionalProfileEntity professionalProfile = professionalProfileManager.findOne(qid);
        return getJsonProfessionalProfile(professionalProfile, populateProfile);
    }

    /** This can have duplicate profiles as multiple store can have same professional person. */
    @Mobile
    public JsonProfessionalProfileTVList findAllProfessionalProfile(String bizNameId) {
        JsonProfessionalProfileTVList jsonProfessionalProfileTVList = new JsonProfessionalProfileTVList();

        List<BizStoreEntity> bizStores = bizStoreManager.getAllBizStoresActive(bizNameId);
        for (BizStoreEntity bizStore : bizStores) {
            ProfessionalProfileEntity professionalProfile = professionalProfileManager.findByStoreCodeQR(bizStore.getCodeQR());
            if (null != professionalProfile) {
                jsonProfessionalProfileTVList.addJsonProfessionalProfileTV(getJsonProfessionalProfile(professionalProfile, POPULATE_PROFILE.TV));
            } else {
                LOG.warn("Missing professional profile {} {}", bizStore.getDisplayName(), bizStore.getCodeQR());
            }
        }

        return jsonProfessionalProfileTVList;
    }

    private JsonProfessionalProfile getJsonProfessionalProfile(ProfessionalProfileEntity professionalProfile, POPULATE_PROFILE populateProfile) {
        Assert.notNull(populateProfile, "Professional profile cannot be null");
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(professionalProfile.getQueueUserId());
        switch (populateProfile) {
            case TV:
                String nameWithSalutation = userProfile.getName();
                String professionType = "";
                if (BusinessTypeEnum.DO == userProfile.getBusinessType()) {
                    BusinessUserStoreEntity businessUserStore = businessUserStoreManager.findUserManagingStoreWithUserLevel(userProfile.getQueueUserId(), UserLevelEnum.S_MANAGER);
                    BizStoreEntity bizStore = bizStoreManager.findByCodeQR(businessUserStore.getCodeQR());
                    professionType = MedicalDepartmentEnum.valueOf(bizStore.getBizCategoryId()).getDescription();

                    if (MedicalDepartmentEnum.valueOf(bizStore.getBizCategoryId()) != MedicalDepartmentEnum.PHY) {
                        nameWithSalutation = "Dr. " + userProfile.getName();
                    }
                }
                return new JsonProfessionalProfileTV()
                    .setProfileImage(userProfile.getProfileImage())
                    .setProfessionType(professionType)
                    .setName(nameWithSalutation)
                    .setWebProfileId(professionalProfile.getWebProfileId())
                    .setPracticeStart(professionalProfile.getPracticeStart())
                    .setAboutMe(professionalProfile.getAboutMe())
                    .setEducation(professionalProfile.getEducationAsJson())
                    //.setLicenses(professionalProfile.getLicensesAsJson())
                    .setAwards(professionalProfile.getAwardsAsJson())
                    //.setDataDictionary(professionalProfile.getDataDictionary())
                    //.setReviews(reviews)
                    .setManagerAtStoreCodeQRs(professionalProfile.getManagerAtStoreCodeQRs())
                    .setFormVersion(professionalProfile.getFormVersion());
            case PUBLIC:
            case SELF:
            default:
                Set<String> codeQRs = professionalProfile.getManagerAtStoreCodeQRs();

                Map<String, JsonReviewList> reviews = new HashMap<>();
                for (String codeQR : codeQRs) {
                    reviews.put(codeQR, reviewService.findQueueReviews(codeQR));
                }
                return new JsonProfessionalProfile()
                    .setName(userProfile.getName())
                    .setWebProfileId(professionalProfile.getWebProfileId())
                    .setPracticeStart(professionalProfile.getPracticeStart())
                    .setAboutMe(professionalProfile.getAboutMe())
                    .setEducation(professionalProfile.getEducationAsJson())
                    .setLicenses(professionalProfile.getLicensesAsJson())
                    .setAwards(professionalProfile.getAwardsAsJson())
                    .setDataDictionary(POPULATE_PROFILE.SELF == populateProfile ? professionalProfile.getDataDictionary() : null)
                    .setReviews(reviews)
                    .setManagerAtStoreCodeQRs(professionalProfile.getManagerAtStoreCodeQRs())
                    .setFormVersion(professionalProfile.getFormVersion());

        }
    }
}
