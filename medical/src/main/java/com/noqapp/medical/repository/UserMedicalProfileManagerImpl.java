package com.noqapp.medical.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.UserMedicalProfileEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 5/30/18 5:12 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class UserMedicalProfileManagerImpl implements UserMedicalProfileManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserMedicalProfileManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            UserMedicalProfileEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserMedicalProfileManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserMedicalProfileEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(UserMedicalProfileEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public UserMedicalProfileEntity findOne(String qid) {
        return mongoTemplate.findOne(
                query(where("QID").is(qid)),
                UserMedicalProfileEntity.class,
                TABLE
        );
    }

    @Override
    public void updateDentalAnatomy(String qid, String dentalAnatomy, String diagnosedById) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid)),
            entityUpdate(update("DA", dentalAnatomy).set("EB", diagnosedById)),
            UserMedicalProfileEntity.class,
            TABLE
        );
    }
}
