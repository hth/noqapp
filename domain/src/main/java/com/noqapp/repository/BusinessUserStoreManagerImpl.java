package com.noqapp.repository;

import com.mongodb.client.result.DeleteResult;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.types.UserLevelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.noqapp.repository.util.AppendAdditionalFields.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * User: hitender
 * Date: 12/13/16 10:30 AM
 */
@SuppressWarnings({
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

    @Override
    public boolean hasAccessWithUserLevel(String qid, String codeQR, UserLevelEnum userLevel) {
        return mongoTemplate.exists(
                query(where("QID").is(qid).and("QR").is(codeQR).and("UL").is(userLevel)),
                BusinessUserStoreEntity.class,
                TABLE);
    }

    @Override
    public boolean hasAccessUsingStoreId(String qid, String bizStoreId) {
        return mongoTemplate.exists(query(where("QID").is(qid).and("BS").is(bizStoreId)), BusinessUserStoreEntity.class, TABLE);
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
    public long findNumberOfPeopleAssignedToQueue(String bizStoreId) {
        return mongoTemplate.count(
                query(where("BS").is(bizStoreId)
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
    public long findNumberOfPeoplePendingApprovalToQueue(String bizStoreId) {
        return mongoTemplate.count(
                query(where("BS").is(bizStoreId)
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
    public List<BusinessUserStoreEntity> getAllManagingStore(String bizStoreId) {
        return mongoTemplate.find(
                query(where("BS").is(bizStoreId).andOperator(isNotDeleted())),
                BusinessUserStoreEntity.class,
                TABLE
        );
    }

    @Override
    public BusinessUserStoreEntity findUserManagingStoreWithUserLevel(String qid, UserLevelEnum userLevel) {
        return mongoTemplate.findOne(
                query(where("QID").is(qid)
                        .and("UL").is(userLevel)
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
    public List<BusinessUserStoreEntity> findAllManagingStoreWithUserLevel(String bizStoreId, UserLevelEnum userLevel) {
        return mongoTemplate.find(
                query(where("BS").is(bizStoreId).and("UL").is(userLevel).andOperator(isNotDeleted())),
                BusinessUserStoreEntity.class,
                TABLE
        );
    }

    @Override
    public long deleteAllManagingStore(String bizStoreId) {
        DeleteResult deleteResult = mongoTemplate.remove(
                query(where("BS").is(bizStoreId)),
                BusinessUserStoreEntity.class,
                TABLE
        );

        return deleteResult.getDeletedCount();
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

    @Override
    public void removeFromBusiness(String qid, String bizNameId) {
        mongoTemplate.remove(
                query(where("QID").is(qid).and("BN").is(bizNameId)),
                BusinessUserStoreEntity.class,
                TABLE
        );
    }

    @Override
    public void removeFromStore(String qid, String bizStoreId) {
        mongoTemplate.remove(
                query(where("QID").is(qid).and("BS").is(bizStoreId)),
                BusinessUserStoreEntity.class,
                TABLE);
    }

    @Override
    public BusinessUserStoreEntity findOneByQidAndCodeQR(String qid, String codeQR) {
        return mongoTemplate.findOne(
            query(where("QID").is(qid).and("QR").is(codeQR)),
            BusinessUserStoreEntity.class,
            TABLE
        );
    }

    @Override
    public List<BusinessUserStoreEntity> findAll() {
        return mongoTemplate.findAll(BusinessUserStoreEntity.class, TABLE);
    }
}
