package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.CouponEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 2019-06-09 13:39
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class CouponManagerImpl implements CouponManager {
    private static final Logger LOG = LoggerFactory.getLogger(CouponManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        CouponEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    public CouponManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(CouponEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(CouponEntity object) {

    }
}
