package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public UserPreferenceEntity getById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(new ObjectId(id))), UserPreferenceEntity.class, TABLE);
    }

    @Override
    public UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile) {
        return mongoTemplate.findOne(query(where("USER_PROFILE.$id").is(new ObjectId(userProfile.getId()))),
                UserPreferenceEntity.class,
                TABLE);
    }

    @Override
    public UserPreferenceEntity getByQueueUserId(String qid) {
        return mongoTemplate.findOne(query(where("QID").is(qid)), UserPreferenceEntity.class, TABLE);
    }

    @Override
    public void deleteHard(UserPreferenceEntity object) {
        mongoTemplate.remove(object, TABLE);
    }
}

