package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.ScheduledTaskEntity;

import com.mongodb.client.result.UpdateResult;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 9/10/18 8:12 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class ScheduledTaskManagerImpl implements ScheduledTaskManager {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTaskManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        ScheduledTaskEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ScheduledTaskManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ScheduledTaskEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(ScheduledTaskEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public ScheduledTaskEntity findOneById(String id) {
        return mongoTemplate.findById(id, ScheduledTaskEntity.class, TABLE);
    }

    @Override
    public void inActive(String id) {
        UpdateResult result = mongoTemplate.updateFirst(
            query(where("id").is(new ObjectId(id))),
            entityUpdate(update("A", false)),
            ScheduledTaskEntity.class,
            TABLE
        );

        LOG.info("Schedule inactive id={} ack={} modifiedCount={}", id, result.wasAcknowledged(), result.getModifiedCount());
    }
}
