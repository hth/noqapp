package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 5:10 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BusinessUserManagerImpl implements BusinessUserManager {
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
    public BusinessUserEntity findByRid(String qid) {
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
    public BusinessUserEntity findBusinessUser(String qid) {
        return mongoTemplate.findOne(
                query(where("QID").is(qid)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                BusinessUserEntity.class,
                TABLE);
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
        /** Do not implement this method. No hard delete for business user. */
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<BusinessUserEntity> awaitingApprovals() {
        return mongoTemplate.find(
                query(where("RS").is(BusinessUserRegistrationStatusEnum.C)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ).limit(10).with(new Sort(ASC, "U")),
                BusinessUserEntity.class,
                TABLE);
    }

    @Override
    public long awaitingApprovalCount() {
        return mongoTemplate.count(
                query(where("RS").is(BusinessUserRegistrationStatusEnum.C)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                BusinessUserEntity.class,
                TABLE
        );
    }
}
