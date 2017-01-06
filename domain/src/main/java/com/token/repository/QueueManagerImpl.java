package com.token.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
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
        mongoTemplate.insert(object, TABLE);
    }

    @Override
    public QueueEntity findOne(String codeQR, String did, String rid) {
        Query query;
        if (StringUtils.isNotBlank(rid)) {
            query = Query.query(where("QR").is(codeQR).and("DID").is(did).and("RID").is(rid));
        } else {
            query = Query.query(where("QR").is(codeQR).and("DID").is(did));
        }

        QueueEntity queue = mongoTemplate.findOne(
                query,
                QueueEntity.class,
                TABLE
        );

        if (queue == null) {
            queue = mongoTemplate.findOne(
                    query,
                    QueueEntity.class,
                    TABLE
            );
        }

        return queue;
    }

    @Override
    public void deleteHard(QueueEntity object) {

    }
}
