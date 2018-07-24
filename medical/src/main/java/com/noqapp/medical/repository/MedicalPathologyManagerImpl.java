package com.noqapp.medical.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalPathologyEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
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
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRecordManagerImpl.class);
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
}
