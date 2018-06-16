package com.noqapp.medical.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalPhysicalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * hitender
 * 3/16/18 1:45 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class MedicalPhysicalManagerImpl implements MedicalPhysicalManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRecordManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MedicalPhysicalEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalPhysicalManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalPhysicalEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MedicalPhysicalEntity object) {

    }

    @Override
    public List<MedicalPhysicalEntity> findByQid(String qid) {
        return mongoTemplate.find(
                Query.query(where("QID").is(qid)),
                MedicalPhysicalEntity.class,
                TABLE
        );
    }
}
