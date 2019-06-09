package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.DiscountEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 14:41
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class DiscountManagerImpl implements DiscountManager {
    private static final Logger LOG = LoggerFactory.getLogger(DiscountManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        DiscountEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    public DiscountManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(DiscountEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(DiscountEntity object) {

    }

    @Override
    public List<DiscountEntity> findAll(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId)),
            DiscountEntity.class,
            TABLE
        );
    }

    @Override
    public void inActive(String discountId) {
        mongoTemplate.updateFirst(
            query(where("id").is(discountId)),
            entityUpdate(update("A", false)),
            DiscountEntity.class,
            TABLE
        );
    }
}
