package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StoreProductEntity;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
    public List<StoreProductEntity> findAll(String storeId) {
        return mongoTemplate.find(Query.query(where("BZ").is(storeId)).with(new Sort(ASC, "PN")), StoreProductEntity.class, TABLE);
    }

    @Override
    public long countOfProduct(String storeId) {
        return mongoTemplate.count(Query.query(where("BZ").is(storeId)), StoreProductEntity.class, TABLE);
    }

    @Override
    public boolean existProductName(String storeId, String productName) {
        return mongoTemplate.exists(
                Query.query(where("BZ").is(storeId).and("PN").regex("^" + productName + "$", "i")),
                StoreProductEntity.class,
                TABLE
        );
    }

    @Override
    public long countCategoryUse(String storeId, String storeCategoryId) {
        return mongoTemplate.count(
                query(where("BZ").is(storeId).and("SC").is(storeCategoryId)),
                StoreProductEntity.class,
                TABLE
        );
    }

    @Override
    public StoreProductEntity findOne(String id) {
        return mongoTemplate.findOne(
                query(where("id").is(new ObjectId(id))),
                StoreProductEntity.class,
                TABLE
        );
    }

    @Override
    public void removeStoreCategoryReference(String storeId) {
        mongoTemplate.updateMulti(
                query(where("SC").is(storeId)),
                entityUpdate(new Update().unset("SC")),
                StoreProductEntity.class,
                TABLE
        );
    }
}
