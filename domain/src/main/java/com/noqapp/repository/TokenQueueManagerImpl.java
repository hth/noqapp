package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;

import com.mongodb.WriteConcern;
import com.mongodb.client.result.UpdateResult;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/**
 * User: hitender
 * Date: 12/16/16 8:51 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class TokenQueueManagerImpl implements TokenQueueManager {
    private static final Logger LOG = LoggerFactory.getLogger(TokenQueueManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        TokenQueueEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public TokenQueueManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(TokenQueueEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(TokenQueueEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    /**
     * TODO add REDIS cache
     *
     * @param codeQR
     * @return
     */
    @Override
    public TokenQueueEntity findByCodeQR(String codeQR) {
        LOG.info("findByCodeQR codeQR={}", codeQR);
        return mongoTemplate.findOne(query(where("id").is(codeQR)), TokenQueueEntity.class, TABLE);
    }

    @Override
    public TokenQueueEntity getNextToken(String codeQR, int availableTokenCount) {
        if (availableTokenCount > 0) {
            return mongoTemplate.findAndModify(
                query(where("id").is(codeQR).and("LN").lt(availableTokenCount)),
                new Update().inc("LN", 1),
                FindAndModifyOptions.options().returnNew(true),
                TokenQueueEntity.class,
                TABLE);
        } else {
            return mongoTemplate.findAndModify(
                query(where("id").is(codeQR)),
                new Update().inc("LN", 1),
                FindAndModifyOptions.options().returnNew(true),
                TokenQueueEntity.class,
                TABLE);
        }
    }

    @Override
    public TokenQueueEntity updateServing(String codeQR, int serving, QueueStatusEnum queueStatus) {
        return mongoTemplate.findAndModify(
            query(where("id").is(codeQR)),
            entityUpdate(update("CS", serving).set("QS", queueStatus)),
            FindAndModifyOptions.options().returnNew(true),
            TokenQueueEntity.class,
            TABLE);
    }

    @Override
    public List<TokenQueueEntity> getTokenQueues(String[] ids) {
        try {
            return mongoTemplate.find(
                /* Make sure ids does not contain null as List.of(ids) fails when null is encountered. */
                query(where("id").in(Arrays.asList(ids))),
                TokenQueueEntity.class,
                TABLE
            );
        } catch (NullPointerException e) {
            String[] cleanedIds = Arrays.stream(ids)
                .filter(s -> (s != null))
                .toArray(String[]::new);

            if (cleanedIds.length < ids.length) {
                LOG.warn("Attempting to recover when getting tokens reason={}", e.getLocalizedMessage());
                return getTokenQueues(cleanedIds);
            }

            LOG.error("Failed getting tokens reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed getting tokens");
        }
    }

    @Override
    public void changeQueueStatus(String codeQR, QueueStatusEnum queueStatus) {
        mongoTemplate.updateFirst(
            query(where("id").is(codeQR)),
            entityUpdate(update("QS", queueStatus)),
            TokenQueueEntity.class,
            TABLE);
    }

    @Override
    public void resetForNewDay(String codeQR) {
        mongoTemplate.updateFirst(
            query(where("id").is(codeQR).and("QS").ne(QueueStatusEnum.C)),
            entityUpdate(update("LN", 0).set("CS", 0).set("QS", QueueStatusEnum.S)),
            TokenQueueEntity.class,
            TABLE);
    }

    @Override
    public boolean updateDisplayNameAndBusinessType(
        String codeQR,
        String topic,
        String displayName,
        BusinessTypeEnum businessType,
        String appendPrefix,
        String bizCategoryId
    ) {
        Update update;
        if (StringUtils.isBlank(bizCategoryId)) {
            update = entityUpdate(update("DN", displayName).set("BT", businessType).set("AP", appendPrefix));
        } else {
            update = entityUpdate(update("DN", displayName).set("BT", businessType).set("AP", appendPrefix).set("BC", bizCategoryId));
        }

        UpdateResult updateResult = mongoTemplate.updateFirst(
            query(where("id").is(codeQR).and("TP").is(topic)),
            update,
            TokenQueueEntity.class,
            TABLE
        );

        return updateResult.getModifiedCount() == 1;
    }

    @Override
    public void changeStoreBusinessType(String codeQR, BusinessTypeEnum existingBusinessType, BusinessTypeEnum migrateToBusinessType) {
        mongoTemplate.updateFirst(
            query(where("id").is(codeQR).and("BT").is(existingBusinessType)),
            entityUpdate(update("BT", migrateToBusinessType)),
            TokenQueueEntity.class,
            TABLE);
    }
}
