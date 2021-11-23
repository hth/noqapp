package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PointEarnedEntity;
import com.noqapp.domain.types.PointActivityEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

/**
 * hitender
 * 6/24/21 7:05 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class PointEarnedManagerImpl implements PointEarnedManager {
    private static final Logger LOG = LoggerFactory.getLogger(PointEarnedManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        PointEarnedEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PointEarnedManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PointEarnedEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(PointEarnedEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public Stream<String> findUniqueAllNotMarkedComputed() {
        return mongoTemplate.findDistinct(
            query(where("MC").is(false)),
            "QID",
            PointEarnedEntity.class,
            String.class
        ).stream();
    }

    @Override
    public Stream<PointEarnedEntity> findAllNotMarkedComputed() {
        return mongoTemplate.stream(
            query(where("MC").is(false)).with(Sort.by(ASC, "C")),
            PointEarnedEntity.class,
            TABLE
        ).stream();
    }

    @Override
    public void markComputedById(String id) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(update("MC", true)),
            PointEarnedEntity.class,
            TABLE
        );
    }

    @Override
    public long countReviewPoints(String qid) {
        return mongoTemplate.count(
            query(where("qid").is(qid).and("PA").is(PointActivityEnum.REV)),
            PointEarnedEntity.class,
            TABLE
        );
    }

    @Override
    public long countInvitePoints(String qid) {
        return mongoTemplate.count(
            query(where("qid").is(qid).and("PA").is(PointActivityEnum.INV)),
            PointEarnedEntity.class,
            TABLE
        );
    }

    @Override
    public long countInviteePoints(String qid) {
        return mongoTemplate.count(
            query(where("qid").is(qid).and("PA").is(PointActivityEnum.ISU)),
            PointEarnedEntity.class,
            TABLE
        );
    }
}
