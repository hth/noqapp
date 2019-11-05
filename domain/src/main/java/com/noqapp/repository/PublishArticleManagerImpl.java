package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.types.ValidateStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 2019-01-02 18:22
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class PublishArticleManagerImpl implements PublishArticleManager {
    private static final Logger LOG = LoggerFactory.getLogger(PublishArticleManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        PublishArticleEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PublishArticleManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PublishArticleEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public List<PublishArticleEntity> findAll(String qid) {
        return mongoTemplate.find(
            query(where("QID").is(qid).and("D").is(false)),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public PublishArticleEntity findOne(String id) {
        return mongoTemplate.findOne(
            query(where("id").is(id)),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public boolean exists(String id, String qid) {
        return mongoTemplate.exists(
            query(where("id").is(id).and("QID").is(qid)),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public void takeOffOrOnline(String id, boolean active) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            update("A", active),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public PublishArticleEntity changeStatus(String id, ValidateStatusEnum validateStatus) {
        return mongoTemplate.findAndModify(
            query(where("id").is(id)),
            update("VS", validateStatus),
            FindAndModifyOptions.options().returnNew(true),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteHard(PublishArticleEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public List<PublishArticleEntity> findPendingApprovals() {
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.P)).with(Sort.by(Sort.Direction.ASC, "C")),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public PublishArticleEntity findOnePendingReview(String id) {
        return mongoTemplate.findOne(
            query(where("id").is(id).and("VS").is(ValidateStatusEnum.P)),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public List<PublishArticleEntity> getLatestArticles() {
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.A)
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
            ).limit(10).with(Sort.by(Sort.Direction.ASC, "PD")),
            PublishArticleEntity.class,
            TABLE
        );
    }

    @Override
    public long findPendingApprovalCount() {
        return mongoTemplate.count(
            query(where("VS").is(ValidateStatusEnum.P)),
            PublishArticleEntity.class,
            TABLE
        );
    }
}
