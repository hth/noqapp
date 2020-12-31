package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.ProfessionalProfileEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

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
public class ProfessionalProfileManagerImpl implements ProfessionalProfileManager {
    private static final Logger LOG = LoggerFactory.getLogger(ProfessionalProfileManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        ProfessionalProfileEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ProfessionalProfileManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ProfessionalProfileEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(ProfessionalProfileEntity object) {

    }

    @Override
    public boolean existsQid(String qid) {
        return mongoTemplate.exists(
            query(where("QID").is(qid)),
            ProfessionalProfileEntity.class,
            TABLE
        );
    }

    @Override
    public ProfessionalProfileEntity findOne(String qid) {
        return mongoTemplate.findOne(
            query(where("QID").is(qid)),
            ProfessionalProfileEntity.class,
            TABLE
        );
    }

    @Override
    public ProfessionalProfileEntity removeMarkedAsDeleted(String qid) {
        return mongoTemplate.findAndModify(
            query(where("QID").is(qid).and("D").is(true)),
            entityUpdate(update("D", false)),
            FindAndModifyOptions.options().returnNew(true),
            ProfessionalProfileEntity.class,
            TABLE
        );
    }

    @Override
    public ProfessionalProfileEntity findByWebProfileId(String webProfileId) {
        return mongoTemplate.findOne(
            query(where("WP").is(webProfileId)),
            ProfessionalProfileEntity.class,
            TABLE
        );
    }

    @Override
    public ProfessionalProfileEntity findByStoreCodeQR(String codeQR) {
        return mongoTemplate.findOne(
            query(where("MA").in(codeQR)),
            ProfessionalProfileEntity.class,
            TABLE
        );
    }
}
