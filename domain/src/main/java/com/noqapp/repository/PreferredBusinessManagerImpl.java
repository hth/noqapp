package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PreferredBusinessEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 8/12/18 3:29 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class PreferredBusinessManagerImpl implements PreferredBusinessManager {
    private static final Logger LOG = LoggerFactory.getLogger(MailManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            PreferredBusinessEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PreferredBusinessManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PreferredBusinessEntity object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for PreferredBusinessEntity={}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteHard(PreferredBusinessEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public List<PreferredBusinessEntity> findAll(String bizNameId) {
        return mongoTemplate.find(
                query(where("BN").is(bizNameId)),
                PreferredBusinessEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(
            query(where("_id").is(new ObjectId(id))),
            PreferredBusinessEntity.class,
            TABLE
        );
    }

    @Override
    public boolean exists(String bizNameId, String preferredBizNameId) {
        return mongoTemplate.exists(
            query(where("BN").is(bizNameId).and("PB").is(preferredBizNameId)),
            PreferredBusinessEntity.class,
            TABLE
        );
    }
}
