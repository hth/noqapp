package com.noqapp.medical.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.HealthCareProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * hitender
 * 5/30/18 3:55 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class HealthCareProfileManagerImpl implements HealthCareProfileManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRecordManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            HealthCareProfileEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public HealthCareProfileManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(HealthCareProfileEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(HealthCareProfileEntity object) {

    }

    @Override
    public boolean existsQid(String qid) {
        return mongoTemplate.exists(
                query(where("QID").is(qid)),
                HealthCareProfileEntity.class,
                TABLE
        );
    }

    @Override
    public HealthCareProfileEntity findOne(String qid) {
        return mongoTemplate.findOne(
                query(where("QID").is(qid)),
                HealthCareProfileEntity.class,
                TABLE
        );
    }

    @Override
    public void removeMarkedAsDeleted(String qid) {
        mongoTemplate.updateFirst(
                query(where("QID").is(qid)),
                entityUpdate(Update.update("D", false)),
                HealthCareProfileEntity.class,
                TABLE
        );
    }

    @Override
    public HealthCareProfileEntity findByCodeQR(String codeQR) {
        return mongoTemplate.findOne(
                query(where("QR").is(codeQR)),
                HealthCareProfileEntity.class,
                TABLE
        );
    }
}
