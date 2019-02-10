package com.noqapp.medical.repository;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;

import org.bson.types.ObjectId;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 3/16/18 1:25 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class MedicalRecordManagerImpl implements MedicalRecordManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRecordManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MedicalRecordEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalRecordManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalRecordEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public List<MedicalRecordEntity> historicalRecords(String qid, int limit) {
        return mongoTemplate.find(
                query(where("QID").is(qid).and("DBI").exists(true)).limit(limit).with(new Sort(ASC, "C")),
                MedicalRecordEntity.class,
                TABLE
        );
    }

    @Override
    public MedicalRecordEntity findById(String id) {
        return mongoTemplate.findById(new ObjectId(id), MedicalRecordEntity.class);
    }

    @Override
    public void deleteHard(MedicalRecordEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<MedicalRecordEntity> findByFollowUpWithoutNotificationSent(int afterHour, int beforeHour) {
        DateTime now = DateUtil.now();
        return mongoTemplate.find(
            query(where("FP").exists(true).and("NF").is(false).and("C").gte(now.minusHours(afterHour)).lt(now.minusHours(beforeHour))),
            MedicalRecordEntity.class,
            TABLE
        );
    }

    @Override
    public List<MedicalRecordEntity> findAllFollowUp(String codeQR) {
        DateTime midnightNow = DateUtil.midnight(DateUtil.now());
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("FP").gte(midnightNow).lte(midnightNow.plusDays(3))),
            MedicalRecordEntity.class,
            TABLE
        );
    }

    @Override
    public MedicalRecordEntity findOne() {
        return mongoTemplate.find(
            query(where("DBI").exists(true)),
            MedicalRecordEntity.class,
            TABLE
        ).get(0);
    }

    @Override
    public void addTransactionId(String recordReferenceId, String transactionId) {
        mongoTemplate.updateFirst(
            query(where("id").is(recordReferenceId)),
            new Update().addToSet("TIS", transactionId),
            MedicalRecordEntity.class,
            TABLE
        );
    }

    @Override
    public void addMedicalMedicationId(String recordReferenceId, String medicalMedicationId) {
        mongoTemplate.updateFirst(
                query(where("id").is(recordReferenceId)),
                update("MI", medicalMedicationId),
                MedicalRecordEntity.class,
                TABLE
        );
    }

    @Override
    public void addMedicalLaboratoryId(String recordReferenceId, String medicalLaboratoryId) {
        mongoTemplate.updateFirst(
                query(where("id").is(recordReferenceId)),
                update("LI", medicalLaboratoryId),
                MedicalRecordEntity.class,
                TABLE
        );
    }

    @Override
    public void addMedicalRadiologiesId(String recordReferenceId, String medicalRadiologyId) {
        mongoTemplate.updateFirst(
                query(where("id").is(recordReferenceId)),
                new Update().addToSet("RI", medicalRadiologyId),
                MedicalRecordEntity.class,
                TABLE
        );
    }
}
