package com.noqapp.medical.service;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.medical.JsonUserMedicalProfile;
import com.noqapp.medical.domain.UserMedicalProfileEntity;
import com.noqapp.medical.domain.UserMedicalProfileHistoryEntity;
import com.noqapp.medical.repository.UserMedicalProfileHistoryManager;
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
    private UserMedicalProfileHistoryManager userMedicalProfileHistoryManager;

    @Autowired
    public UserMedicalProfileService(
        UserMedicalProfileManager userMedicalProfileManager,
        UserMedicalProfileHistoryManager userMedicalProfileHistoryManager
    ) {
        this.userMedicalProfileManager = userMedicalProfileManager;
        this.userMedicalProfileHistoryManager = userMedicalProfileHistoryManager;
    }

    public void save(UserMedicalProfileEntity userMedicalProfile) {
        UserMedicalProfileEntity history = findOne(userMedicalProfile.getQueueUserId());
        if (null != history) {
            UserMedicalProfileHistoryEntity userMedicalProfileHistory = new UserMedicalProfileHistoryEntity()
                .setQueueUserId(history.getQueueUserId())
                .setBloodType(history.getBloodType())
                .setOccupation(history.getOccupation())
                .setPastHistory(history.getPastHistory())
                .setFamilyHistory(history.getFamilyHistory())
                .setKnownAllergies(history.getKnownAllergies())
                .setMedicineAllergies(history.getMedicineAllergies())
                .setDentalAnatomy(history.getDentalAnatomy())
                .setEditedByQID(history.getEditedByQID());
            userMedicalProfileHistoryManager.save(userMedicalProfileHistory);
        }
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
                .setBloodType(userMedicalProfile.getBloodType())
                .setOccupation(userMedicalProfile.getOccupation())
                .setPastHistory(userMedicalProfile.getPastHistory())
                .setFamilyHistory(userMedicalProfile.getFamilyHistory())
                .setKnownAllergies(userMedicalProfile.getKnownAllergies())
                .setMedicineAllergies(userMedicalProfile.getMedicineAllergies())
                .setDentalAnatomy(userMedicalProfile.getDentalAnatomy())
                .setExternalMedicalReports(userMedicalProfile.getExternalMedicalReportsAsJson());
        }

        return new JsonUserMedicalProfile();
    }

    void updateDentalAnatomy(String qid, String dentalAnatomy, String diagnosedById) {
        userMedicalProfileManager.updateDentalAnatomy(qid, dentalAnatomy, diagnosedById);
    }
}
