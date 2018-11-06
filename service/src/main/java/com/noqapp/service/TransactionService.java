package com.noqapp.service;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.service.exceptions.FailedTransactionException;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public TransactionService(
        MongoOperations mongoOperations,
        MongoTransactionManager mongoTransactionManager,
        MongoTemplate mongoTemplate,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderProductManager purchaseOrderProductManager
    ) {
        this.mongoOperations = mongoOperations;
        this.mongoTransactionManager = mongoTransactionManager;
        this.mongoTemplate = mongoTemplate;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderProductManager = purchaseOrderProductManager;
    }

    void completePurchase(PurchaseOrderEntity purchaseOrder, List<PurchaseOrderProductEntity> purchaseOrderProducts) {
        //TODO(hth) this is a hack for supporting integration test
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() != 2) {
            purchaseOrderManager.save(purchaseOrder);
            for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
                purchaseOrderProductManager.save(purchaseOrderProduct);
            }

            return;
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
        } catch (Exception e) {
            LOG.error("Failed transaction bizStoreId={} qid={}", purchaseOrder.getBizStoreId(), purchaseOrder.getQueueUserId());
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }
}
