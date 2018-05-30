package com.noqapp.medical.service;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.medical.JsonUserMedicalProfile;
import com.noqapp.medical.domain.UserMedicalProfileEntity;
import com.noqapp.medical.repository.UserMedicalProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 5/30/18 5:15 AM
 */
@Service
public class UserMedicalProfileService {

    private UserMedicalProfileManager userMedicalProfileManager;

    @Autowired
    public UserMedicalProfileService(UserMedicalProfileManager userMedicalProfileManager) {
        this.userMedicalProfileManager = userMedicalProfileManager;
    }

    public void save(UserMedicalProfileEntity userMedicalProfile) {
        userMedicalProfileManager.save(userMedicalProfile);
    }

    public UserMedicalProfileEntity findOne(String qid) {
        return userMedicalProfileManager.findOne(qid);
    }

    @Mobile
    public JsonUserMedicalProfile findOneAsJson(String qid) {
        UserMedicalProfileEntity userMedicalProfile = findOne(qid);

        if (null != userMedicalProfile) {
            return new JsonUserMedicalProfile()
                    .setBloodType(userMedicalProfile.getBloodType());
        }

        return new JsonUserMedicalProfile();
    }
}
