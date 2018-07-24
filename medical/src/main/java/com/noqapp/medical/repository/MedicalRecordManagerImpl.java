package com.noqapp.medical.repository;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
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
                query(where("QID").is(qid)).limit(limit).with(new Sort(ASC, "C")),
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
}
