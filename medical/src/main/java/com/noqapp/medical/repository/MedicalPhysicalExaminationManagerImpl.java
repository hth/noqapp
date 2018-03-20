package com.noqapp.medical.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.MedicalPhysicalExaminationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * hitender
 * 3/16/18 2:20 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class MedicalPhysicalExaminationManagerImpl implements MedicalPhysicalExaminationManager {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRecordManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MedicalPhysicalExaminationEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MedicalPhysicalExaminationManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MedicalPhysicalExaminationEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(MedicalPhysicalExaminationEntity object) {

    }

    @Override
    public List<MedicalPhysicalExaminationEntity> findByRefId(String referenceId) {
        return mongoTemplate.find(
                query(where("MP").is(referenceId)),
                MedicalPhysicalExaminationEntity.class,
                TABLE
        );
    }

    @Override
    public void updateWithMedicalPhysicalReferenceId(String id, String referenceId) {
        mongoTemplate.updateFirst(
                query(where("_id").is(id)),
                entityUpdate(update("MP", referenceId)),
                MedicalPhysicalExaminationEntity.class,
                TABLE
        );
    }
}
