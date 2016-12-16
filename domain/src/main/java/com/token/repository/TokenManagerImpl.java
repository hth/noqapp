package com.token.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.TokenEntity;

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
public class TokenManagerImpl implements TokenManager {
    private static final Logger LOG = LoggerFactory.getLogger(TokenManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            TokenEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public TokenManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(TokenEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(TokenEntity object) {

    }

    @Override
    public TokenEntity findByCodeQR(String codeQR) {
        return mongoTemplate.findOne(query(where("id").is(codeQR)), TokenEntity.class, TABLE);
    }
}
