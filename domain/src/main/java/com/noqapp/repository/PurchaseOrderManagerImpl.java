package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 3/29/18 2:31 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class PurchaseOrderManagerImpl implements PurchaseOrderManager {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            PurchaseOrderEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PurchaseOrderManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PurchaseOrderEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(PurchaseOrderEntity object) {

    }
}
