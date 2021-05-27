package com.noqapp.service;

import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonProfile;
import com.noqapp.domain.json.JsonUserPreference;
import com.noqapp.domain.types.CommunicationModeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentMethodEnum;
import com.noqapp.repository.UserAddressManager;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 11/19/16 12:45 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class UserProfilePreferenceService {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfilePreferenceService.class);

    private UserProfileManager userProfileManager;
    private UserPreferenceManager userPreferenceManager;
    private UserAddressManager userAddressManager;

    @Autowired
    public UserProfilePreferenceService(
        UserProfileManager userProfileManager,
        UserPreferenceManager userPreferenceManager,
        UserAddressManager userAddressManager
    ) {
        this.userProfileManager = userProfileManager;
        this.userPreferenceManager = userPreferenceManager;
        this.userAddressManager = userAddressManager;
    }

    public void save(UserPreferenceEntity userPreference) {
        userPreferenceManager.save(userPreference);
    }

    public UserProfileEntity findOneByMail(String mail) {
        return userProfileManager.findOneByMail(mail);
    }

    public UserPreferenceEntity findByQueueUserId(String qid) {
        return userPreferenceManager.findByQueueUserId(qid);
    }

    @Mobile
    @SuppressWarnings("unused")
    public UserProfileEntity getProfileUpdateSince(String qid, Date since) {
        return userProfileManager.getProfileUpdateSince(qid, since);
    }

    public void deleteHard(UserProfileEntity userProfile) {
        userProfileManager.deleteHard(userProfile);
    }

    public void deleteHard(UserPreferenceEntity userPreference) {
        userPreferenceManager.deleteHard(userPreference);
    }

    @Mobile
    public JsonUserPreference findUserPreferenceAsJson(String qid) {
        UserPreferenceEntity userPreference = findByQueueUserId(qid);
        return JsonUserPreference.convertToJsonUserPreference(userPreference);
    }

    @Mobile
    public UserPreferenceEntity changeNotificationSound(String qid) {
        UserPreferenceEntity userPreference = findByQueueUserId(qid);
        switch (userPreference.getFirebaseNotification()) {
            case R:
                return userPreferenceManager.changeFirebaseNotification(qid, CommunicationModeEnum.M);
            case M:
                return userPreferenceManager.changeFirebaseNotification(qid, CommunicationModeEnum.R);
            case S:
            default:
                LOG.error("Reached unsupported communication mode {}", userPreference.getFirebaseNotification());
                throw new UnsupportedOperationException("Reached unsupported communication mode " + userPreference.getFirebaseNotification().getDescription());
        }
    }

    @Mobile
    public UserPreferenceEntity changeReceivePromotionalSMS(String qid) {
        UserPreferenceEntity userPreference = findByQueueUserId(qid);
        switch (userPreference.getPromotionalSMS()) {
            case R:
                return userPreferenceManager.changePromotionalSMS(qid, CommunicationModeEnum.S);
            case S:
                return userPreferenceManager.changePromotionalSMS(qid, CommunicationModeEnum.R);
            case M:
            default:
                LOG.error("Reached unsupported communication mode {}", userPreference.getPromotionalSMS());
                throw new UnsupportedOperationException("Reached unsupported communication mode " + userPreference.getFirebaseNotification().getDescription());
        }
    }

    @Mobile
    public UserPreferenceEntity updateOrderPreference(
        String qid,
        DeliveryModeEnum deliveryMode,
        PaymentMethodEnum paymentMethod,
        String userAddressId
    ) {
        UserPreferenceEntity userPreference = findByQueueUserId(qid);
        if (null != deliveryMode) {
            userPreference.setDeliveryMode(deliveryMode);
        }

        if (null != paymentMethod) {
            userPreference.setPaymentMethod(paymentMethod);
        }

        if (StringUtils.isNotBlank(userAddressId)) {
            if (userAddressManager.doesAddressExists(userAddressId, qid)) {
                userPreference.setUserAddressId(userAddressId);
            } else {
                LOG.warn("No such user address id={} qid={}", userAddressId, qid);
            }
        }

        return userPreferenceManager.updateOrderPreference(
            qid,
            userPreference.getDeliveryMode(),
            userPreference.getPaymentMethod(),
            userPreference.getUserAddressId());
    }

    @Mobile
    public void addFavorite(String qid, String codeQR) {
        userPreferenceManager.addFavorite(qid, codeQR);
    }

    @Mobile
    public void removeFavorite(String qid, String codeQR) {
        userPreferenceManager.removeFavorite(qid, codeQR);
    }

    /** Gets profile of SOS users registered in user preferences. */
    public List<UserProfileEntity> findSOSUsers(String qid) {
        UserPreferenceEntity userPreference = userPreferenceManager.findByQueueUserId(qid);
        Set<String> sosReceiverQids = userPreference.getSosReceiverQids();

        List<UserProfileEntity> userProfiles = new LinkedList<>();
        for (String sosReceiverQid : sosReceiverQids) {
            userProfiles.add(userProfileManager.findByQueueUserId(sosReceiverQid));
        }

        return userProfiles;
    }

    public void removeSOSUser(String phoneNumber, String queueUserId) {
        UserPreferenceEntity userPreference = userPreferenceManager.findByQueueUserId(queueUserId);
        UserProfileEntity userProfile = userProfileManager.findOneByPhone(phoneNumber);
        if (null != userProfile) {
            if (userPreference.getSosReceiverQids().contains(userProfile.getQueueUserId())) {
                userPreference.getSosReceiverQids().remove(userProfile.getQueueUserId());
                userPreferenceManager.save(userPreference);
            }
        }
    }
}
