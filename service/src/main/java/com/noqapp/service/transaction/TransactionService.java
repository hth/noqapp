package com.noqapp.service.transaction;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.StoreProductManager;
import com.noqapp.service.exceptions.FailedTransactionException;
import com.noqapp.service.payment.CashfreeService;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.result.DeleteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * hitender
 * 11/5/18 6:48 PM
 */
@Service
public class TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private MongoOperations mongoOperations;
    private MongoTransactionManager mongoTransactionManager;
    private MongoTemplate mongoTemplate;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderProductManager purchaseOrderProductManager;
    private StoreProductManager storeProductManager;
    private CashfreeService cashfreeService;

    @Autowired
    public TransactionService(
        MongoOperations mongoOperations,
        MongoTransactionManager mongoTransactionManager,
        MongoTemplate mongoTemplate,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderProductManager purchaseOrderProductManager,
        StoreProductManager storeProductManager,
        CashfreeService cashfreeService
    ) {
        this.mongoOperations = mongoOperations;
        this.mongoTransactionManager = mongoTransactionManager;
        this.mongoTemplate = mongoTemplate;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderProductManager = purchaseOrderProductManager;
        this.storeProductManager = storeProductManager;
        this.cashfreeService = cashfreeService;
    }

    public void completePurchase(PurchaseOrderEntity purchaseOrder, List<PurchaseOrderProductEntity> purchaseOrderProducts) {
        //TODO(hth) this is a hack for supporting integration test
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() != 2) {
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
            mongoOperations.withSession(session).insert(purchaseOrder);
            for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
                mongoOperations.withSession(session).insert(purchaseOrderProduct);
            }
            session.commitTransaction();
        } catch (DuplicateKeyException e) {
            LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
            throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
        } catch (Exception e) {
            LOG.error("Failed transaction bizStoreId={} qid={}", purchaseOrder.getBizStoreId(), purchaseOrder.getQueueUserId());
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }

    public void bulkProductUpdate(List<StoreProductEntity> storeProducts, String bizStoreId, String qid) {
        //TODO(hth) this is a hack for supporting integration test
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() != 2) {
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
            LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
            throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
        } catch (Exception e) {
            LOG.error("Failed transaction bizStoreId={} qid={}", bizStoreId, qid);
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }

    public PurchaseOrderEntity cancelPurchaseInitiatedByClient(String qid, String transactionId) {
        //TODO(hth) this is a hack for supporting integration test
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() != 2) {
            try {
                PurchaseOrderEntity purchaseOrder = purchaseOrderManager.cancelOrderByClient(qid, transactionId);
                if (purchaseOrder.getPaymentMode() != PaymentModeEnum.CA) {
                    cashfreeService.refundInitiatedByClient();
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
            PurchaseOrderEntity purchaseOrder = purchaseOrderManager.cancelOrderByClient(qid, transactionId);
            if (purchaseOrder.getPaymentMode() != PaymentModeEnum.CA) {
                cashfreeService.refundInitiatedByClient();
            }
            return purchaseOrder;
        } catch (Exception e) {
            LOG.error("Failed transaction to cancel placed order transactionId={} qid={}", transactionId, qid);
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }
}
