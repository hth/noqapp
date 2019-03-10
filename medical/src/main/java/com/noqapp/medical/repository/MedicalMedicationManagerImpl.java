package com.noqapp.medical.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalMedicationEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 6/15/18 12:06 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class MedicalMedicationManagerImpl implements MedicalMedicationManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalMedicationManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MedicalMedicationEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalMedicationManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalMedicationEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MedicalMedicationEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(String id) {
        mongoTemplate.remove(
            query(where("id").is(new ObjectId(id))),
            MedicalMedicationEntity.class,
            TABLE
        );
    }

    @Override
    public void changePatient(String id, String queueUserId) {
        mongoTemplate.updateFirst(
            query(where("id").is(new ObjectId(id))),
            update("QID", queueUserId),
            MedicalMedicationEntity.class,
            TABLE
        );
    }

    @Override
    public MedicalMedicationEntity findOneById(String id) {
        return mongoTemplate.findOne(query(where("id").is(new ObjectId(id))), MedicalMedicationEntity.class, TABLE);
    }
}
