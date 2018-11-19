package com.noqapp.medical.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterPathologyEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 11/16/18 1:11 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class MasterPathologyManagerImpl implements MasterPathologyManager {
    private static final Logger LOG = LoggerFactory.getLogger(MasterPathologyManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        MasterPathologyEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MasterPathologyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MasterPathologyEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MasterPathologyEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public List<MasterPathologyEntity> findAllMatching(MedicalDepartmentEnum medicalDepartment) {
        return mongoTemplate.find(
            query(where("MD").in(medicalDepartment)),
            MasterPathologyEntity.class,
            TABLE
        );
    }
}
