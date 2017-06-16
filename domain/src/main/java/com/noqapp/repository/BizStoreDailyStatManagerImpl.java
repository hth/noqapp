package com.noqapp.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BizStoreDailyStatEntity;
import com.noqapp.domain.BizStoreEntity;

/**
 * User: hitender
 * Date: 6/16/17 4:48 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BizStoreDailyStatManagerImpl implements BizStoreDailyStatManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreDailyStatManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizStoreEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizStoreDailyStatManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BizStoreDailyStatEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(BizStoreDailyStatEntity object) {
        mongoTemplate.remove(object);
    }
}
