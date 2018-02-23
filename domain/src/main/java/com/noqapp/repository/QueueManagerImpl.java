package com.noqapp.repository;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.client.result.UpdateResult;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.noqapp.repository.util.AppendAdditionalFields.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * User: hitender
 * Date: 1/2/17 8:32 PM
 */
@SuppressWarnings({
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
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() > 2) {
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
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }

        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(update("QS", QueueUserStateEnum.A).set("SB", new Date()).set("SE", new Date()).set("A", false)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity findQueuedOne(String codeQR, String did, String qid) {
        Query query;
        if (StringUtils.isNotBlank(qid)) {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("QID").is(qid).and("QS").is(QueueUserStateEnum.Q));
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
    public QueueEntity findToAbort(String codeQR, String did, String qid) {
        Query query;
        if (StringUtils.isNotBlank(qid)) {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("QS").is(QueueUserStateEnum.Q).and("QID").is(qid));
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
    public QueueEntity updateAndGetNextInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState, String goTo, String sid) {
        boolean status = updateServedInQueue(codeQR, tokenNumber, queueUserState, sid);
        LOG.info("serving status={} codeQR={} tokenNumber={} sid={}", status, codeQR, tokenNumber, sid);
        return getNext(codeQR, goTo, sid);
    }

    @Override
    public boolean updateServedInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState, String sid) {
        boolean status = mongoTemplate.updateFirst(
                /* Do not update if user aborted between beginning of service and before completion of service. */
                query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("QS").ne(QueueUserStateEnum.A).and("SID").is(sid)),
                entityUpdate(update("QS", queueUserState).set("A", false).set("SE", new Date())),
                QueueEntity.class,
                TABLE).getModifiedCount() > 1;
        LOG.info("serving status={} codeQR={} tokenNumber={}", status, codeQR, tokenNumber);
        return status;
    }

    @Override
    public QueueEntity getNext(String codeQR, String goTo, String sid) {
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setReadPreference(ReadPreference.primaryPreferred());
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }

        QueueEntity queue = mongoTemplate.findOne(
                query(where("QR").is(codeQR)
                        .orOperator(
                                where("QS").is(QueueUserStateEnum.Q).and("SN").exists(false),
                                /*
                                 * Second or condition will get you any of the skipped
                                 * clients by the same server device id.
                                 */
                                where("QS").is(QueueUserStateEnum.Q).and("SE").exists(false).and("SID").is(sid)
                        )
                ).with(new Sort(ASC, "TN")),
                QueueEntity.class,
                TABLE);

        if (updateWhenNextInQueueAcquired(codeQR, goTo, sid, queue)) {
            return getNext(codeQR, goTo, sid);
        }

        return queue;
    }

    @Override
    public QueueEntity getThisAsNext(String codeQR, String goTo, String sid, int tokenNumber) {
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setReadPreference(ReadPreference.primaryPreferred());
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }

        QueueEntity queue = mongoTemplate.findOne(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber)
                        .orOperator(
                                where("QS").is(QueueUserStateEnum.Q).and("SN").exists(false),
                                /*
                                 * Second or condition will get you any of the skipped
                                 * clients by the same server device id.
                                 */
                                where("QS").is(QueueUserStateEnum.Q).and("SE").exists(false).and("SID").is(sid)
                        )
                ),
                QueueEntity.class,
                TABLE);

        if (updateWhenNextInQueueAcquired(codeQR, goTo, sid, queue)) {
            /*
             * Since could not get the specific token, going back to regular
             * cycle to acquire one in the descending order.
             */
            return getNext(codeQR, goTo, sid);
        }

        return queue;
    }

    private boolean updateWhenNextInQueueAcquired(String codeQR, String goTo, String sid, QueueEntity queue) {
        if (null != queue) {
            /* Mark as being served. */
            UpdateResult updateResult = mongoTemplate.updateFirst(
                    /* Removed additional where clause as we just did it and found one. */
                    query(where("id").is(queue.getId()).and("QS").is(QueueUserStateEnum.Q)),
                    entityUpdate(update("SN", goTo).set("SID", sid).set("SB", new Date())),
                    QueueEntity.class,
                    TABLE
            );

            LOG.info("Next to serve modified={} matched={} queue={}",
                    updateResult.getModifiedCount(),
                    updateResult.getMatchedCount(),
                    queue);

            if (0 == updateResult.getMatchedCount()) {
                LOG.info("Could not lock since its already modified codeQR={} token={}, going to next in queue",
                        codeQR, queue.getTokenNumber());

                return true;
            }
        }
        return false;
    }

    public List<QueueEntity> findAllQueuedByDid(String did) {
        Assertions.assertTrue(StringUtils.isNotBlank(did), "DID should not be blank");
        return mongoTemplate.find(
                query(where("DID").is(did).and("QS").is(QueueUserStateEnum.Q)).with(new Sort(ASC, "C")),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllQueuedByQid(String qid) {
        return mongoTemplate.find(
                query(where("QID").is(qid).and("QS").is(QueueUserStateEnum.Q)).with(new Sort(ASC, "C")),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllNotQueuedByDid(String did) {
        Assertions.assertTrue(StringUtils.isNotBlank(did), "DID should not be blank");
        return mongoTemplate.find(
                query(where("DID").is(did).and("QS").ne(QueueUserStateEnum.Q)).with(new Sort(DESC, "C")),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllNotQueuedByQid(String qid) {
        //todo (hth) Add distinct
//        DBObject query = QueryBuilder.start("QID").is(qid).and("QS").notEquals(QueueUserStateEnum.Q).and("C").get();
//        return mongoTemplate.getCollection(TABLE).distinct("QR", query);

        return mongoTemplate.find(
                query(where("QID").is(qid).and("QS").ne(QueueUserStateEnum.Q)).with(new Sort(DESC, "C")),
                QueueEntity.class,
                TABLE);
    }

    public boolean isQueued(int tokenNumber, String codeQR) {
        return mongoTemplate.exists(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("QS").is(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllClientServiced(int numberOfAttemptsToSendFCM) {
        return mongoTemplate.find(
                query(where("NS").is(false).and("NC").lt(numberOfAttemptsToSendFCM)
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

    public long deleteByCodeQR(String codeQR) {
        return mongoTemplate.remove(
                query(where("QR").is(codeQR)),
                QueueEntity.class,
                TABLE
        ).getDeletedCount();
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
    public boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, int hoursSaved) {
        Query query;
        if (StringUtils.isNotBlank(qid)) {
            query = query(
                    where("QR").is(codeQR)
                            .and("TN").is(token)
                            .and("DID").is(did)
                            .and("QS").ne(QueueUserStateEnum.Q)
                            .and("RA").is(0)
                            .and("HR").is(0)
                            .and("QID").is(qid));
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
        ).getModifiedCount() > 0;
    }

    @Override
    public List<QueueEntity> findAllClientQueuedOrAborted(String codeQR) {
        return mongoTemplate.find(
                query(where("QR").is(codeQR)
                        .orOperator(
                                where("QS").is(QueueUserStateEnum.Q),
                                where("QS").is(QueueUserStateEnum.A))
                ),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public long countAllQueued(String codeQR) {
        return mongoTemplate.count(
                query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public long previouslyVisitedClientCount(String codeQR) {
        return mongoTemplate.count(
                query(where("QR").is(codeQR)
                        .and("VS").is(true)
                        .and("QS").ne(QueueUserStateEnum.A)),
                QueueEntity.class,
                TABLE);
    }

    @Override
    public long newVisitClientCount(String codeQR) {
        return mongoTemplate.count(
                query(where("QR").is(codeQR)
                        .and("VS").is(false)
                        .and("QS").ne(QueueUserStateEnum.A)),
                QueueEntity.class,
                TABLE);
    }

    @Override
    public QueueEntity findQueuedByPhone(String codeQR, String phone) {
        return mongoTemplate.findOne(
                query(where("QR").is(codeQR).and("PH").is(phone)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public void addPhoneNumberToExistingQueue(int token, String codeQR, String did, String customerPhone) {
        mongoTemplate.updateFirst(
            query(where("QR").is(codeQR).and("DID").is(did).and("TN").is(token)),
                entityUpdate(update("PH", customerPhone)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public long markAllAbortWhenQueueClosed(String codeQR, String serverDeviceId) {
        UpdateResult updateResult = mongoTemplate.updateMulti(
                query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q)),
                entityUpdate(
                        update("QS", QueueUserStateEnum.A)
                                .set("SID", serverDeviceId)
                                .set("SB", new Date())
                                .set("SE", new Date())
                                .set("A", false)
                ),
                QueueEntity.class,
                TABLE
        );

        return updateResult.getModifiedCount();
    }
}
