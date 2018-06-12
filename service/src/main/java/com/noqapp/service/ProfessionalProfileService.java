package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.medical.JsonProfessionalProfile;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.repository.ProfessionalProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 5/31/18 11:03 AM
 */
@Service
public class ProfessionalProfileService {

    private ProfessionalProfileManager professionalProfileManager;

    @Autowired
    public ProfessionalProfileService(ProfessionalProfileManager professionalProfileManager) {
        this.professionalProfileManager = professionalProfileManager;
    }

    public void createHealthCareProfile(String qid) {
        ProfessionalProfileEntity healthCareProfile = professionalProfileManager.findOne(qid);
        if (null == healthCareProfile) {
            healthCareProfile = new ProfessionalProfileEntity(qid, CommonUtil.generateHexFromObjectId());
            professionalProfileManager.save(healthCareProfile);
        } else if (healthCareProfile.isDeleted()) {
            /* Remove soft delete when this person is added again to some hospital. */
            professionalProfileManager.removeMarkedAsDeleted(qid);
        }
    }

    /**
     * Delete valid only when license field empty or education field is empty.
     */
    public void softDeleteHealthCareProfile(String qid) {
        ProfessionalProfileEntity healthCareProfile = professionalProfileManager.findOne(qid);
        if (healthCareProfile.getLicenses().isEmpty() || healthCareProfile.getEducation().isEmpty()) {
            healthCareProfile.markAsDeleted();
            professionalProfileManager.save(healthCareProfile);
        }
    }

    public ProfessionalProfileEntity findByWebProfileId(String webProfileId) {
        return professionalProfileManager.findByWebProfileId(webProfileId);
    }

    @Mobile
    public JsonProfessionalProfile findByWebProfileIdAsJson(String webProfileId) {
        ProfessionalProfileEntity healthCareProfile = findByWebProfileId(webProfileId);
        return getJsonHealthCareProfile(healthCareProfile);
    }

    private JsonProfessionalProfile getJsonHealthCareProfile(ProfessionalProfileEntity healthCareProfile) {
        //TODO this temp, must revert logic
        if (null == healthCareProfile) {
            return new JsonProfessionalProfile()
                    .setWebProfileId(healthCareProfile.getWebProfileId())
                    .setPracticeStart(healthCareProfile.getPracticeStart())
                    .setEducation(healthCareProfile.getEducationAsJson())
                    .setLicenses(healthCareProfile.getLicensesAsJson())
                    .setAwards(healthCareProfile.getAwardsAsJson())
                    .setDataDictionary(healthCareProfile.getDataDictionary())
                    .setManagerAtStoreCodeQRs(healthCareProfile.getManagerAtStoreCodeQRs());
        } else {
            return new JsonProfessionalProfile()
                    .setWebProfileId(healthCareProfile.getWebProfileId())
                    .setManagerAtStoreCodeQRs(healthCareProfile.getManagerAtStoreCodeQRs());
        }
    }

    public ProfessionalProfileEntity findByQid(String qid) {
        return professionalProfileManager.findOne(qid);
    }

    public JsonProfessionalProfile getJsonHealthCareProfileByQid(String qid) {
        ProfessionalProfileEntity healthCareProfile = professionalProfileManager.findOne(qid);
        return getJsonHealthCareProfile(healthCareProfile);
    }

    public void save(ProfessionalProfileEntity healthCareProfile) {
        professionalProfileManager.save(healthCareProfile);
    }
}
