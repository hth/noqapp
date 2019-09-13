package com.noqapp.medical.repository;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MedicalRecordEntity;

import org.bson.types.ObjectId;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
            query(where("QID").is(qid)
                .orOperator(
                    where("BT").is(BusinessTypeEnum.DO).and("DBI").exists(true),
                    where("BT").is(BusinessTypeEnum.HS)
                )
            ).limit(limit).with(new Sort(ASC, "C")),
            MedicalRecordEntity.class,
            TABLE
        );
    }

    @Override
    public List<MedicalRecordEntity> historicalRecords(String qid, MedicalDepartmentEnum medicalDepartment, int limit) {
        return mongoTemplate.find(
            query(
                where("QID").is(qid).and("BT").is(BusinessTypeEnum.DO).and("BCI").is(medicalDepartment).and("DBI").exists(true)
            ).limit(limit).with(new Sort(ASC, "C")),
            MedicalRecordEntity.class,
            TABLE
        );
    }

    @Override
    public MedicalRecordEntity findById(String id) {
        return mongoTemplate.findById(new ObjectId(id), MedicalRecordEntity.class);
    }

    @Override
    public List<MedicalRecordEntity> findByCodeQRFilteredOnFieldWithinDateRange(String codeQR, String populateField, Date from, Date until) {
        Query query = query(where("QR").is(codeQR).and("C").gte(from).lt(until));
        query.fields().include(populateField).include("QID");
        return mongoTemplate.find(query, MedicalRecordEntity.class, TABLE);
    }

    @Override
    public void deleteHard(MedicalRecordEntity object) {
        mongoTemplate.remove(object);
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
    public MedicalRecordEntity findByBizNameId(String bizNameId) {
        return mongoTemplate.findOne(
            query(where("BN").is(bizNameId).and("DBI").exists(true)).with(new Sort(DESC, "C")),
            MedicalRecordEntity.class,
            TABLE
        );
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

    @Override
    public void unsetMedicalRadiology(String recordReferenceId) {
        mongoTemplate.updateFirst(
                query(where("id").is(recordReferenceId)),
                new Update().unset("RI"),
                MedicalRecordEntity.class,
                TABLE
        );
    }
}
