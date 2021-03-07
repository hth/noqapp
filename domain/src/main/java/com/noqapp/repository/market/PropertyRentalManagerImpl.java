package com.noqapp.repository.market;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.market.PropertyRentalEntity;

import com.mongodb.DuplicateKeyException;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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
public class PropertyRentalManagerImpl implements PropertyRentalManager {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyRentalManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        PropertyRentalEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PropertyRentalManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PropertyRentalEntity object) {
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
    public void deleteHard(PropertyRentalEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public PropertyRentalEntity findOneById(String id) {
        return mongoTemplate.findById(new ObjectId(id), PropertyRentalEntity.class, TABLE);
    }

    @Override
    public List<PropertyRentalEntity> findByQid(String queueUserId) {
        return mongoTemplate.find(
            query(where("QID").is(queueUserId)).with(Sort.by(Sort.Direction.DESC, "C")),
            PropertyRentalEntity.class,
            TABLE
        );
    }

    @Override
    public Stream<PropertyRentalEntity> findAllWithStream() {
        return mongoTemplate.find(
            query(where("PU").gte(new Date()).and("A").is(true).and("D").is(false)),
            PropertyRentalEntity.class,
            TABLE
        ).stream();
    }
}
