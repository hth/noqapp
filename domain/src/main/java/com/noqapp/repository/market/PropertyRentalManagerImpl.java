package com.noqapp.repository.market;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.domain.types.catgeory.MarketplaceRejectReasonEnum;

import com.mongodb.DuplicateKeyException;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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
    public PropertyRentalEntity findOneById(String qid, String id) {
        return mongoTemplate.findOne(
            query(where("id").is(id).and("QID").is(qid)).with(Sort.by(Sort.Direction.DESC, "C")),
            PropertyRentalEntity.class,
            TABLE
        );
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

    @Override
    public PropertyRentalEntity findOneByIdAndExpressInterestWithViewCount(String qid, String id) {
        return mongoTemplate.findAndModify(
            query(where("id").is(id)),
            new Update().addToSet("EQ", qid).inc("EC", 1).inc("VC", 1),
            FindAndModifyOptions.options().returnNew(true),
            PropertyRentalEntity.class,
            TABLE);
    }

    @Override
    public PropertyRentalEntity findOneByIdAndViewCount(String id) {
        return mongoTemplate.findAndModify(
            query(where("id").is(id)),
            new Update().inc("VC", 1),
            FindAndModifyOptions.options().returnNew(true),
            PropertyRentalEntity.class,
            TABLE);
    }

    @Override
    public List<PropertyRentalEntity> findAllPendingApproval() {
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.P)).with(Sort.by(Sort.Direction.DESC, "C")).limit(5),
            PropertyRentalEntity.class,
            TABLE
        );
    }

    @Override
    public List<PropertyRentalEntity> findAllPendingApprovalWithoutImage() {
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.P).and("PI").exists(false).and("U").lte(DateUtil.minusMinutes(30))).with(Sort.by(Sort.Direction.DESC, "U")),
            PropertyRentalEntity.class,
            TABLE
        );
    }

    @Override
    public long findAllPendingApprovalCount() {
        return mongoTemplate.count(
            query(where("VS").is(ValidateStatusEnum.P)),
            PropertyRentalEntity.class,
            TABLE
        );
    }

    @Override
    public PropertyRentalEntity changeStatus(String marketplaceId, ValidateStatusEnum validateStatus, MarketplaceRejectReasonEnum marketplaceRejectReason, Date publishUntil, String validatedByQid) {
        Update update;
        if (ValidateStatusEnum.A == validateStatus) {
            update = entityUpdate(update("VS", validateStatus).set("VB", validatedByQid).set("PU", publishUntil).unset("RR"));
        } else {
            update = entityUpdate(update("VS", validateStatus).set("RR", marketplaceRejectReason).set("VB", validatedByQid));
        }
        return mongoTemplate.findAndModify(
            query(where("id").is(marketplaceId)),
            update,
            FindAndModifyOptions.options().returnNew(true),
            PropertyRentalEntity.class,
            TABLE);
    }
}
