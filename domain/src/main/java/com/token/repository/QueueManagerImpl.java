package com.token.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.QueueEntity;

/**
 * User: hitender
 * Date: 1/2/17 8:32 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class QueueManagerImpl implements QueueManager {
    private static final Logger LOG = LoggerFactory.getLogger(QueueManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            QueueEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public QueueManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(QueueEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(QueueEntity object) {

    }
}
