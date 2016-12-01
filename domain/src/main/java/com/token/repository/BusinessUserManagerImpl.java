package com.token.repository;

import static com.token.repository.util.AppendAdditionalFields.isActive;
import static com.token.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.BusinessUserEntity;

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
    public BusinessUserEntity findByRid(String rid) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid)),
                BusinessUserEntity.class,
                TABLE);
    }

    @Override
    public BusinessUserEntity findBusinessUser(String rid) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                BusinessUserEntity.class,
                TABLE);
    }

    @Override
    public boolean doesBusinessUserExists(String rid, String bizId) {
        return mongoTemplate.exists(
                query(where("RID").is(rid).and("BIZ_NAME.$id").is(new ObjectId(bizId))
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
}
