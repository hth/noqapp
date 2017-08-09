package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.GenerateUserIds;

/**
 * User: hitender
 * Date: 11/19/16 12:33 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class GenerateUserIdManagerImpl implements GenerateUserIdManager {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateUserIdManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            GenerateUserIds.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public GenerateUserIdManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;

        GenerateUserIds generateUserIds = mongoTemplate.findById(
                GenerateUserIds.class.getName(),
                GenerateUserIds.class,
                TABLE);

        if (null == generateUserIds) {
            generateUserIds = GenerateUserIds.newInstance();
            save(generateUserIds);
        }
    }

    @Override
    public void save(GenerateUserIds object) {
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public String getNextAutoGeneratedUserId() {
        GenerateUserIds generateUserIds = mongoTemplate.findAndModify(
                query(where("_id").is(GenerateUserIds.class.getName())),
                new Update().inc("RID", 1),
                FindAndModifyOptions.options().returnNew(true),
                GenerateUserIds.class,
                TABLE);

        return String.valueOf(generateUserIds.getAutoGeneratedQueueUserId());
    }

    public long getLastGenerateUserId() {
        GenerateUserIds generateUserIds = mongoTemplate.findById(
                GenerateUserIds.class.getName(),
                GenerateUserIds.class,
                TABLE);

        if (null == generateUserIds) {
            LOG.debug("Collection {} not yet created", TABLE);
            return GenerateUserIds.STARTING_USER_ID;
        }
        return generateUserIds.getAutoGeneratedQueueUserId();
    }

    @Override
    public void deleteHard(GenerateUserIds object) {
        throw new UnsupportedOperationException("This operation is not supported");
    }
}
