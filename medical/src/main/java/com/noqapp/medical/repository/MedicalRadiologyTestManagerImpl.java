package com.noqapp.medical.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalRadiologyTestEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 8/2/18 6:36 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class MedicalRadiologyTestManagerImpl implements MedicalRadiologyTestManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRadiologyTestManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        MedicalRadiologyTestEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalRadiologyTestManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalRadiologyTestEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MedicalRadiologyTestEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<MedicalRadiologyTestEntity> findRadiologyTestByIds(String medicalRadiologyReferenceId) {
        return mongoTemplate.find(
            query(where("RRI").is(medicalRadiologyReferenceId)),
            MedicalRadiologyTestEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteByRadiologyReferenceId(String medicalRadiologyReferenceId) {
        mongoTemplate.remove(
            query(where("RRI").is(medicalRadiologyReferenceId)),
            MedicalRadiologyTestEntity.class,
            TABLE
        );
    }

    @Override
    public void changePatient(String medicalRadiologyReferenceId, String queueUserId) {
        mongoTemplate.findAndModify(
            query(where("RRI").is(medicalRadiologyReferenceId)),
            update("QID", queueUserId),
            MedicalRadiologyTestEntity.class,
            TABLE
        );
    }
}
