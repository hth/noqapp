package com.noqapp.medical.transaction;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.medical.repository.MasterLabManager;
import com.noqapp.service.exceptions.FailedTransactionException;

import com.mongodb.ClientSessionOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.result.DeleteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * hitender
 * 2018-12-11 07:05
 */
@Service
public class MedicalTransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalTransactionService.class);

    private MongoOperations mongoOperations;
    private MongoTransactionManager mongoTransactionManager;
    private MasterLabManager masterLabManager;
    private List<ServerAddress> mongoHosts;

    @Autowired
    public MedicalTransactionService(
        MongoOperations mongoOperations,
        MongoTransactionManager mongoTransactionManager,
        MasterLabManager masterLabManager,
        List<ServerAddress> mongoHosts
    ) {
        this.mongoOperations = mongoOperations;
        this.mongoTransactionManager = mongoTransactionManager;
        this.masterLabManager = masterLabManager;
        this.mongoHosts = mongoHosts;
    }

    public void bulkProductUpdate(List<MasterLabEntity> masterLabs, HealthCareServiceEnum healthCareService) {
        //TODO(hth) this is a hack for supporting integration test
        if (mongoHosts.size() < 2) {
            try {
                long deletedCount = masterLabManager.deleteMatching(healthCareService);
                for (MasterLabEntity masterLab : masterLabs) {
                    masterLabManager.save(masterLab);
                }
                LOG.info("Store product removed={} added={} healthCareService={}", deletedCount, masterLabs.size(), healthCareService);
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
            DeleteResult deleteResult = mongoOperations.withSession(session).remove(query(where("HS").is(healthCareService)), MasterLabEntity.class);
            for (MasterLabEntity masterLab : masterLabs) {
                mongoOperations.withSession(session).insert(masterLab);
            }
            session.commitTransaction();
            LOG.info("Store product removed={} added={} healthCareService={}", deleteResult.getDeletedCount(), masterLabs.size(), healthCareService);
        } catch (DuplicateKeyException e) {
            LOG.error("Reason failed {}", e.getLocalizedMessage(), e);
            throw new FailedTransactionException("Failed, found duplicate data " + CommonUtil.parseForDuplicateException(e.getLocalizedMessage()));
        } catch (Exception e) {
            LOG.error("Failed transaction healthCareService={}", healthCareService);
            session.abortTransaction();
            throw new FailedTransactionException("Failed to complete transaction");
        } finally {
            session.close();
        }
    }
}
