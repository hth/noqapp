package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * hitender
 * 3/29/18 2:42 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class PurchaseProductOrderManagerImpl implements PurchaseProductOrderManager {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseProductOrderManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            PurchaseOrderProductEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PurchaseProductOrderManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PurchaseOrderProductEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(PurchaseOrderProductEntity object) {

    }

    @Override
    public List<PurchaseOrderProductEntity> getAllByPurchaseOrderId(String purchaseOrderId) {
        return mongoTemplate.find(
                Query.query(where("PO").is(purchaseOrderId)),
                PurchaseOrderProductEntity.class,
                TABLE
        );
    }
}
