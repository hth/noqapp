package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BusinessUserStoreEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 12/13/16 10:30 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BusinessUserStoreManagerImpl implements BusinessUserStoreManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BusinessUserStoreEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BusinessUserStoreManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BusinessUserStoreEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(BusinessUserStoreEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public boolean hasAccess(String qid, String codeQR) {
        return mongoTemplate.exists(query(where("QID").is(qid).and("QR").is(codeQR)), BusinessUserStoreEntity.class, TABLE);
    }

    //TODO support pagination
    @Override
    public List<BusinessUserStoreEntity> getQueues(String qid, int limit) {
        Query query = query(where("QID").is(qid)
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
        );

        if (limit > 0) {
            query = query.limit(limit);
        }
        return mongoTemplate.find(query, BusinessUserStoreEntity.class, TABLE);
    }

    @Override
    public long findNumberOfPeopleAssignedToQueue(String storeId) {
        return mongoTemplate.count(
                query(where("BS").is(storeId)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                BusinessUserStoreEntity.class,
                TABLE
        );
    }

    @Override
    public long findNumberOfPeoplePendingApprovalToQueue(String storeId) {
        return mongoTemplate.count(
                query(where("BS").is(storeId)
                        .andOperator(
                                isNotActive(),
                                isNotDeleted()
                        )
                ),
                BusinessUserStoreEntity.class,
                TABLE
        );
    }

    @Override
    public List<BusinessUserStoreEntity> getAllQueueManagers(String storeId) {
        return mongoTemplate.find(
                query(where("BS").is(storeId).andOperator(isNotDeleted())),
                BusinessUserStoreEntity.class,
                TABLE
        );
    }

    @Override
    public void activateAccount(String qid, String bizNameId) {
        mongoTemplate.updateFirst(
                query(where("QID").is(qid).and("BN").is(bizNameId)),
                entityUpdate(update("A", true)),
                BusinessUserStoreEntity.class,
                TABLE
        );
    }
}
