package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.OutGoingNotificationEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 12/9/20 5:32 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class OutGoingNotificationManagerImpl implements OutGoingNotificationManager {
    private static final Logger LOG = LoggerFactory.getLogger(OutGoingNotificationManagerImpl.class);

    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        OutGoingNotificationEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public OutGoingNotificationManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(OutGoingNotificationEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(OutGoingNotificationEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public OutGoingNotificationEntity findToSend(int weekYear, int year) {
        return mongoTemplate.findOne(
            query(where("WY").is(weekYear).and("YR").is(year).and("ST").is(false).and("A").is(true)),
            OutGoingNotificationEntity.class,
            TABLE
        );
    }
}
