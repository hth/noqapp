package com.noqapp.service.transaction;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.exceptions.FailedTransactionException;

import com.mongodb.ClientSessionOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * hitender
 * 4/19/20 1:47 AM
 */
@Service
public class BusinessModificationService {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessModificationService.class);

    private MongoOperations mongoOperations;
    private MongoTransactionManager mongoTransactionManager;
    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;
    private TokenQueueManager tokenQueueManager;
    private List<ServerAddress> mongoHosts;

    @Autowired
    public BusinessModificationService(
        MongoOperations mongoOperations,
        MongoTransactionManager mongoTransactionManager,
        BizNameManager bizNameManager,
        BizStoreManager bizStoreManager,
        TokenQueueManager tokenQueueManager,
        List<ServerAddress> mongoHosts
    ) {
        this.mongoOperations = mongoOperations;
        this.mongoTransactionManager = mongoTransactionManager;
        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
        this.tokenQueueManager = tokenQueueManager;
        this.mongoHosts = mongoHosts;
    }

    public boolean isQueueStatusAtStart(String bizNameId) {
        List<BizStoreEntity> bizStores = bizStoreManager.getAllBizStores(bizNameId);
        for (BizStoreEntity bizStore : bizStores) {
            TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(bizStore.getCodeQR());
            if (QueueStatusEnum.S != tokenQueue.getQueueStatus()) {
                return false;
            }
        }

        return true;
    }

    public void changeBizNameBusinessType(String bizNameId, BusinessTypeEnum existingBusinessType, BusinessTypeEnum migrateToBusinessType) {
        //TODO(hth) this is a hack for supporting integration test
        if (mongoHosts.size() < 2) {
            try {
                bizNameManager.changeBizNameBusinessType(bizNameId, existingBusinessType, migrateToBusinessType);
                List<BizStoreEntity> bizStores = bizStoreManager.getAllBizStores(bizNameId);
                for (BizStoreEntity bizStore : bizStores) {
                    tokenQueueManager.changeStoreBusinessType(bizStore.getCodeQR(), existingBusinessType, migrateToBusinessType);
                }
                bizStoreManager.changeStoreBusinessType(bizNameId, existingBusinessType, migrateToBusinessType);

                return;
            } catch (Exception e) {
                LOG.error("Failed business type migration bizStoreId={} {} {} {}", bizNameId, existingBusinessType, migrateToBusinessType, e.getLocalizedMessage(), e);
                throw new FailedTransactionException("Failed to complete migration business");
            }
        }

        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
            .causallyConsistent(true)
            .build();

        ClientSession session = Objects.requireNonNull(mongoTransactionManager.getDbFactory()).getSession(sessionOptions);
        session.startTransaction();
        try {
            LOG.info("Update business type for bizNameId={} {} {}", bizNameId, existingBusinessType, migrateToBusinessType);
            mongoOperations.withSession(session).updateFirst(
                query(where("id").is(new ObjectId(bizNameId)).and("BT").is(existingBusinessType)),
                update("BT", migrateToBusinessType),
                BizNameEntity.class
            );

            List<BizStoreEntity> bizStores = bizStoreManager.getAllBizStores(bizNameId);
            for (BizStoreEntity bizStore : bizStores) {
                mongoOperations.withSession(session).updateFirst(
                    query(where("id").is(bizStore.getCodeQR()).and("BT").is(existingBusinessType)),
                    entityUpdate(update("BT", migrateToBusinessType)),
                    TokenQueueEntity.class
                );
            }

            mongoOperations.withSession(session).updateMulti(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameId)).and("BT").is(existingBusinessType)),
                update("BT", migrateToBusinessType),
                BizStoreEntity.class
            );

            session.commitTransaction();
        } catch (Exception e) {
            LOG.error("Failed business type migration bizStoreId={} {} {} {}", bizNameId, existingBusinessType, migrateToBusinessType, e.getLocalizedMessage(), e);
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete migration business");
        } finally {
            session.close();
        }
    }
}
