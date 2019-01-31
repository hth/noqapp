package com.noqapp.medical.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterLabEntity;

import com.mongodb.client.result.DeleteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 11/16/18 3:17 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class MasterLabManagerImpl implements MasterLabManager {
    private static final Logger LOG = LoggerFactory.getLogger(MasterLabManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        MasterLabEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MasterLabManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MasterLabEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MasterLabEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public List<MasterLabEntity> findAllMatching(MedicalDepartmentEnum medicalDepartment) {
        return mongoTemplate.find(
            query(where("MD").in(medicalDepartment)),
            MasterLabEntity.class,
            TABLE
        );
    }

    @Override
    public List<MasterLabEntity> findAll() {
        return mongoTemplate.findAll(MasterLabEntity.class, TABLE);
    }

    @Override
    public long deleteMatching(HealthCareServiceEnum healthCareService) {
        DeleteResult deleteResult = mongoTemplate.remove(
            query(where("HS").is(healthCareService)),
            MasterLabEntity.class,
            TABLE
        );

        return deleteResult.getDeletedCount();
    }

    @Override
    public void deleteAll() {
        mongoTemplate.remove(new Query(), MasterLabEntity.class);
    }

    @Override
    public List<MasterLabEntity> findAllMatching(HealthCareServiceEnum healthCareService) {
        return mongoTemplate.find(
            query(where("HS").is(healthCareService)),
            MasterLabEntity.class,
            TABLE
        );
    }

    @Override
    public MasterLabEntity findOne(String productName, HealthCareServiceEnum healthCareService) {
        return mongoTemplate.findOne(
            query(where("HS").is(healthCareService).and("PN").is(productName)),
            MasterLabEntity.class,
            TABLE
        );
    }
}
