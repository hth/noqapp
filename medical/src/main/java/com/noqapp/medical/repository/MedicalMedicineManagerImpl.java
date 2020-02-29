package com.noqapp.medical.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalMedicineEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 6/15/18 12:07 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class MedicalMedicineManagerImpl implements MedicalMedicineManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalMedicineManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MedicalMedicineEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalMedicineManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalMedicineEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MedicalMedicineEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<MedicalMedicineEntity> findByQid(String qid) {
        return mongoTemplate.find(
                query(where("QID").is(qid)),
                MedicalMedicineEntity.class,
                TABLE
        );
    }

    @Override
    public List<MedicalMedicineEntity> findByMedicationRefId(String medicalMedicineReferenceId) {
        return mongoTemplate.find(
                query(where("MRI").is(medicalMedicineReferenceId)),
                MedicalMedicineEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteByMedicationRefId(String medicalMedicineReferenceId) {
        mongoTemplate.remove(
            query(where("MRI").is(medicalMedicineReferenceId)),
            MedicalMedicineEntity.class,
            TABLE
        );
    }

    @Override
    public List<MedicalMedicineEntity> findByIds(List<String> ids) {
        List<MedicalMedicineEntity> medicalMedicines = new LinkedList<>();
        for (String id : ids) {
            medicalMedicines.add(mongoTemplate.findById(id, MedicalMedicineEntity.class, TABLE));
        }
        return medicalMedicines;
    }

    @Override
    public void changePatient(String medicalMedicineReferenceId, String queueUserId) {
        mongoTemplate.findAndModify(
            query(where("MRI").is(medicalMedicineReferenceId)),
            update("QID", queueUserId),
            MedicalMedicineEntity.class,
            TABLE
        );
    }
}
