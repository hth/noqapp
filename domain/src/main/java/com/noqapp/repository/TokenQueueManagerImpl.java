package com.noqapp.repository;

import com.mongodb.WriteConcern;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.QueueStatusEnum;
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

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * User: hitender
 * Date: 12/16/16 8:51 AM
 */
@SuppressWarnings ({
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
        throw new UnsupportedOperationException("This method is not supported");
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
    public TokenQueueEntity getNextToken(String codeQR) {
        return mongoTemplate.findAndModify(
                query(where("_id").is(codeQR)),
                new Update().inc("LN", 1),
                FindAndModifyOptions.options().returnNew(true),
                TokenQueueEntity.class,
                TABLE);
    }

    @Override
    public TokenQueueEntity updateServing(String codeQR, int serving, QueueStatusEnum queueStatus) {
        return mongoTemplate.findAndModify(
                query(where("_id").is(codeQR)),
                entityUpdate(update("CS", serving).set("QS", queueStatus)),
                FindAndModifyOptions.options().returnNew(true),
                TokenQueueEntity.class,
                TABLE);
    }

    @Override
    public List<TokenQueueEntity> getTokenQueues(String[] ids) {
        return mongoTemplate.find(
                query(where("_id").in(Arrays.asList(ids))),
                TokenQueueEntity.class,
                TABLE
        );
    }

    @Override
    public void changeQueueStatus(String codeQR, QueueStatusEnum queueStatus) {
        if (mongoTemplate.getDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }
        mongoTemplate.updateFirst(
                query(where("_id").is(codeQR)),
                entityUpdate(update("QS", queueStatus)),
                TokenQueueEntity.class,
                TABLE);
    }

    @Override
    public void resetForNewDay(String codeQR) {
        mongoTemplate.updateFirst(
                query(where("_id").is(codeQR).and("QS").ne(QueueStatusEnum.C)),
                entityUpdate(update("LN", 0).set("CS", 0).set("QS", QueueStatusEnum.S)),
                TokenQueueEntity.class,
                TABLE);
    }
}
