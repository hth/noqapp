package com.token.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.token.domain.BaseEntity;
import com.token.domain.BrowserEntity;

/**
 * User: hitender
 * Date: 11/19/16 7:15 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class BrowserManagerImpl implements BrowserManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BrowserEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BrowserManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BrowserEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public BrowserEntity getByCookie(String cookie) {
        Assert.hasText(cookie, "Cookie is empty");
        return mongoTemplate.findOne(query(where("CK").is(cookie)), BrowserEntity.class);
    }

    @Override
    public void deleteHard(BrowserEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
