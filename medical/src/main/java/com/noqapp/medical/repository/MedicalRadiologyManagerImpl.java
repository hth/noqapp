package com.noqapp.medical.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.medical.LabCategoryEnum;
import com.noqapp.medical.domain.MedicalRadiologyEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 8/2/18 6:37 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class MedicalRadiologyManagerImpl implements MedicalRadiologyManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRadiologyManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        MedicalRadiologyEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalRadiologyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalRadiologyEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MedicalRadiologyEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public List<MedicalRadiologyEntity> findByIds(List<String> ids) {
        List<MedicalRadiologyEntity> medicalRadiologies = new LinkedList<>();
        for (String id : ids) {
            medicalRadiologies.add(mongoTemplate.findOne(query(where("id").is(new ObjectId(id))), MedicalRadiologyEntity.class, TABLE));
        }
        return medicalRadiologies;
    }

    @Override
    public MedicalRadiologyEntity findOne(List<String> ids, LabCategoryEnum labCategory) {
        List<MedicalRadiologyEntity> medicalRadiologies = new LinkedList<>();
        for (String id : ids) {
            medicalRadiologies.add(mongoTemplate.findOne(query(where("id").is(new ObjectId(id)).and("LC").is(labCategory)), MedicalRadiologyEntity.class, TABLE));
        }
        if (medicalRadiologies.isEmpty()) {
            return null;
        }
        return medicalRadiologies.get(0);
    }

    @Override
    public void updateWithTransactionId(String id, String transactionId) {
        mongoTemplate.updateFirst(
            query(where("id").is(new ObjectId(id))),
            Update.update("TI", transactionId),
            MedicalRadiologyEntity.class,
            TABLE
        );
    }

    @Override
    public MedicalRadiologyEntity findByTransactionId(String transactionId) {
        return mongoTemplate.findOne(
            query(where("TI").is(transactionId)),
            MedicalRadiologyEntity.class,
            TABLE
        );
    }

    @Override
    public MedicalRadiologyEntity findById(String id) {
        return mongoTemplate.findOne(
            query(where("id").is(new ObjectId(id))),
            MedicalRadiologyEntity.class,
            TABLE
        );
    }
}
