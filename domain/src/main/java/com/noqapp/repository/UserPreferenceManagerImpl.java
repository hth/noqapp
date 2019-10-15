package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.types.CommunicationModeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentMethodEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * User: hitender
 * Date: 11/19/16 1:55 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public final class UserPreferenceManagerImpl implements UserPreferenceManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        UserPreferenceEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserPreferenceManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserPreferenceEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public UserPreferenceEntity findById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findById(id, UserPreferenceEntity.class, TABLE);
    }

    @Override
    public UserPreferenceEntity findByQueueUserId(String qid) {
        return mongoTemplate.findOne(query(where("QID").is(qid)), UserPreferenceEntity.class, TABLE);
    }

    @Override
    public UserPreferenceEntity changePromotionalSMS(String qid, CommunicationModeEnum communicationMode) {
        return mongoTemplate.findAndModify(
            query(where("QID").is(qid)),
            entityUpdate(update("PS", communicationMode)),
            FindAndModifyOptions.options().returnNew(true),
            UserPreferenceEntity.class,
            TABLE
        );
    }

    @Override
    public UserPreferenceEntity changeFirebaseNotification(String qid, CommunicationModeEnum communicationMode) {
        return mongoTemplate.findAndModify(
            query(where("QID").is(qid)),
            entityUpdate(update("FN", communicationMode)),
            FindAndModifyOptions.options().returnNew(true),
            UserPreferenceEntity.class,
            TABLE
        );
    }

    @Override
    public UserPreferenceEntity updateOrderPreference(String qid, DeliveryModeEnum deliveryMode, PaymentMethodEnum paymentMethod, String userAddressId) {
        return mongoTemplate.findAndModify(
            query(where("QID").is(qid)),
            entityUpdate(update("DM", deliveryMode).set("PM", paymentMethod).set("UAI", userAddressId)),
            FindAndModifyOptions.options().returnNew(true),
            UserPreferenceEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteHard(UserPreferenceEntity object) {
        mongoTemplate.remove(object, TABLE);
    }
}

