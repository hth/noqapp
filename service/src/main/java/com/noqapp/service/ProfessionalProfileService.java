package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonProfessionalProfile;
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

    /**
     * Delete valid only when license field empty or education field is empty.
     */
    public void softDeleteProfessionalProfileProfile(String qid) {
        ProfessionalProfileEntity professionalProfile = professionalProfileManager.findOne(qid);
        if (professionalProfile.getLicenses().isEmpty() || professionalProfile.getEducation().isEmpty()) {
            professionalProfile.markAsDeleted();
            professionalProfileManager.save(professionalProfile);
        }
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
        return new JsonProfessionalProfile()
                .setWebProfileId(professionalProfile.getWebProfileId())
                .setPracticeStart(professionalProfile.getPracticeStart())
                .setEducation(professionalProfile.getEducationAsJson())
                .setLicenses(professionalProfile.getLicensesAsJson())
                .setAwards(professionalProfile.getAwardsAsJson())
                .setDataDictionary(professionalProfile.getDataDictionary())
                .setManagerAtStoreCodeQRs(professionalProfile.getManagerAtStoreCodeQRs());
    }

    public ProfessionalProfileEntity findByQid(String qid) {
        return professionalProfileManager.findOne(qid);
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
