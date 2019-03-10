package com.noqapp.medical.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalPathologyTestEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 7/25/18 1:35 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class MedicalPathologyTestManagerImpl implements MedicalPathologyTestManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalPathologyTestManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        MedicalPathologyTestEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalPathologyTestManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalPathologyTestEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MedicalPathologyTestEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<MedicalPathologyTestEntity> findPathologyTestByIds(String medicalPathologyReferenceId) {
        return mongoTemplate.find(
            query(where("PRI").is(medicalPathologyReferenceId)),
            MedicalPathologyTestEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteByPathologyReferenceId(String medicalPathologyReferenceId) {
        mongoTemplate.remove(
            query(where("PRI").is(medicalPathologyReferenceId)),
            MedicalPathologyTestEntity.class,
            TABLE
        );
    }

    @Override
    public void changePatient(String medicalPathologyReferenceId, String queueUserId) {
        mongoTemplate.findAndModify(
            query(where("PRI").is(medicalPathologyReferenceId)),
            Update.update("QID", queueUserId),
            MedicalPathologyTestEntity.class,
            TABLE
        );
    }
}
