package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.types.BusinessCustomerAttributeEnum;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;

import com.mongodb.DuplicateKeyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;

/**
 * hitender
 * 6/17/18 2:06 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class BusinessCustomerManagerImpl implements BusinessCustomerManager {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCustomerManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        BusinessCustomerEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BusinessCustomerManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BusinessCustomerEntity object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DuplicateKeyException e) {
            LOG.error("Already exists {} {} reason={}", object.getQueueUserId(), object.getBusinessCustomerId(), e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void deleteHard(BusinessCustomerEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public BusinessCustomerEntity findOneByCustomerId(String businessCustomerId, String bizNameId) {
        return mongoTemplate.findOne(
            query(where("BC").is(businessCustomerId).and("BN").is(bizNameId)),
            BusinessCustomerEntity.class,
            TABLE
        );
    }

    @Override
    public BusinessCustomerEntity findOneByQid(String qid, String bizNameId) {
        return mongoTemplate.findOne(
            query(where("QID").is(qid).and("BN").is(bizNameId)),
            BusinessCustomerEntity.class,
            TABLE
        );
    }

    @Override
    public BusinessCustomerEntity findOneByQidAndAttribute(String qid, String bizNameId, BusinessCustomerAttributeEnum businessCustomerAttribute) {
        return mongoTemplate.findOne(
            query(where("QID").is(qid).and("BN").is(bizNameId).and("CA").all(businessCustomerAttribute)),
            BusinessCustomerEntity.class,
            TABLE
        );
    }

    @Override
    public void addBusinessCustomerAttribute(String businessCustomerId, BusinessCustomerAttributeEnum businessCustomerAttribute) {
        mongoTemplate.updateFirst(
            query(where("id").is(businessCustomerId)),
            new Update().addToSet("CA", businessCustomerAttribute),
            BusinessCustomerEntity.class,
            TABLE
        );
    }

    @Override
    public void clearBusinessCustomer(String qid, String bizNameId) {
        mongoTemplate.remove(
            query(where("QID").is(qid).and("BN").is(bizNameId)),
            BusinessCustomerEntity.class,
            TABLE
        );
    }

    public void updateBusinessCustomer(
        String businessCustomerId,
        LinkedHashSet<BusinessCustomerAttributeEnum> businessCustomerAttributes,
        CustomerPriorityLevelEnum customerPriorityLevel,
        String limitBusinessCategory
    ) {
        mongoTemplate.updateFirst(
            query(where("id").is(businessCustomerId)),
            entityUpdate(new Update().pullAll("CA", BusinessCustomerAttributeEnum.values())), //Clean before update
            BusinessCustomerEntity.class,
            TABLE
        );

        Update update;
        if (null == limitBusinessCategory) {
            update = update("PL", customerPriorityLevel)
                .addToSet("CA").each(businessCustomerAttributes);
        } else {
            update = update("PL", customerPriorityLevel)
                .addToSet("CA").each(businessCustomerAttributes)
                .set("LC", limitBusinessCategory);
        }
        mongoTemplate.updateFirst(
            query(where("id").is(businessCustomerId)),
            entityUpdate(update),
            BusinessCustomerEntity.class,
            TABLE
        );
    }
}
