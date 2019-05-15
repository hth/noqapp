package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
    public PurchaseOrderProductEntity findOne(String id) {
        return mongoTemplate.findOne(
            query(where("_id").is(new ObjectId(id))),
            PurchaseOrderProductEntity.class,
            TABLE
        );
    }

    @Override
    public List<PurchaseOrderProductEntity> getAllByPurchaseOrderId(String purchaseOrderId) {
        return mongoTemplate.find(
            query(where("PO").is(purchaseOrderId)),
            PurchaseOrderProductEntity.class,
            TABLE
        );
    }

    @Override
    public List<PurchaseOrderProductEntity> getAllByPurchaseOrderIdWhenPriceZero(String purchaseOrderId) {
        return mongoTemplate.find(
            query(where("PO").is(purchaseOrderId).and("PP").is(0)),
            PurchaseOrderProductEntity.class,
            TABLE
        );
    }

    public long deleteByCodeQR(String codeQR, Date until) {
        return mongoTemplate.remove(
            query(where("QR").is(codeQR).and("C").lte(until)),
            PurchaseOrderProductEntity.class,
            TABLE
        ).getDeletedCount();
    }

    @Override
    public void changePatient(String purchaseOrderId, String queueUserId) {
        mongoTemplate.findAndModify(
            query(where("PO").is(purchaseOrderId)),
            update("QID", queueUserId),
            PurchaseOrderProductEntity.class,
            TABLE
        );
    }

    @Override
    public void removePurchaseOrderProduct(String purchaseOrderId) {
        mongoTemplate.remove(
            query(where("PO").is(purchaseOrderId)),
            PurchaseOrderProductEntity.class,
            TABLE
        );
    }
}
