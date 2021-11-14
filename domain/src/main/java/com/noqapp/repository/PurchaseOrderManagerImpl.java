package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.IncidentEventEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.TransactionViaEnum;

import com.mongodb.client.result.UpdateResult;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

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

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * hitender
 * 3/29/18 2:31 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class PurchaseOrderManagerImpl implements PurchaseOrderManager {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            PurchaseOrderEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PurchaseOrderManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PurchaseOrderEntity object) {
        if (null != object.getId()) {
            object.setUpdated();
        }

        /* Add Transaction Id when empty. This gets replaced right after this step. */
        if (null == object.getTransactionId()) {
            object.setTransactionId(object.getId());
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(PurchaseOrderEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public PurchaseOrderEntity findById(String id) {
        return mongoTemplate.findById(new ObjectId(id),  PurchaseOrderEntity.class, TABLE);
    }

    @Override
    public PurchaseOrderEntity findBy(Set<String> qidSet, String codeQR, int tokenNumber) {
        return mongoTemplate.findOne(
            query(where("QID").in(qidSet).and("QR").is(codeQR).and("TN").is(tokenNumber)),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public List<PurchaseOrderEntity> findAllOpenOrder(String qid) {
        return mongoTemplate.find(
            query(where("QID").is(qid).and("DM").ne(DeliveryModeEnum.QS)
                .andOperator(
                    where("PS").ne(PurchaseOrderStateEnum.OD),
                    where("PS").ne(PurchaseOrderStateEnum.CO)
                )
            ),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public List<PurchaseOrderEntity> findAllClientOrderDelivered(int numberOfAttemptsToSendFCM) {
        return mongoTemplate.find(
            query(where("NS").is(false).and("NC").lt(numberOfAttemptsToSendFCM).and("PS").is(PurchaseOrderStateEnum.OD)),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public List<PurchaseOrderEntity> findAllPastDeliveredOrCancelledOrders(String qid, BusinessTypeEnum ignoreBusinessType) {
        return mongoTemplate.find(
            query(where("QID").is(qid).and("BT").ne(ignoreBusinessType)
                .orOperator(
                    where("PS").is(PurchaseOrderStateEnum.OD),
                    where("PS").is(PurchaseOrderStateEnum.CO)
                )
            ),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public List<PurchaseOrderEntity> findAllOpenOrderByCodeQR(String codeQR) {
        return mongoTemplate.find(
                query(where("QR").is(codeQR).and("PS").ne(PurchaseOrderStateEnum.OD)).with(Sort.by(DESC, "C")),
                PurchaseOrderEntity.class,
                TABLE);
    }

    @Override
    public List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR)).with(Sort.by(ASC, "TN")),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public List<PurchaseOrderEntity> findAllOrderByCodeQRUntil(String codeQR, Date until) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("C").lte(until)).with(Sort.by(DESC, "C")),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity findOne(String codeQR, int tokenNumber) {
        return mongoTemplate.findOne(
            query(where("QR").is(codeQR).and("TN").is(tokenNumber)),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public long countAllPlacedOrder(String codeQR) {
        return mongoTemplate.count(
            query(where("QR").is(codeQR).and("PS").is(PurchaseOrderStateEnum.PO)).with(Sort.by(DESC, "C")),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity getNext(String codeQR, String goTo, String sid) {
        PurchaseOrderEntity purchaseOrder = mongoTemplate.findOne(
            query(where("QR").is(codeQR)
                .orOperator(
                    where("PS").is(PurchaseOrderStateEnum.PO).and("SN").exists(false),
                    /*
                     * Second or condition will get you any of the skipped
                     * clients by the same server device id.
                     */
                    where("PS").is(PurchaseOrderStateEnum.PO).and("SE").exists(false).and("SID").is(sid)
                )
            ).with(Sort.by(ASC, "TN")),
            PurchaseOrderEntity.class,
            TABLE);

        if (updateWhenNextInQueueAcquired(codeQR, goTo, sid, purchaseOrder)) {
            return getNext(codeQR, goTo, sid);
        }

        return purchaseOrder;
    }

    @Override
    public PurchaseOrderEntity getThisAsNext(String codeQR, String goTo, String sid, int tokenNumber) {
        PurchaseOrderEntity purchaseOrder = mongoTemplate.findOne(
            query(where("QR").is(codeQR).and("TN").is(tokenNumber)
                .orOperator(
                    where("PS").is(PurchaseOrderStateEnum.PO).and("SN").exists(false),
                    /*
                     * Second or condition will get you any of the skipped
                     * clients by the same server device id.
                     */
                    where("PS").is(PurchaseOrderStateEnum.PO).and("SE").exists(false).and("SID").is(sid)
                )
            ),
            PurchaseOrderEntity.class,
            TABLE);

        if (updateWhenNextInQueueAcquired(codeQR, goTo, sid, purchaseOrder)) {
            /*
             * Since could not get the specific token, going back to regular
             * cycle to acquire one in the descending order.
             */
            return getNext(codeQR, goTo, sid);
        }

        return purchaseOrder;
    }

    @Override
    public PurchaseOrderEntity updateAndGetNextInQueue(
        String codeQR,
        int tokenNumber,
        PurchaseOrderStateEnum purchaseOrderState,
        String goTo,
        String sid,
        TokenServiceEnum tokenService
    ) {
        boolean status = updateServedInQueue(codeQR, goTo, tokenNumber, purchaseOrderState, sid, tokenService);
        LOG.info("serving status={} codeQR={} tokenNumber={} sid={}", status, codeQR, tokenNumber, sid);
        return getNext(codeQR, goTo, sid);
    }

    @Override
    public boolean updateServedInQueue(String codeQR, String goTo, int tokenNumber, PurchaseOrderStateEnum purchaseOrderState, String sid, TokenServiceEnum tokenService) {
        Query query;
        if (TokenServiceEnum.W == tokenService) {
            query = query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("PS").is(PurchaseOrderStateEnum.PO));
        } else {
            query = query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("PS").is(PurchaseOrderStateEnum.PO).and("SID").is(sid));
        }
        boolean status = mongoTemplate.updateFirst(
            /* Do not update if user aborted between beginning of service and before completion of service. */
            query,
            entityUpdate(
                update("SN", goTo)
                    .set("SID", sid)
                    .set("PS", PurchaseOrderStateEnum.OP)
                    .push("OS", PurchaseOrderStateEnum.OP)
                    .set("SB", new Date())),
            PurchaseOrderEntity.class,
            TABLE).getModifiedCount() > 1;
        LOG.info("serving status={} codeQR={} tokenNumber={}", status, codeQR, tokenNumber);
        return status;
    }

    private boolean updateWhenNextInQueueAcquired(String codeQR, String goTo, String sid, PurchaseOrderEntity purchaseOrder) {
        if (null != purchaseOrder) {
            /* Mark as being served. */
            UpdateResult updateResult = mongoTemplate.updateFirst(
                /* Removed additional where clause as we just did it and found one. */
                query(where("id").is(purchaseOrder.getId()).and("PS").is(PurchaseOrderStateEnum.PO)),
                entityUpdate(
                    update("SN", goTo)
                        .set("SID", sid)
                        .set("PS", PurchaseOrderStateEnum.OP)
                        .push("OS", PurchaseOrderStateEnum.OP)
                        .set("SB", new Date())),
                PurchaseOrderEntity.class,
                TABLE
            );

            LOG.info("Next to serve modified={} matched={} queue={}",
                updateResult.getModifiedCount(),
                updateResult.getMatchedCount(),
                purchaseOrder);

            if (0 == updateResult.getMatchedCount()) {
                LOG.info("Could not lock since it is already modified codeQR={} token={}, going to next in queue",
                    codeQR, purchaseOrder.getTokenNumber());

                return true;
            }
        }
        return false;
    }

    @Override
    public void increaseAttemptToSendNotificationCount(String id) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(new Update().inc("NC", 1)),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public long deleteByCodeQR(String codeQR, Date until) {
        return mongoTemplate.remove(
            query(where("QR").is(codeQR).and("C").lte(until)),
            PurchaseOrderEntity.class,
            TABLE
        ).getDeletedCount();
    }

    @Deprecated
    @Override
    public PurchaseOrderEntity cancelOrderByClient(String qid, String transactionId) {
        return mongoTemplate.findAndModify(
            query(where("TI").is(transactionId).and("QID").is(qid).and("PS").is(PurchaseOrderStateEnum.PO)),
            entityUpdate(update("PS", PurchaseOrderStateEnum.CO).push("OS", PurchaseOrderStateEnum.CO).set("PY", PaymentStatusEnum.PR)),
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity cancelOrderByClientWhenNotPaid(String qid, String transactionId) {
        return mongoTemplate.findAndModify(
            query(where("TI").is(transactionId).and("QID").is(qid)
                .and("PM").exists(false)
                .and("PS").in(
                    PurchaseOrderStateEnum.IN,
                    PurchaseOrderStateEnum.PC,
                    PurchaseOrderStateEnum.VB,
                    PurchaseOrderStateEnum.IB,
                    PurchaseOrderStateEnum.FO)),
            entityUpdate(update("PS", PurchaseOrderStateEnum.CO).push("OS", PurchaseOrderStateEnum.CO)),
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public boolean isOrderCancelled(String qid, String transactionId) {
        return mongoTemplate.exists(
            query(where("TI").is(transactionId).and("QID").is(qid).and("PS").is(PurchaseOrderStateEnum.CO)),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity markPaymentStatusAsRefund(String transactionId) {
        return mongoTemplate.findAndModify(
            query(where("TI").is(transactionId).and("PS").is(PurchaseOrderStateEnum.CO)),
            entityUpdate(update("PY", PaymentStatusEnum.PR)),
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity cancelOrderByMerchant(String qid, String transactionId) {
        return mongoTemplate.findAndModify(
            query(where("TI").is(transactionId).and("QID").is(qid)),
            entityUpdate(update("PS", PurchaseOrderStateEnum.CO).push("OS", PurchaseOrderStateEnum.CO).set("PY", PaymentStatusEnum.PR)),
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public boolean reviewService(String codeQR, int token, String qid, int ratingCount, String review, SentimentTypeEnum sentimentType) {
        Query query = query(
            where("QR").is(codeQR)
                .and("TN").is(token)
                .and("PS").ne(PurchaseOrderStateEnum.PO)
                .and("RA").is(0)
                .and("QID").is(qid));

        /* Review has to be null. If not null and the text is null then that is an issue. */
        Update update;
        if (StringUtils.isBlank(review)) {
            update = entityUpdate(update("RA", ratingCount));
        } else {
            update = entityUpdate(update("RA", ratingCount).set("RV", review).set("ST", sentimentType));
        }

        return mongoTemplate.updateFirst(query, update, PurchaseOrderEntity.class, TABLE).getModifiedCount() > 0;
    }

    @Override
    public List<PurchaseOrderEntity> findReviews(String codeQR) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("RA").gt(0)),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity findByQidAndTransactionId(String qid, String transactionId) {
        return mongoTemplate.findOne(
            query(where("TI").is(transactionId).and("QID").is(qid)
                .andOperator(
                    isActive(),
                    isNotDeleted())
            ),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity findByTransactionId(String transactionId) {
        return mongoTemplate.findOne(
            query(where("TI").is(transactionId)
                .andOperator(
                    isActive(),
                    isNotDeleted())
            ),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public boolean existsTransactionId(String transactionId) {
        return mongoTemplate.exists(
            query(where("TI").is(transactionId)),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity findByTransactionIdAndBizStore(String transactionId, String bizStoreId) {
        return mongoTemplate.findOne(
            query(where("TI").is(transactionId).and("BS").is(bizStoreId)
                .andOperator(
                    isActive(),
                    isNotDeleted())
            ),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public boolean isPaid(String transactionId) {
        return mongoTemplate.exists(
            query(where("TI").is(transactionId).and("PY").is(PaymentStatusEnum.PA)),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public PurchaseOrderEntity updateOnPaymentGatewayNotification(
        String transactionId,
        String transactionMessage,
        String transactionReferenceId,
        PaymentStatusEnum paymentStatus,
        PurchaseOrderStateEnum purchaseOrderState,
        PaymentModeEnum paymentMode
    ) {
        Update update;
        if (StringUtils.isBlank(transactionReferenceId)) {
            update = update("TM", transactionMessage)
                .set("PY", paymentStatus)
                .set("PS", purchaseOrderState).push("OS", purchaseOrderState)
                .set("PM", paymentMode)
                .set("TV", TransactionViaEnum.I);
        } else {
            update = update("TM", transactionMessage)
                .set("TR", transactionReferenceId)
                .set("PY", paymentStatus)
                .set("PS", purchaseOrderState).push("OS", purchaseOrderState)
                .set("PM", paymentMode)
                .set("TV", TransactionViaEnum.I);
        }
        return mongoTemplate.findAndModify(
            query(where("TI").is(transactionId)),
            update,
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity updateOnCashPayment(
        String transactionId,
        String transactionMessage,
        PaymentStatusEnum paymentStatus,
        PurchaseOrderStateEnum purchaseOrderState,
        PaymentModeEnum paymentMode
    ) {
        return mongoTemplate.findAndModify(
            query(where("TI").is(transactionId)),
            update("TM", transactionMessage)
                .unset("TR")
                .set("PY", paymentStatus)
                .set("PS", purchaseOrderState).push("OS", purchaseOrderState)
                .set("PM", paymentMode)
                .set("TV", TransactionViaEnum.E),
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class,
            TABLE);
    }

    @Override
    public PurchaseOrderEntity changePatient(String transactionId, UserProfileEntity userProfile, UserAddressEntity userAddress) {
        Update update;
        if (null == userAddress) {
            update = update("QID", userProfile.getQueueUserId())
                .set("CN", userProfile.getName())
                .set("CP", StringUtils.isBlank(userProfile.getGuardianPhone()) ? userProfile.getPhone() : userProfile.getGuardianPhone());
        } else {
            update = update("QID", userProfile.getQueueUserId())
                .set("CN", userProfile.getName())
                .set("AI", userAddress.getId())
                .set("CP", StringUtils.isBlank(userProfile.getGuardianPhone()) ? userProfile.getPhone() : userProfile.getGuardianPhone());
        }
        return mongoTemplate.findAndModify(query(where("TI").is(transactionId)), update, FindAndModifyOptions.options().returnNew(true), PurchaseOrderEntity.class, TABLE);
    }

    @Override
    public PurchaseOrderEntity updateWithPartialCounterPayment(
        String partialPayment,
        String transactionId,
        String bizStoreId,
        String transactionMessage,
        PaymentModeEnum paymentMode,
        String partialPaymentAcceptedByQid
    ) {
        return mongoTemplate.findAndModify(
            query(where("TI").is(transactionId).and("BS").is(bizStoreId).and("PP").exists(false)),
            update("PP", partialPayment)
                .set("PY", PaymentStatusEnum.MP)
                .set("PS", PurchaseOrderStateEnum.PO).push("OS", PurchaseOrderStateEnum.PO)
                .set("PM", paymentMode)
                .set("TM", transactionMessage)
                .set("TV", TransactionViaEnum.E)
                .set("PQ", partialPaymentAcceptedByQid),
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public PurchaseOrderEntity updateWithCounterPayment(
        String transactionId,
        String bizStoreId,
        String transactionMessage,
        PaymentModeEnum paymentMode,
        String fullPaymentAcceptedByQid
    ) {
        return mongoTemplate.findAndModify(
            query(where("TI").is(transactionId).and("BS").is(bizStoreId)),
            update("PY", PaymentStatusEnum.PA)
                /* Removed this update as order created by merchant is always set to PurchaseOrderStateEnum.PO .*/
                //.set("PS", PurchaseOrderStateEnum.PO).push("OS", PurchaseOrderStateEnum.PO)
                .set("PM", paymentMode)
                .set("TM", transactionMessage)
                .set("TV", TransactionViaEnum.E)
                .set("FQ", fullPaymentAcceptedByQid),
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public void updatePurchaseOrderWithToken(int token, String displayToken, Date expectedServiceBegin, String transactionId) {
        Update update = entityUpdate(update("TN", token).set("DT", displayToken));
        if (null != expectedServiceBegin) {
            update.set("EB", expectedServiceBegin);
        }
        mongoTemplate.updateFirst(query(where("TI").is(transactionId).and("DM").is(DeliveryModeEnum.QS)), update, PurchaseOrderEntity.class, TABLE);
    }

    @Override
    public void removePurchaseOrderForService(String transactionId) {
        mongoTemplate.remove(
            query(where("TI").is(transactionId).and("DM").is(DeliveryModeEnum.QS)),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public void cancelOrderWhenBackedAwayFromGateway(String transactionId) {
        mongoTemplate.updateFirst(
            query(where("TI").is(transactionId).and("PS").is(PurchaseOrderStateEnum.VB)
                .orOperator(
                    where("DM").is(DeliveryModeEnum.HD),
                    where("DM").is(DeliveryModeEnum.TO)
                )),
            update("PS", PurchaseOrderStateEnum.CO)
                .push("OS", PurchaseOrderStateEnum.CO)
                .set("PY", PaymentStatusEnum.PC),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public List<PurchaseOrderEntity> findByBizNameId(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId)).with(Sort.by(DESC, "C")),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public List<PurchaseOrderEntity> findPurchaseMadeUsingCoupon(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("CI").exists(true)).with(Sort.by(DESC, "C")),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public List<PurchaseOrderEntity> findByQidAndBizNameId(String qid, String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("QID").is(qid)).with(Sort.by(DESC, "C")),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public void changeItToPurchaseOrderState(String transactionId, String bizStoreId) {
        mongoTemplate.updateFirst(
            query(where("TI").is(transactionId).and("BS").is(bizStoreId)),
            new Update().set("PS", PurchaseOrderStateEnum.PO).push("OS", PurchaseOrderStateEnum.PO),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public Stream<PurchaseOrderEntity> findAllWithStream() {
        return mongoTemplate.findAll(PurchaseOrderEntity.class, TABLE).stream();
    }
}
