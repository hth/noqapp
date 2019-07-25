package com.noqapp.repository;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StoreCategoryEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 3/22/18 11:10 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class StoreCategoryManagerImpl implements StoreCategoryManager {
    private static final Logger LOG = LoggerFactory.getLogger(StoreCategoryManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        StoreCategoryEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public StoreCategoryManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(StoreCategoryEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(StoreCategoryEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public List<StoreCategoryEntity> findAll(String storeId) {
        return mongoTemplate.find(
            query(where("BS").is(storeId)).with(new Sort(ASC, "CN")),
            StoreCategoryEntity.class,
            TABLE);
    }

    @Override
    public long countOfCategory(String storeId) {
        return mongoTemplate.count(Query.query(where("BS").is(storeId)), StoreCategoryEntity.class, TABLE);
    }

    @Override
    public boolean existCategoryName(String storeId, String categoryName) {
        return mongoTemplate.exists(
            Query.query(where("BS").is(storeId).and("CN").regex("^" + categoryName + "$", "i")),
            StoreCategoryEntity.class,
            TABLE
        );
    }

    @Override
    public StoreCategoryEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), StoreCategoryEntity.class, TABLE);
    }
}
