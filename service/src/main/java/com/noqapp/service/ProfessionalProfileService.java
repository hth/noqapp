package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonProfessionalProfile;
import com.noqapp.domain.json.JsonReviewList;
import com.noqapp.domain.types.CommonStatusEnum;
import com.noqapp.repository.ProfessionalProfileManager;
import com.noqapp.repository.UserProfileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * hitender
 * 5/31/18 11:03 AM
 */
@Service
public class ProfessionalProfileService {
    private static final Logger LOG = LoggerFactory.getLogger(ProfessionalProfileService.class);

    private ProfessionalProfileManager professionalProfileManager;
    private UserProfileManager userProfileManager;
    private ReviewService reviewService;

    @Autowired
    public ProfessionalProfileService(
        ProfessionalProfileManager professionalProfileManager,
        UserProfileManager userProfileManager,
        ReviewService reviewService
    ) {
        this.professionalProfileManager = professionalProfileManager;
        this.userProfileManager = userProfileManager;
        this.reviewService = reviewService;
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

    @Mobile
    public JsonProfessionalProfile findByWebProfileIdAsJson(String webProfileId) {
        ProfessionalProfileEntity professionalProfile = findByWebProfileId(webProfileId);
        return getJsonProfessionalProfile(professionalProfile);
    }

    private JsonProfessionalProfile getJsonProfessionalProfile(ProfessionalProfileEntity professionalProfile) {
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(professionalProfile.getQueueUserId());
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
            .setDataDictionary(professionalProfile.getDataDictionary())
            .setReviews(reviews)
            .setManagerAtStoreCodeQRs(professionalProfile.getManagerAtStoreCodeQRs())
            .setFormVersion(professionalProfile.getFormVersion());
    }

    public ProfessionalProfileEntity findByQid(String qid) {
        return professionalProfileManager.findOne(qid);
    }

    public ProfessionalProfileEntity findByQidAndRemoveAnySoftDelete(String qid) {
        ProfessionalProfileEntity professionalProfile = findByQid(qid);
        if (professionalProfile.isDeleted()) {
            professionalProfile = professionalProfileManager.removeMarkedAsDeleted(qid);
        }

        return professionalProfile;
    }

    @Mobile
    public JsonProfessionalProfile getJsonProfessionalProfileByQid(String qid) {
        ProfessionalProfileEntity professionalProfile = professionalProfileManager.findOne(qid);
        return getJsonProfessionalProfile(professionalProfile);
    }

    public void save(ProfessionalProfileEntity professionalProfile) {
        professionalProfileManager.save(professionalProfile);
    }
}
