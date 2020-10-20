package com.noqapp.service.transaction;

import static com.noqapp.common.utils.Constants.MINUTES_05;
import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.json.payment.cashfree.JsonRequestRefund;
import com.noqapp.domain.json.payment.cashfree.JsonResponseRefund;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.StoreProductManager;
import com.noqapp.service.exceptions.FailedTransactionException;
import com.noqapp.service.exceptions.PurchaseOrderCancelException;
import com.noqapp.service.exceptions.PurchaseOrderRefundPartialException;
import com.noqapp.service.exceptions.QueueAbortPaidPastDurationException;
import com.noqapp.service.payment.CashfreeService;

import com.mongodb.ClientSessionOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.result.DeleteResult;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * hitender
 * 11/5/18 6:48 PM
 */
@Service
public class TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private int preventPaidAbortBeforeHours;

    private MongoOperations mongoOperations;
    private MongoTransactionManager mongoTransactionManager;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderProductManager purchaseOrderProductManager;
    private StoreProductManager storeProductManager;
    private CashfreeService cashfreeService;
    private List<ServerAddress> mongoHosts;

    @Autowired
    public TransactionService(
        @Value("${preventPaidAbortBeforeHours:2}")
        int preventPaidAbortBeforeHours,

        MongoOperations mongoOperations,
        MongoTransactionManager mongoTransactionManager,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderProductManager purchaseOrderProductManager,
        StoreProductManager storeProductManager,
        CashfreeService cashfreeService,
        List<ServerAddress> mongoHosts
    ) {
        this.preventPaidAbortBeforeHours = preventPaidAbortBeforeHours;

        this.mongoOperations = mongoOperations;
        this.mongoTransactionManager = mongoTransactionManager;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderProductManager = purchaseOrderProductManager;
        this.storeProductManager = storeProductManager;
        this.cashfreeService = cashfreeService;
        this.mongoHosts = mongoHosts;
    }

    public void completePurchase(PurchaseOrderEntity purchaseOrder, List<PurchaseOrderProductEntity> purchaseOrderProducts) {
        //TODO(hth) this is a hack for supporting integration test
        if (mongoHosts.size() < 2) {
            try {
                purchaseOrderManager.save(purchaseOrder);
                for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
                    purchaseOrderProductManager.save(purchaseOrderProduct);
                }

                return;
            } catch (DuplicateKeyException e) {
                LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
            } catch (Exception e) {
                LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed to complete transaction");
            }
        }

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
            .causallyConsistent(true)
            .build();

        ClientSession session = Objects.requireNonNull(mongoTransactionManager.getDbFactory()).getSession(sessionOptions);
        session.startTransaction();
        try {
            LOG.info("Purchase order {} {}", purchaseOrder, purchaseOrderProducts);
            mongoOperations.withSession(session).insert(purchaseOrder);
            for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
                mongoOperations.withSession(session).insert(purchaseOrderProduct);
            }
            session.commitTransaction();
        } catch (DuplicateKeyException e) {
            LOG.error("Failed complete purchase reason={}", e.getLocalizedMessage(), e);
            throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
        } catch (Exception e) {
            LOG.error("Failed to complete purchase bizStoreId={} qid={}", purchaseOrder.getBizStoreId(), purchaseOrder.getQueueUserId(), e);
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }

    public void bulkProductUpdate(List<StoreProductEntity> storeProducts, String bizStoreId, String qid) {
        //TODO(hth) this is a hack for supporting integration test
        if (mongoHosts.size() < 2) {
            try {
                long deletedCount = storeProductManager.removedStoreProduct(bizStoreId);
                for (StoreProductEntity storeProduct : storeProducts) {
                    storeProductManager.save(storeProduct);
                }
                LOG.info("Store product removed={} added={} bizStoreId={} qid={}", deletedCount, storeProducts.size(), bizStoreId, qid);
                return;
            } catch (DuplicateKeyException e) {
                LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
            } catch (Exception e) {
                LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed to complete transaction");
            }
        }

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
            .causallyConsistent(true)
            .build();

        ClientSession session = Objects.requireNonNull(mongoTransactionManager.getDbFactory()).getSession(sessionOptions);
        session.startTransaction();
        try {
            DeleteResult deleteResult = mongoOperations.withSession(session).remove(query(where("BS").is(bizStoreId)), StoreProductEntity.class);
            for (StoreProductEntity storeProduct : storeProducts) {
                mongoOperations.withSession(session).insert(storeProduct);
            }
            session.commitTransaction();
            LOG.info("Store product removed={} added={} bizStoreId={} qid={}", deleteResult.getDeletedCount(), storeProducts.size(), bizStoreId, qid);
        } catch (DuplicateKeyException e) {
            LOG.error("Failed bulkProduct reason={}", e.getLocalizedMessage(), e);
            throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
        } catch (Exception e) {
            LOG.error("Failed bulkProduct bizStoreId={} qid={}", bizStoreId, qid, e);
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }

    public PurchaseOrderEntity cancelPurchaseInitiatedByClient(String qid, String transactionId) {
        PurchaseOrderEntity purchaseOrderBeforeCancel = purchaseOrderManager.findByTransactionId(transactionId);
        if (StringUtils.isNotBlank(purchaseOrderBeforeCancel.getPartialPayment())) {
            LOG.warn("Refund failed for when order includes partial payment {}", transactionId);
            throw new PurchaseOrderRefundPartialException("Refund failed for when order includes partial payment");
        }

        /* Invoke payment gateway when number is positive and greater than zero. */
        boolean priceIsPositive = new BigDecimal(purchaseOrderBeforeCancel.orderPriceForTransaction()).intValue() > 0;
        if (priceIsPositive && null != purchaseOrderBeforeCancel.getTransactionVia()) {
            switch (purchaseOrderBeforeCancel.getTransactionVia()) {
                case I:
                case E:
                    isTransactionCancellationAllowed(purchaseOrderBeforeCancel);
                    LOG.info("Cancel and process refund for {} {}", purchaseOrderBeforeCancel.orderPriceForTransaction(), transactionId);
                    break;
                case U:
                    LOG.error("Payment via {} {}. Cannot cancel", purchaseOrderBeforeCancel.getTransactionVia(), transactionId);
                    throw new PurchaseOrderCancelException("Cannot cancel this transaction");
                default:
                    LOG.info("Cancel and process refund for {} {}", purchaseOrderBeforeCancel.orderPriceForTransaction(), transactionId);
            }
        }

        if (null == purchaseOrderBeforeCancel.getPaymentMode()) {
            return purchaseOrderManager.cancelOrderByClientWhenNotPaid(qid, transactionId);
        }

        //TODO(hth) this is a hack for supporting integration test
        if (mongoHosts.size() < 2) {
            try {
                if (PaymentModeEnum.CA != purchaseOrderBeforeCancel.getPaymentMode() && priceIsPositive) {
                    JsonRequestRefund jsonRequestRefund = new JsonRequestRefund()
                        .setRefundAmount(purchaseOrderBeforeCancel.orderPriceForTransaction())
                        .setRefundNote("Refund initiated by client")
                        .setReferenceId(purchaseOrderBeforeCancel.getTransactionReferenceId());
                    JsonResponseRefund jsonResponseRefund = cashfreeService.refundInitiatedByClient(jsonRequestRefund);
                    LOG.info("Refund {}", jsonResponseRefund.toString());
                    if (!jsonResponseRefund.isOk()) {
                        LOG.error("Failed requesting refund for qid={} transactionId={}", qid, transactionId);
                        throw new FailedTransactionException("Failed response from Cashfree");
                    }
                }

                PurchaseOrderEntity purchaseOrder = purchaseOrderManager.cancelOrderByClient(qid, transactionId);
                if (!priceIsPositive) {
                    purchaseOrder = purchaseOrderManager.markPaymentStatusAsRefund(transactionId);
                }
                return purchaseOrder;
            } catch (DuplicateKeyException e) {
                LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
            } catch (Exception e) {
                LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed to complete transaction");
            }
        }

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
            .causallyConsistent(true)
            .build();

        ClientSession session = Objects.requireNonNull(mongoTransactionManager.getDbFactory()).getSession(sessionOptions);
        session.startTransaction();
        try {
            EnumSet<PurchaseOrderStateEnum> enumSet = EnumSet.of(PurchaseOrderStateEnum.PO, PurchaseOrderStateEnum.VB);
            PurchaseOrderEntity purchaseOrder = mongoOperations.withSession(session).findAndModify(
                query(where("TI").is(transactionId).and("QID").is(qid).and("PS").in(enumSet)),
                entityUpdate(update("PS", PurchaseOrderStateEnum.CO).push("OS", PurchaseOrderStateEnum.CO)),
                FindAndModifyOptions.options().returnNew(true),
                PurchaseOrderEntity.class
            );

            /* Initiate refund on cashfree. */
            if (null != purchaseOrder && PaymentModeEnum.CA != purchaseOrder.getPaymentMode() && priceIsPositive) {
                JsonRequestRefund jsonRequestRefund = new JsonRequestRefund()
                    .setRefundAmount(purchaseOrder.orderPriceForTransaction())
                    .setRefundNote("Refund initiated by client")
                    .setReferenceId(purchaseOrder.getTransactionReferenceId());

                JsonResponseRefund jsonResponseRefund = cashfreeService.refundInitiatedByClient(jsonRequestRefund);
                if (jsonResponseRefund.isOk()) {
                    purchaseOrder = markPaymentStatusAsRefund(transactionId, session);
                }
            } else if(!priceIsPositive) {
                purchaseOrder = markPaymentStatusAsRefund(transactionId, session);
            }
            session.commitTransaction();
            LOG.info("Refund completed for qid={} transactionId={}", qid, transactionId);
            return purchaseOrder;
        } catch (Exception e) {
            LOG.error("Failed transaction to cancel placed order transactionId={} qid={}", transactionId, qid, e);
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }

    private void isTransactionCancellationAllowed(PurchaseOrderEntity purchaseOrder) {
        switch (purchaseOrder.getBusinessType().getTransactionCancel()) {
            case HTA:
                //When less than 2 hours limit cancellation
                long hours = DateUtil.getHoursBetween(DateUtil.asLocalDateTime(purchaseOrder.getExpectedServiceBegin()), LocalDateTime.now());
                if (preventPaidAbortBeforeHours <= hours) {
                    LOG.warn("Within hour limit, prevent cancellation");
                    throw new QueueAbortPaidPastDurationException("Please contact business to cancel this transaction.");
                }

                break;
            case MEA:
                if (purchaseOrder.getOrderStates().contains(PurchaseOrderStateEnum.OP)) {
                    throw new PurchaseOrderCancelException("Order is being prepared. Cannot be cancelled, contact business to cancel this transaction.");
                }
                break;
            case TMA:
                long minutes = DateUtil.getMinutesBetween(DateUtil.asLocalDateTime(purchaseOrder.getCreated()), LocalDateTime.now());
                if (MINUTES_05 <= minutes) {
                    LOG.warn("Within minute limit, prevent cancellation");
                    throw new QueueAbortPaidPastDurationException("Please contact business to cancel this transaction.");
                }

                if (purchaseOrder.getOrderStates().contains(PurchaseOrderStateEnum.OP)) {
                    LOG.warn("Business accepted order, prevent cancellation");
                    throw new PurchaseOrderCancelException("Please contact business to cancel this transaction.");
                }
                break;
            case TNS:
                //Do nothing
                break;
            default:
                //Do nothing
        }
    }

    public PurchaseOrderEntity cancelPurchaseInitiatedByMerchant(String qid, String transactionId) {
        PurchaseOrderEntity purchaseOrderBeforeCancel = purchaseOrderManager.findByTransactionId(transactionId);

        /* Invoke payment gateway when number is positive and greater than zero. */
        boolean priceIsPositive = new BigDecimal(purchaseOrderBeforeCancel.orderPriceForTransaction()).intValue() > 0;

        if (null == purchaseOrderBeforeCancel.getPaymentMode()) {
            return purchaseOrderManager.cancelOrderByClientWhenNotPaid(qid, transactionId);
        }

        //TODO(hth) this is a hack for supporting integration test
        if (mongoHosts.size() < 2) {
            try {
                if (priceIsPositive &&
                    PurchaseOrderStateEnum.PO == purchaseOrderBeforeCancel.getPresentOrderState() &&
                    null != purchaseOrderBeforeCancel.getTransactionVia()
                ) {
                    switch (purchaseOrderBeforeCancel.getTransactionVia()) {
                        case E:
                            /* Complete refund when external as its initiated by business */
                            break;
                        case U:
                            LOG.error("Payment via {} {}. Cannot cancel", purchaseOrderBeforeCancel.getTransactionVia(), transactionId);
                            throw new PurchaseOrderCancelException("Cannot cancel this transaction");
                        case I:
                            JsonRequestRefund jsonRequestRefund = new JsonRequestRefund()
                                .setRefundAmount(purchaseOrderBeforeCancel.orderPriceForTransaction())
                                .setRefundNote("Refund initiated by merchant")
                                .setReferenceId(purchaseOrderBeforeCancel.getTransactionReferenceId());

                            JsonResponseRefund jsonResponseRefund = cashfreeService.refundInitiatedByClient(jsonRequestRefund);
                            LOG.info("Refund {}", jsonResponseRefund.toString());
                            if (!jsonResponseRefund.isOk()) {
                                LOG.error("Failed requesting refund for qid={} transactionId={}", qid, transactionId);
                                throw new FailedTransactionException("Failed response from Cashfree");
                            }
                            break;
                        default:
                            LOG.error("Reached unsupported condition reason={}", purchaseOrderBeforeCancel.getTransactionVia());
                            throw new UnsupportedOperationException("Does not support " + purchaseOrderBeforeCancel.getTransactionVia());
                    }
                }

                return purchaseOrderManager.cancelOrderByMerchant(qid, transactionId);
            } catch (DuplicateKeyException e) {
                LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
            } catch (Exception e) {
                LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed to complete transaction");
            }
        }

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
            .causallyConsistent(true)
            .build();

        ClientSession session = Objects.requireNonNull(mongoTransactionManager.getDbFactory()).getSession(sessionOptions);
        session.startTransaction();
        try {
            PurchaseOrderEntity purchaseOrder = mongoOperations.withSession(session).findAndModify(
                query(where("TI").is(transactionId).and("PS").is(PurchaseOrderStateEnum.PO)),
                entityUpdate(update("PS", PurchaseOrderStateEnum.CO).push("OS", PurchaseOrderStateEnum.CO)),
                FindAndModifyOptions.options().returnNew(true),
                PurchaseOrderEntity.class
            );

            /* Check if refund on cashfree has to be initiated. */
            if (null == purchaseOrderBeforeCancel.getTransactionVia()) {
                purchaseOrder = markPaymentStatusAsRefund(transactionId, session);
            } else if (priceIsPositive && PurchaseOrderStateEnum.CO == purchaseOrder.getPresentOrderState()) {
                switch (purchaseOrderBeforeCancel.getTransactionVia()) {
                    case E:
                        purchaseOrder = markPaymentStatusAsRefund(transactionId, session);
                        break;
                    case U:
                        LOG.error("Payment via {} {}. Cannot cancel", purchaseOrderBeforeCancel.getTransactionVia(), transactionId);
                        throw new PurchaseOrderCancelException("Cannot cancel this transaction");
                    case I:
                        JsonRequestRefund jsonRequestRefund = new JsonRequestRefund()
                            .setRefundAmount(purchaseOrder.orderPriceForTransaction())
                            .setRefundNote("Refund initiated by merchant")
                            .setReferenceId(purchaseOrder.getTransactionReferenceId());

                        JsonResponseRefund jsonResponseRefund = cashfreeService.refundInitiatedByClient(jsonRequestRefund);
                        LOG.info("Refund {}", jsonResponseRefund.toString());
                        if (!jsonResponseRefund.isOk()) {
                            LOG.error("Failed requesting refund for qid={} transactionId={}", qid, transactionId);
                            throw new FailedTransactionException("Failed response from Cashfree");
                        } else {
                            purchaseOrder = markPaymentStatusAsRefund(transactionId, session);
                        }
                        break;
                    default:
                        LOG.error("Reached unsupported condition reason={}", purchaseOrderBeforeCancel.getTransactionVia());
                        throw new UnsupportedOperationException("Does not support " + purchaseOrderBeforeCancel.getTransactionVia());

                }
            }
            session.commitTransaction();
            LOG.info("Refund completed for qid={} transactionId={}", qid, transactionId);
            return purchaseOrder;
        } catch (Exception e) {
            LOG.error("Failed transaction to cancel placed order transactionId={} qid={}", transactionId, qid, e);
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }

    private PurchaseOrderEntity markPaymentStatusAsRefund(String transactionId, ClientSession session) {
        return mongoOperations.withSession(session).findAndModify(
            query(where("TI").is(transactionId)),
            entityUpdate(update("PY", PaymentStatusEnum.PR)),
            FindAndModifyOptions.options().returnNew(true),
            PurchaseOrderEntity.class
        );
    }
}
