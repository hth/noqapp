package com.noqapp.medical.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalPathologyEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 7/25/18 1:32 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class MedicalPathologyManagerImpl implements MedicalPathologyManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalPathologyManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        MedicalPathologyEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalPathologyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalPathologyEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MedicalPathologyEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(String id) {
        mongoTemplate.remove(
            query(where("id").is(new ObjectId(id))),
            MedicalPathologyEntity.class,
            TABLE
        );
    }

    @Override
    public void updateWithTransactionId(String id, String transactionId) {
        mongoTemplate.updateFirst(
            query(where("id").is(new ObjectId(id))),
            Update.update("TI", transactionId),
            MedicalPathologyEntity.class,
            TABLE
        );
    }
}
