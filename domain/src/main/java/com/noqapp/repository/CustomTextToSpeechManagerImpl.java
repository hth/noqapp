package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.CustomTextToSpeechEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 12/13/19 8:02 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class CustomTextToSpeechManagerImpl implements CustomTextToSpeechManager {
    private static final Logger LOG = LoggerFactory.getLogger(CustomTextToSpeechManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        CustomTextToSpeechEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public CustomTextToSpeechManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(CustomTextToSpeechEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(CustomTextToSpeechEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public CustomTextToSpeechEntity findOne(String bizNameId) {
        return mongoTemplate.findOne(
            query(where("BN").is(bizNameId)),
            CustomTextToSpeechEntity.class,
            TABLE
        );
    }
}
