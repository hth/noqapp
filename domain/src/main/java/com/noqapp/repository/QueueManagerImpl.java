package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.QueueUserStateEnum;

import java.util.Date;
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
        if (mongoTemplate.getDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void insert(QueueEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.insert(object, TABLE);
    }

    @Override
    public void abort(String id) {
        if (mongoTemplate.getDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }

        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(update("QS", QueueUserStateEnum.A).set("ST", new Date()).set("A", false)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity findQueuedOne(String codeQR, String did, String rid) {
        Query query;
        if (StringUtils.isNotBlank(rid)) {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("RID").is(rid).and("QS").is(QueueUserStateEnum.Q));
        } else {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("QS").is(QueueUserStateEnum.Q));
        }

        return mongoTemplate.findOne(
                query,
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity findOne(String codeQR, int tokenNumber) {
        return mongoTemplate.findOne(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity findToAbort(String codeQR, String did, String rid) {
        Query query;
        if (StringUtils.isNotBlank(rid)) {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("QS").is(QueueUserStateEnum.Q).and("RID").is(rid));
        } else {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("QS").is(QueueUserStateEnum.Q));
        }

        return mongoTemplate.findOne(
                query,
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteHard(QueueEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public QueueEntity updateAndGetNextInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState) {
        boolean status = mongoTemplate.updateFirst(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber)),
                entityUpdate(update("QS", queueUserState).set("A", false).set("ST", new Date())),
                QueueEntity.class,
                TABLE).getN() > 1;
        LOG.debug("serving status={} codeQR={} tokenNumber={}", status, codeQR, tokenNumber);
        return getNext(codeQR);
    }

    @Override
    public QueueEntity getNext(String codeQR) {
        if (mongoTemplate.getDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setReadPreference(ReadPreference.primaryPreferred());
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }

        QueueEntity queue = mongoTemplate.findOne(
                query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q).and("LO").is(false)).with(new Sort(ASC, "TN")),
                QueueEntity.class,
                TABLE);

        /* Mark as being served. */
        WriteResult writeConcern = mongoTemplate.updateFirst(
                query(where("id").is(queue.getId()).and("LO").is(false)),
                entityUpdate(update("LO", true)),
                QueueEntity.class,
                TABLE
        );

        if (writeConcern.getN() <= 0 && null != queue) {
            LOG.info("Could not lock since its already modified token={}, going to next", queue.getTokenNumber());
            return getNext(codeQR);
        }

        return queue;
    }

    public List<QueueEntity> findAllQueuedByDid(String did) {
        return mongoTemplate.find(
                query(where("DID").is(did).and("QS").is(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllQueuedByRid(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid).and("QS").is(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllNotQueuedByDid(String did) {
        return mongoTemplate.find(
                query(where("DID").is(did).and("QS").ne(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllNotQueuedByRid(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid).and("QS").ne(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public boolean isQueued(int tokenNumber, String codeQR) {
        return mongoTemplate.exists(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("QS").is(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllClientServiced(int attemptToSendNotificationCounts) {
        return mongoTemplate.find(
                query(where("NS").is(false).and("NC").lt(attemptToSendNotificationCounts)
                        .orOperator(
                                where("QS").is(QueueUserStateEnum.S),
                                where("QS").is(QueueUserStateEnum.N)
                        )
                ),
                QueueEntity.class,
                TABLE
        );
    }

    public List<QueueEntity> findByCodeQR(String codeQR) {
        return mongoTemplate.find(
                query(where("QR").is(codeQR)),
                QueueEntity.class,
                TABLE
        );
    }

    public int deleteByCodeQR(String codeQR) {
        return mongoTemplate.remove(
                query(where("QR").is(codeQR)),
                QueueEntity.class,
                TABLE
        ).getN();
    }

    @Override
    public void increaseAttemptToSendNotificationCount(String id) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(new Update().inc("NC", 1)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public boolean reviewService(String codeQR, int token, String did, String rid, int ratingCount, int hoursSaved) {
        Query query;
        if (StringUtils.isNotBlank(rid)) {
            query = query(
                    where("QR").is(codeQR)
                            .and("TN").is(token)
                            .and("DID").is(did)
                            .and("QS").ne(QueueUserStateEnum.Q)
                            .and("RA").is(0)
                            .and("HR").is(0)
                            .and("RID").is(rid));
        } else {
            query = query(
                    where("QR").is(codeQR)
                            .and("TN").is(token)
                            .and("DID").is(did)
                            .and("QS").ne(QueueUserStateEnum.Q)
                            .and("RA").is(0)
                            .and("HR").is(0));
        }

        return mongoTemplate.updateFirst(
                query,
                entityUpdate(update("RA", ratingCount).set("HR", hoursSaved)),
                QueueEntity.class,
                TABLE
        ).getN() > 0;
    }
}
