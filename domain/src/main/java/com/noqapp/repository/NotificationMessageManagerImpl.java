package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.NotificationMessageEntity;

import com.mongodb.client.result.UpdateResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 6/5/20 2:02 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class NotificationMessageManagerImpl implements NotificationMessageManager {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationMessageManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        NotificationMessageEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public NotificationMessageManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(NotificationMessageEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(NotificationMessageEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    public boolean increaseViewClientCount(String id) {
        UpdateResult updateFirst = mongoTemplate.updateFirst(
            query(where("id").is(id)),
            new Update().inc("VC", 1),
            NotificationMessageEntity.class,
            TABLE
        );

        return updateFirst.wasAcknowledged();
    }

    public boolean increaseViewUnregisteredCount(String id) {
        UpdateResult updateFirst = mongoTemplate.updateFirst(
            query(where("id").is(id)),
            new Update().inc("VU", 1),
            NotificationMessageEntity.class,
            TABLE
        );

        return updateFirst.wasAcknowledged();
    }

    public boolean increaseViewBusinessCount(String id) {
        UpdateResult updateFirst = mongoTemplate.updateFirst(
            query(where("id").is(id)),
            new Update().inc("VB", 1),
            NotificationMessageEntity.class,
            TABLE
        );

        return updateFirst.wasAcknowledged();
    }

    @Override
    public boolean findPreviouslySentMessages(String title, String body, String topic, String qid) {
        return mongoTemplate.exists(
            query(where("TP").is(topic).and("QID").is(qid).and("TL").is(title).and("BD").is(body).and("C").gte(DateUtil.inLastOneHour())),
            NotificationMessageEntity.class,
            TABLE
        );
    }
}
