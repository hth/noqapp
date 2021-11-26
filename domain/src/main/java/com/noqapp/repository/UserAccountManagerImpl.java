package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.AccountInactiveReasonEnum;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/19/16 1:42 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class UserAccountManagerImpl implements UserAccountManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        UserAccountEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserAccountManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserAccountEntity object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserAuthenticationEntity={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public UserAccountEntity getById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(id)), UserAccountEntity.class, TABLE);
    }

    @Override
    public void deleteHard(UserAccountEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public UserAccountEntity findByQueueUserId(String qid) {
        return mongoTemplate.findOne(query(where("QID").is(qid)), UserAccountEntity.class, TABLE);
    }

    @Override
    public UserAccountEntity findByUserId(String userId) {
        return mongoTemplate.findOne(query(where("UID").is(userId)), UserAccountEntity.class, TABLE);
    }

    @Override
    public void updateAccountToValidated(String id, AccountInactiveReasonEnum air) {
        mongoTemplate.updateFirst(
            query(where("id").is(id).and("AIR").is(air)),
            entityUpdate(update("A", true).set("AV", true).unset("AIR")),
            UserAccountEntity.class
        );
    }

    @Override
    public UserAccountEntity markAccountAsValid(String qid) {
       return mongoTemplate.findAndModify(
            query(where("QID").is(qid)),
            entityUpdate(update("AV", true)),
            FindAndModifyOptions.options().returnNew(true),
            UserAccountEntity.class
        );
    }

    @Override
    public long countRegisteredBetweenDates(Date from, Date to) {
        return mongoTemplate.count(
            new Query().addCriteria(where("C").gte(from).lt(to)),
            UserAccountEntity.class,
            TABLE
        );
    }

    @Override
    public boolean isPhoneValidated(String qid) {
        return mongoTemplate.exists(
            query(where("QID").is(qid).and("PV").is(true)),
            UserAccountEntity.class,
            TABLE
        );
    }

    @Override
    public boolean existWithAuth(String id) {
        return mongoTemplate.exists(
            query(where("USER_AUTHENTICATION.$id").is(new ObjectId(id))),
            UserAccountEntity.class,
            TABLE
        );
    }

    @Override
    public void updateName(String firstName, String lastName, String displayName, String qid) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            entityUpdate(update("FN", firstName).set("LN", lastName).set("DN", displayName)),
            UserAccountEntity.class,
            TABLE
        );
    }

    @Override
    public void increaseOTPCount(String qid) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            entityUpdate(new Update().inc("OC", 1)),
            UserAccountEntity.class,
            TABLE
        );
    }

    @Override
    public void resetOTPCount(String qid) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            entityUpdate(update("OC", 0)),
            UserAccountEntity.class,
            TABLE
        );
    }

    @Override
    public Stream<UserAccountEntity> getAccountsWithLimitedAccess(AccountInactiveReasonEnum accountInactiveReason) {
        return mongoTemplate.stream(
            query(where("AIR").is(accountInactiveReason)),
            UserAccountEntity.class,
            TABLE
        ).stream();
    }
}

