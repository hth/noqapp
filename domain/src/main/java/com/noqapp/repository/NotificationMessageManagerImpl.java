package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.NotificationMessageEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
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
}
