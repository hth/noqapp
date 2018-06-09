package com.noqapp.medical.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.medical.JsonHealthCareProfile;
import com.noqapp.medical.domain.HealthCareProfileEntity;
import com.noqapp.medical.repository.HealthCareProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 5/31/18 11:03 AM
 */
@Service
public class HealthCareProfileService {

    private HealthCareProfileManager healthCareProfileManager;

    @Autowired
    public HealthCareProfileService(HealthCareProfileManager healthCareProfileManager) {
        this.healthCareProfileManager = healthCareProfileManager;
    }

    public void createHealthCareProfile(String qid) {
        HealthCareProfileEntity healthCareProfile = healthCareProfileManager.findOne(qid);
        if (null == healthCareProfile) {
            healthCareProfile = new HealthCareProfileEntity(qid, CommonUtil.generateHexFromObjectId());
            healthCareProfileManager.save(healthCareProfile);
        } else if (healthCareProfile.isDeleted()) {
            /* Remove soft delete when this person is added again to some hospital. */
            healthCareProfileManager.removeMarkedAsDeleted(qid);
        }
    }

    /**
     * Delete valid only when license field empty or education field is empty.
     */
    public void softDeleteHealthCareProfile(String qid) {
        HealthCareProfileEntity healthCareProfile = healthCareProfileManager.findOne(qid);
        if (healthCareProfile.getLicenses().isEmpty() || healthCareProfile.getEducation().isEmpty()) {
            healthCareProfile.markAsDeleted();
            healthCareProfileManager.save(healthCareProfile);
        }
    }

    public HealthCareProfileEntity findByWebProfileId(String webProfileId) {
        return healthCareProfileManager.findByWebProfileId(webProfileId);
    }

    @Mobile
    public JsonHealthCareProfile findByWebProfileIdAsJson(String webProfileId) {
        HealthCareProfileEntity healthCareProfile = findByWebProfileId(webProfileId);
        return getJsonHealthCareProfile(healthCareProfile);
    }

    private JsonHealthCareProfile getJsonHealthCareProfile(HealthCareProfileEntity healthCareProfile) {
        //TODO this temp, must revert logic
        if (null == healthCareProfile) {
            return new JsonHealthCareProfile()
                    .setWebProfileId(healthCareProfile.getWebProfileId())
                    .setPracticeStart(healthCareProfile.getPracticeStart())
                    .setEducation(healthCareProfile.getEducationAsJson())
                    .setLicenses(healthCareProfile.getLicensesAsJson())
                    .setAwards(healthCareProfile.getAwardsAsJson())
                    .setPrescriptionDictionary(healthCareProfile.getPrescriptionDictionary())
                    .setManagerAtStoreCodeQRs(healthCareProfile.getManagerAtStoreCodeQRs());
        } else {
            return new JsonHealthCareProfile()
                    .setWebProfileId(healthCareProfile.getWebProfileId())
                    .setManagerAtStoreCodeQRs(healthCareProfile.getManagerAtStoreCodeQRs());
        }
    }

    public HealthCareProfileEntity findByQid(String qid) {
        return healthCareProfileManager.findOne(qid);
    }

    public JsonHealthCareProfile getJsonHealthCareProfileByQid(String qid) {
        HealthCareProfileEntity healthCareProfile = healthCareProfileManager.findOne(qid);
        return getJsonHealthCareProfile(healthCareProfile);
    }

    public void save(HealthCareProfileEntity healthCareProfile) {
        healthCareProfileManager.save(healthCareProfile);
    }
}
