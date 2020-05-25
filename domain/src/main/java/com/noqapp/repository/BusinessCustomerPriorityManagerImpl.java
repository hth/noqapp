package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.*;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BusinessCustomerPriorityEntity;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 5/15/20 4:56 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class BusinessCustomerPriorityManagerImpl implements BusinessCustomerPriorityManager {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCustomerPriorityManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        BusinessCustomerPriorityEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BusinessCustomerPriorityManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BusinessCustomerPriorityEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(BusinessCustomerPriorityEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public boolean existPriorityCode(String bizNameId, CustomerPriorityLevelEnum customerPriorityLevel) {
        return mongoTemplate.exists(
            query(where("BN").is(bizNameId).and("PL").is(customerPriorityLevel)),
            BusinessCustomerPriorityEntity.class,
            TABLE
        );
    }

    @Override
    public BusinessCustomerPriorityEntity findOne(String bizNameId, String priorityName) {
        return mongoTemplate.findOne(
            query(where("BN").is(bizNameId).and("PN").is(priorityName)),
            BusinessCustomerPriorityEntity.class,
            TABLE
        );
    }

    @Override
    public List<BusinessCustomerPriorityEntity> findAll(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId)),
            BusinessCustomerPriorityEntity.class,
            TABLE
        );
    }
}
