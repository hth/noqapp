package com.token.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.BusinessUserStoreEntity;

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

    }
}
