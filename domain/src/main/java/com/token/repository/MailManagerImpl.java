package com.token.repository;

import static com.token.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.MailEntity;
import com.token.domain.types.MailStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 11/27/16 12:46 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class MailManagerImpl implements MailManager {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MailEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public MailManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(MailEntity object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for MailEntity={}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<MailEntity> pendingMails() {
        return mongoTemplate.find(
                query(where("MS").is(MailStatusEnum.N)),
                MailEntity.class,
                TABLE
        );
    }

    @Override
    public void updateMail(String id, MailStatusEnum mailStatus) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(update("MS", mailStatus).inc("AT", 1)),
                MailEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteHard(MailEntity object) {
        mongoTemplate.remove(object);
    }
}
