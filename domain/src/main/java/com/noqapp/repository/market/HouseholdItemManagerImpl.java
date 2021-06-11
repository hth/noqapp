package com.noqapp.repository.market;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.ValidateStatusEnum;

import com.mongodb.DuplicateKeyException;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * hitender
 * 2/25/21 1:46 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class HouseholdItemManagerImpl implements HouseholdItemManager {
    private static final Logger LOG = LoggerFactory.getLogger(HouseholdItemManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        HouseholdItemEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    public HouseholdItemManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(HouseholdItemEntity object) {
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
    public void deleteHard(HouseholdItemEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public HouseholdItemEntity findOneById(String id) {
        return mongoTemplate.findById(new ObjectId(id), HouseholdItemEntity.class, TABLE);
    }

    @Override
    public List<HouseholdItemEntity> findByQid(String queueUserId) {
        return mongoTemplate.find(
            query(where("QID").is(queueUserId)).with(Sort.by(Sort.Direction.DESC, "C")),
            HouseholdItemEntity.class,
            TABLE
        );
    }

    @Override
    public Stream<HouseholdItemEntity> findAllWithStream() {
        return mongoTemplate.find(
            query(where("PU").gte(new Date()).and("A").is(true).and("D").is(false)),
            HouseholdItemEntity.class,
            TABLE
        ).stream();
    }

    @Override
    public HouseholdItemEntity findOneByIdAndExpressInterest(String id) {
        return mongoTemplate.findAndModify(
            query(where("id").is(id)),
            new Update().inc("LC", 1),
            FindAndModifyOptions.options().returnNew(true),
            HouseholdItemEntity.class,
            TABLE);
    }

    @Override
    public List<HouseholdItemEntity> findAllPendingApproval() {
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.P)).with(Sort.by(Sort.Direction.DESC, "C")).limit(5),
            HouseholdItemEntity.class,
            TABLE
        );
    }

    @Override
    public long findAllPendingApprovalCount() {
        return mongoTemplate.count(
            query(where("VS").is(ValidateStatusEnum.P)),
            HouseholdItemEntity.class,
            TABLE
        );
    }
}
