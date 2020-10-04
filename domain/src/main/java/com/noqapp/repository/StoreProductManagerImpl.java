package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StoreProductEntity;

import com.mongodb.client.result.DeleteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 3/21/18 5:07 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class StoreProductManagerImpl implements StoreProductManager {
    private static final Logger LOG = LoggerFactory.getLogger(StoreProductManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        StoreProductEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public StoreProductManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(StoreProductEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(StoreProductEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public void removeById(String id) {
        mongoTemplate.remove(query(where("id").is(id)), StoreProductEntity.class, TABLE);
    }

    @Override
    public List<StoreProductEntity> findAll(String storeId) {
        return mongoTemplate.find(
            query(where("BS").is(storeId)).with(Sort.by(ASC, "PN")),
            StoreProductEntity.class,
            TABLE);
    }

    @Override
    public List<StoreProductEntity> findAllDisplayCase(String storeId) {
        return mongoTemplate.find(
            query(where("BS").is(storeId).and("DC").is(true)).with(Sort.by(ASC, "PN")),
            StoreProductEntity.class,
            TABLE);
    }

    @Override
    public long countOfProduct(String storeId) {
        return mongoTemplate.count(query(where("BS").is(storeId)), StoreProductEntity.class, TABLE);
    }

    @Override
    public boolean existProductName(String storeId, String productName) {
        return mongoTemplate.exists(
            query(where("BS").is(storeId).and("PN").regex("^" + productName + "$", "i")),
            StoreProductEntity.class,
            TABLE
        );
    }

    @Override
    public long countCategoryUse(String storeId, String storeCategoryId) {
        return mongoTemplate.count(
            query(where("BS").is(storeId).and("SC").is(storeCategoryId)),
            StoreProductEntity.class,
            TABLE
        );
    }

    @Override
    public StoreProductEntity findOne(String id) {
        return mongoTemplate.findOne(
            query(where("id").is(id)),
            StoreProductEntity.class,
            TABLE
        );
    }

    @Override
    public void removeStoreCategoryReference(String storeCategoryId) {
        mongoTemplate.updateMulti(
            query(where("SC").is(storeCategoryId)),
            entityUpdate(new Update().unset("SC")),
            StoreProductEntity.class,
            TABLE
        );
    }

    @Override
    public long removedStoreProduct(String storeId) {
        DeleteResult deleteResult = mongoTemplate.remove(
            query(where("BS").is(storeId)),
            StoreProductEntity.class,
            TABLE
        );

        return deleteResult.getDeletedCount();
    }

    @Override
    public void changeInventoryCount(String productId, int count) {
        mongoTemplate.updateFirst(
            query(where("id").is(productId)),
            new Update().inc("IC", count),
            StoreProductEntity.class,
            TABLE
        );
    }
}
