package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.types.UserLevelEnum;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

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
    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserStoreManagerImpl.class);
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
        Assert.hasText(codeQR, "codeQR is empty");
        return mongoTemplate.exists(query(where("QID").is(qid).and("QR").is(codeQR)), BusinessUserStoreEntity.class, TABLE);
    }

    @Override
    public boolean hasAccessWithUserLevel(String qid, String codeQR, UserLevelEnum userLevel) {
        Assert.hasText(codeQR, "codeQR is empty");
        return mongoTemplate.exists(
            query(where("QID").is(qid).and("QR").is(codeQR).and("UL").is(userLevel)),
            BusinessUserStoreEntity.class,
            TABLE);
    }

    @Override
    public boolean hasAccessUsingStoreId(String qid, String bizStoreId) {
        Assert.hasText(bizStoreId, "bizStoreId is empty");
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
    public BusinessUserStoreEntity findUserManagingStoreWithCodeQRAndUserLevel(String codeQR, UserLevelEnum userLevel) {
        return mongoTemplate.findOne(
            query(where("QR").is(codeQR)
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
    public long updateUserLevel(String qid, UserLevelEnum userLevel) {
        UpdateResult updateResult = mongoTemplate.updateMulti(
            query(where("QID").is(qid)),
            entityUpdate(update("UL", userLevel)),
            BusinessUserStoreEntity.class,
            TABLE
        );

        LOG.info("Updated record for qid={} userLevel={} count={}", qid, userLevel, updateResult.getModifiedCount());
        return updateResult.getModifiedCount();
    }

    @Override
    public long countNumberOfStoreUsers(String bizNameId) {
        return mongoTemplate.count(
            query(where("BN").is(bizNameId)),
            BusinessUserStoreEntity.class,
            TABLE
        );
    }
}
