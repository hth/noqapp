package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.UserAuthenticationEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/19/16 1:48 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public final class UserAuthenticationManagerImpl implements UserAuthenticationManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        UserAuthenticationEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserAuthenticationManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserAuthenticationEntity object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for UserAuthenticationEntity:{} {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserAuthenticationEntity getById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(id)), UserAuthenticationEntity.class, TABLE);
    }

    @Override
    public void updateAuthenticationKey(String id, String authenticationKey) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(update("AU", authenticationKey)),
            UserAuthenticationEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteHard(UserAuthenticationEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public Stream<UserAuthenticationEntity> listAll(Date sinceThen) {
        return mongoTemplate.find(
            query(where("C").gte(sinceThen)).with(Sort.by(ASC, "C")),
            UserAuthenticationEntity.class,
            TABLE).stream();
    }
}

