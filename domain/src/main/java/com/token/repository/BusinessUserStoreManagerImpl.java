package com.token.repository;

import static com.token.repository.util.AppendAdditionalFields.isActive;
import static com.token.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.BusinessUserStoreEntity;

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
    public boolean hasAccess(String rid, String codeQR) {
        return mongoTemplate.exists(query(where("RID").is(rid).and("QR").is(codeQR)), BusinessUserStoreEntity.class, TABLE);
    }

    //TODO support pagination
    @Override
    public List<BusinessUserStoreEntity> getQueues(String rid, int limit) {
        return mongoTemplate.find(
            query(where("RID").is(rid)
                    .andOperator(
                        isActive(),
                        isNotDeleted()
                    )
            ).limit(10),
            BusinessUserStoreEntity.class,
            TABLE
        );
    }
}
