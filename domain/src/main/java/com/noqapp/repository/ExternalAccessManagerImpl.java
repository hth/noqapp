package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.ExternalAccessEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 2/4/18 11:06 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class ExternalAccessManagerImpl implements ExternalAccessManager {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalAccessManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ExternalAccessEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ExternalAccessManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ExternalAccessEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public ExternalAccessEntity findById(String id) {
        return mongoTemplate.findOne(
                query(where("_id").is(id)),
                ExternalAccessEntity.class,
                TABLE);
    }

    @Override
    public List<ExternalAccessEntity> findAll(String bizId) {
        return mongoTemplate.find(
                query(where("BN").is(bizId)).with(Sort.by(Sort.Direction.ASC, "QID")),
                ExternalAccessEntity.class,
                TABLE
        );
    }

    @Override
    public List<ExternalAccessEntity> findByQid(String qid) {
        return mongoTemplate.find(query(where("QID").is(qid)), ExternalAccessEntity.class, TABLE);
    }

    @Override
    public void deleteHard(ExternalAccessEntity object) {
        mongoTemplate.remove(object, TABLE);
    }
}
