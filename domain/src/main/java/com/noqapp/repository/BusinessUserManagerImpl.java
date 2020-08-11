package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;

import com.mongodb.client.result.UpdateResult;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 5:10 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class BusinessUserManagerImpl implements BusinessUserManager {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        BusinessUserEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BusinessUserManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public BusinessUserEntity findByQid(String qid) {
        return mongoTemplate.findOne(
            query(where("QID").is(qid)),
            BusinessUserEntity.class,
            TABLE);
    }

    @Override
    public BusinessUserEntity findById(String id) {
        return mongoTemplate.findOne(
            query(where("_id").is(id)),
            BusinessUserEntity.class,
            TABLE);
    }

    @Override
    public BusinessUserEntity loadBusinessUser() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findBusinessUser(queueUser.getQueueUserId(), null);
    }

    @Override
    public BusinessUserEntity findBusinessUser(String qid, String bizId) {
        Query query = query(where("QID").is(qid)
            .andOperator(
                isActive(),
                isNotDeleted()
            )
        );

        if (null != bizId) {
            query.addCriteria(where("B_N.$id").is(new ObjectId(bizId)));
        }

        return mongoTemplate.findOne(query, BusinessUserEntity.class, TABLE);
    }

    @Override
    public boolean doesBusinessUserExists(String qid, String bizId) {
        return mongoTemplate.exists(
            query(where("QID").is(qid).and("B_N.$id").is(new ObjectId(bizId))
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
            ),
            BusinessUserEntity.class,
            TABLE);
    }

    @Override
    public void save(BusinessUserEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(BusinessUserEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public List<BusinessUserEntity> awaitingBusinessApprovals() {
        return mongoTemplate.find(
            query(where("RS").is(BusinessUserRegistrationStatusEnum.C)
                .and("UL").is(UserLevelEnum.M_ADMIN)
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
            ).limit(10).with(Sort.by(ASC, "U")),
            BusinessUserEntity.class,
            TABLE);
    }

    @Override
    public long awaitingBusinessApprovalCount() {
        return mongoTemplate.count(
            query(where("RS").is(BusinessUserRegistrationStatusEnum.C)
                .and("UL").is(UserLevelEnum.M_ADMIN)
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
            ),
            BusinessUserEntity.class,
            TABLE
        );
    }

    @Override
    public List<BusinessUserEntity> getAllNonAdminForBusiness(String bizNameId) {
        return mongoTemplate.find(
            query(where("B_N.$id").is(new ObjectId(bizNameId))
                .and("UL").ne(UserLevelEnum.M_ADMIN)
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
            ),
            BusinessUserEntity.class,
            TABLE);
    }

    @Override
    public List<BusinessUserEntity> getAllForBusiness(String bizNameId) {
        return mongoTemplate.find(
            query(where("B_N.$id").is(new ObjectId(bizNameId))
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
            ),
            BusinessUserEntity.class,
            TABLE);
    }

    @Override
    public List<BusinessUserEntity> getAllForBusiness(String bizNameId, UserLevelEnum userLevel) {
        return mongoTemplate.find(
            query(where("B_N.$id").is(new ObjectId(bizNameId)).and("UL").is(userLevel).and("RS").is(BusinessUserRegistrationStatusEnum.V)
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
            ),
            BusinessUserEntity.class,
            TABLE);
    }

    @Override
    public long updateUserLevel(String qid, UserLevelEnum userLevel) {
        UpdateResult updateResult = mongoTemplate.updateMulti(
            query(where("QID").is(qid)),
            entityUpdate(update("UL", userLevel)),
            BusinessUserEntity.class,
            TABLE
        );

        LOG.info("Updated record for qid={} userLevel={} count={}", qid, userLevel, updateResult.getModifiedCount());
        return updateResult.getModifiedCount();
    }

    @Override
    public boolean hasAccess(String qid, String bizNameId) {
        Query query = query(where("QID").is(qid)
            .andOperator(
                isActive(),
                isNotDeleted()
            )
        );

        if (null != bizNameId) {
            query.addCriteria(where("B_N.$id").is(new ObjectId(bizNameId)));
        }

        return mongoTemplate.exists(query, BusinessUserEntity.class, TABLE);
    }
}
