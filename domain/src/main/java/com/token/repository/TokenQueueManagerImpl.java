package com.token.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.TokenQueueEntity;

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

    }

    @Override
    public TokenQueueEntity findByCodeQR(String codeQR) {
        LOG.info("codeQR={}", codeQR);
        return mongoTemplate.findOne(query(where("id").is(codeQR)), TokenQueueEntity.class, TABLE);
    }

    @Override
    public TokenQueueEntity getNextToken(String codeQR) {
        return mongoTemplate.findAndModify(
                query(where("_id").is(codeQR)),
                new Update().inc("LN", 1),
                TokenQueueEntity.class,
                TABLE);
    }
}
