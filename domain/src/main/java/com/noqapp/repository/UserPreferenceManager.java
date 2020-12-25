package com.noqapp.repository;

import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.types.CommunicationModeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentMethodEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 11/19/16 1:54 AM
 */
public interface UserPreferenceManager extends RepositoryManager<UserPreferenceEntity> {

    UserPreferenceEntity findById(String id);

    UserPreferenceEntity findByQueueUserId(String qid);

    UserPreferenceEntity changePromotionalSMS(String qid, CommunicationModeEnum communicationMode);

    UserPreferenceEntity changeFirebaseNotification(String qid, CommunicationModeEnum communicationMode);

    UserPreferenceEntity updateOrderPreference(String qid, DeliveryModeEnum deliveryMode, PaymentMethodEnum paymentMethod, String userAddressId);

    UserPreferenceEntity favorite(String qid);

    void addFavorite(String qid, String codeQR);

    void removeFavorite(String qid, String codeQR);
}

