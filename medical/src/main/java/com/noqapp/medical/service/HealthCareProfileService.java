package com.noqapp.medical.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.medical.JsonHealthCareProfile;
import com.noqapp.medical.domain.HealthCareProfileEntity;
import com.noqapp.medical.repository.HealthCareProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 5/31/18 11:03 AM
 */
@Service
public class HealthCareProfileService {

    private Environment environment;
    private HealthCareProfileManager healthCareProfileManager;

    @Autowired
    public HealthCareProfileService(
            Environment environment,
            HealthCareProfileManager healthCareProfileManager
    ) {
        this.environment = environment;
        this.healthCareProfileManager = healthCareProfileManager;
    }

    public void createHealthCareProfile(String qid) {
        HealthCareProfileEntity healthCareProfile = healthCareProfileManager.findOne(qid);
        if (null == healthCareProfile) {
            healthCareProfile = new HealthCareProfileEntity(qid, CommonUtil.generateCodeQR(environment.getProperty("build.env")));
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

    public HealthCareProfileEntity findByCodeQR(String codeQR) {
        return healthCareProfileManager.findByCodeQR(codeQR);
    }

    @Mobile
    public JsonHealthCareProfile findByCodeQRAsJson(String codeQR) {
//        HealthCareProfileEntity healthCareProfile = findByCodeQR(codeQR);
//        if (healthCareProfile != null) {
//            return new JsonHealthCareProfile()
//                    .setCodeQR(codeQR)
//                    .setPracticeStart(healthCareProfile.getPracticeStart())
//                    .setEducation(healthCareProfile.getEducationAsJson())
//                    .setLicenses(healthCareProfile.getLicensesAsJson())
//                    .setAwards(healthCareProfile.getAwardsAsJson())
//                    .setPrescriptionDictionary(healthCareProfile.getPrescriptionDictionary());
//        } else {
//            return new JsonHealthCareProfile()
//                    .setCodeQR(codeQR);
//        }

        return new JsonHealthCareProfile()
                .setCodeQR(codeQR);

    }

    public HealthCareProfileEntity findByQid(String qid) {
        return healthCareProfileManager.findOne(qid);
    }

    public void save(HealthCareProfileEntity healthCareProfile) {
        healthCareProfileManager.save(healthCareProfile);
    }
}
