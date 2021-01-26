package com.noqapp.repository.market;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.market.PropertyEntity;

import com.mongodb.DuplicateKeyException;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 1/11/21 12:51 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class PropertyManagerImpl implements PropertyManager {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        PropertyEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PropertyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PropertyEntity object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DuplicateKeyException e) {
            LOG.error("Already exists {} {} reason={}", object.getQueueUserId(), object.getQueueUserId(), e.getLocalizedMessage(), e);
        }
    }


    @Override
    public void deleteHard(PropertyEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public PropertyEntity findOneById(String id) {
        return mongoTemplate.findById(new ObjectId(id), PropertyEntity.class, TABLE);
    }

    @Override
    public List<PropertyEntity> findByQid(String queueUserId) {
        return mongoTemplate.find(
            query(where("QID").is(queueUserId)).with(Sort.by(Sort.Direction.DESC, "C")),
            PropertyEntity.class,
            TABLE
        );
    }
}
