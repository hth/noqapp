package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.EmailValidateEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 11/25/16 10:05 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class EmailValidateManagerImpl implements EmailValidateManager {
    private static final Logger LOG = LoggerFactory.getLogger(EmailValidateManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        EmailValidateEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public EmailValidateManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(EmailValidateEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object);
    }

    @Override
    public void deleteHard(EmailValidateEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public EmailValidateEntity findByAuthenticationKey(String auth) {
        return mongoTemplate.findOne(
            query(where("AUTH").is(auth).andOperator(isNotDeleted())),
            EmailValidateEntity.class,
            TABLE
        );
    }

    @Override
    public void invalidateAllEntries(String queueUserId) {
        mongoTemplate.updateMulti(
            query(where("QID").is(queueUserId)),
            entityUpdate(update("A", false)),
            EmailValidateEntity.class
        );
    }

    @Override
    public EmailValidateEntity find(String email) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
