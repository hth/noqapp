package com.token.repository;

import static com.token.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.QueueEntity;
import com.token.domain.types.QueueStateEnum;

import java.util.List;

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
            query = query(where("QR").is(codeQR).and("DID").is(did).and("RID").is(rid));
        } else {
            query = query(where("QR").is(codeQR).and("DID").is(did));
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

    @Override
    public QueueEntity updateAndGetNextInQueue(String codeQR, int tokenNumber, QueueStateEnum queueState) {
        boolean status = mongoTemplate.updateFirst(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber)),
                entityUpdate(update("QS", queueState).set("A", false)),
                QueueEntity.class).getN() > 1;
        LOG.debug("serving status={} codeQR={} tokenNumber={}", status, codeQR, tokenNumber);
        return getNext(codeQR);
    }

    private QueueEntity getNext(String codeQR) {
//        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate.findOne(
                query(
                        where("QR").is(codeQR)
                                .and("QS").is(QueueStateEnum.Q)
                ).with(new Sort(ASC, "TN")),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllByDid(String did) {
        return mongoTemplate.find(
                query(where("DID").is(did)),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllByRid(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)),
                QueueEntity.class,
                TABLE);
    }
}
