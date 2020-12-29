package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.PublishJobEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 12/27/20 4:56 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class PublishJobManagerImpl implements PublishJobManager {
    private static final Logger LOG = LoggerFactory.getLogger(PublishJobManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        PublishJobEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PublishJobManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PublishJobEntity object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for PublishJobEntity={}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PublishJobEntity findOne(String id) {
        return mongoTemplate.findOne(
            query(where("id").is(id)),
            PublishJobEntity.class,
            TABLE
        );
    }

    @Override
    public List<PublishJobEntity> findAll(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("D").is(false)),
            PublishJobEntity.class,
            TABLE
        );
    }

    @Override
    public void takeOffOrOnline(String id, boolean active) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            update("A", active),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteHard(PublishJobEntity object) {

    }
}
