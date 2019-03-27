package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.result.UpdateResult;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import org.junit.jupiter.api.Assertions;

import java.util.Date;
import java.util.List;

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
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setWriteConcern(WriteConcern.W3);
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
            query = query(where("QR").is(codeQR).and("QID").is(qid).and("QS").is(QueueUserStateEnum.Q));
        } else {
            query = query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q).and("DID").is(did));
        }

        return mongoTemplate.findOne(
                query,
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity findAllQueuedOne(String codeQR, String did, String qid) {
        Query query;
        if (StringUtils.isNotBlank(qid)) {
            query = query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q)
                    .orOperator(
                            where("QID").is(qid),
                            where("GQ").is(qid)
                    ));
        } else {
            query = query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q).and("DID").is(did));
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
    public QueueEntity findByTransactionId(String codeQR, String transactionId) {
        return mongoTemplate.findOne(
            query(where("QR").is(codeQR).and("TI").is(transactionId)),
            QueueEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteReferenceToTransactionId(String codeQR, String transactionId) {
        mongoTemplate.remove(
            query(where("QR").is(codeQR).and("TI").is(transactionId)),
            QueueEntity.class,
            TABLE
        );
    }

    @Override
    public boolean onPaymentChangeToQueue(String id, int tokenNumber, Date expectedServiceBegin) {
        UpdateResult updateResult = mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(update("TN", tokenNumber).set("EB", expectedServiceBegin).set("QS", QueueUserStateEnum.Q)),
            QueueEntity.class,
            TABLE
        );

        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean doesExistsByQid(String codeQR, int tokenNumber, String qid) {
        return mongoTemplate.exists(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("QID").is(qid)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity findToAbort(String codeQR, String qid) {
        Query query;
        if (StringUtils.isNotBlank(qid)) {
            query = query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q)
                    .orOperator(
                            where("QID").is(qid),
                            where("GQ").is(qid)
                    ));
        } else {
            query = query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q));
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
    public QueueEntity updateAndGetNextInQueue(
            String codeQR,
            int tokenNumber,
            QueueUserStateEnum queueUserState,
            String goTo,
            String sid,
            TokenServiceEnum tokenService
    ) {
        boolean status = updateServedInQueue(codeQR, tokenNumber, queueUserState, sid, tokenService);
        LOG.info("serving status={} codeQR={} tokenNumber={} sid={}", status, codeQR, tokenNumber, sid);
        return getNext(codeQR, goTo, sid);
    }

    @Override
    public boolean updateServedInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState, String sid, TokenServiceEnum tokenService) {
        Query query;
        if (TokenServiceEnum.W == tokenService) {
            query = query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("QS").ne(QueueUserStateEnum.A));
        } else {
            query = query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("QS").ne(QueueUserStateEnum.A).and("SID").is(sid));
        }
        boolean status = mongoTemplate.updateFirst(
                /* Do not update if user aborted between beginning of service and before completion of service. */
                query,
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
                query(where("QS").is(QueueUserStateEnum.Q)
                        .orOperator(
                                where("QID").is(qid),
                                where("GQ").is(qid)
                        )
                ).with(new Sort(ASC, "C")),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findInAQueueByQid(String qid, String codeQR) {
        return mongoTemplate.find(
                query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q)
                        .orOperator(
                                where("QID").is(qid),
                                where("GQ").is(qid)
                        )
                ).with(new Sort(ASC, "C")),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findInAQueueByQidWithAnyQueueState(String qid, String codeQR) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR)
                .orOperator(
                    where("QID").is(qid),
                    where("GQ").is(qid)
                )
            ).with(new Sort(ASC, "C")),
            QueueEntity.class,
            TABLE);
    }

    public QueueEntity findOneQueueByQid(String qid, String codeQR) {
        return mongoTemplate.findOne(
                query(where("QR").is(codeQR).and("QID").is(qid)),
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
                query(where("QS").ne(QueueUserStateEnum.Q)
                        .orOperator(
                                where("QID").is(qid),
                                where("GQ").is(qid)
                        )
                ).with(new Sort(DESC, "C")),
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
                query(where("QR").is(codeQR).and("QS").ne(QueueUserStateEnum.I)),
                QueueEntity.class,
                TABLE
        );
    }

    public List<QueueEntity> findByCodeQRSortedByToken(String codeQR) {
        return mongoTemplate.find(
                query(where("QR").is(codeQR).and("QS").ne(QueueUserStateEnum.I)).with(new Sort(ASC, "TN")),
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
    public boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, int hoursSaved, String review, SentimentTypeEnum sentimentType) {
        Query query;
        if (StringUtils.isNotBlank(qid)) {
            query = query(
                    where("QR").is(codeQR)
                            .and("TN").is(token)
                            .and("DID").is(did)
                            .and("QS").ne(QueueUserStateEnum.Q)
                            .and("RA").is(0)
                            .and("HR").is(0)
                            .orOperator(
                                    where("QID").is(qid),
                                    where("GQ").is(qid)
                            ));
        } else {
            query = query(
                    where("QR").is(codeQR)
                            .and("TN").is(token)
                            .and("DID").is(did)
                            .and("QS").ne(QueueUserStateEnum.Q)
                            .and("RA").is(0)
                            .and("HR").is(0));
        }

        /* Review has to be null. If not null and the text is null then that is an issue. */
        Update update;
        if (StringUtils.isBlank(review)) {
            update = entityUpdate(update("RA", ratingCount).set("HR", hoursSaved));
        } else {
            update = entityUpdate(update("RA", ratingCount).set("HR", hoursSaved).set("RV", review).set("ST", sentimentType));
        }

        return mongoTemplate.updateFirst(query, update, QueueEntity.class, TABLE).getModifiedCount() > 0;
    }

    @Override
    public List<QueueEntity> findAllClientQueuedOrAborted(String codeQR) {
        return mongoTemplate.find(
                query(where("QR").is(codeQR)
                        .orOperator(
                                where("QS").is(QueueUserStateEnum.Q),
                                where("QS").is(QueueUserStateEnum.A))
                ).with(new Sort(ASC, "TN")),
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
                query(where("QR").is(codeQR).and("PH").is(phone).and("QS").is(QueueUserStateEnum.Q)),
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

    @Override
    public void updateServiceBeginTime(String id) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(update("SB", new Date())),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity changeUserInQueue(String codeQR, int tokenNumber, String existingQueueUserId, String changeToQueueUserId) {
        return mongoTemplate.findAndModify(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("QID").is(existingQueueUserId)),
                entityUpdate(update("QID", changeToQueueUserId)),
                FindAndModifyOptions.options().returnNew(true),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public List<QueueEntity> findYetToBeServed(String codeQR) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("QS").is(QueueUserStateEnum.Q)).with(new Sort(DESC, "TN")).limit(10),
            QueueEntity.class,
            TABLE
        );
    }

    @Override
    public List<QueueEntity> findReviews(String codeQR) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("RA").gt(0)),
            QueueEntity.class,
            TABLE
        );
    }

    @Override
    public List<QueueEntity> findLevelUpReviews(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("RA").gt(0)),
            QueueEntity.class,
            TABLE
        );
    }

    @Override
    public QueueEntity findOneByRecordReferenceId(String codeQR, String recordReferenceId) {
        return mongoTemplate.findOne(
            query(where("QR").is(codeQR).and("RR").is(recordReferenceId)),
            QueueEntity.class,
            TABLE
        );
    }

    @Override
    public void updateWithTransactionId(String codeQR, String qid, int tokenNumber, String transactionId) {
        UpdateResult updateResult = mongoTemplate.updateFirst(
            query(where("QR").is(codeQR).and("QID").is(qid).and("TN").is(tokenNumber).and("TI").exists(false)),
            entityUpdate(update("TI", transactionId)),
            QueueEntity.class,
            TABLE
        );

        LOG.debug("update result={}", updateResult.getModifiedCount());
    }
}
