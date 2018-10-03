package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

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
public class PurchaseOrderProductManagerImpl implements PurchaseOrderProductManager {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderProductManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        PurchaseOrderProductEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PurchaseOrderProductManagerImpl(MongoTemplate mongoTemplate) {
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
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public List<PurchaseOrderProductEntity> getAllByPurchaseOrderId(String purchaseOrderId) {
        return mongoTemplate.find(
            query(where("PO").is(purchaseOrderId)),
            PurchaseOrderProductEntity.class,
            TABLE
        );
    }

    public long deleteByCodeQR(String codeQR) {
        return mongoTemplate.remove(
            query(where("QR").is(codeQR)),
            PurchaseOrderProductEntity.class,
            TABLE
        ).getDeletedCount();
    }
}
