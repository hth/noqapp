package com.noqapp.repository;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.SurveyEntity;
import com.noqapp.domain.types.SentimentTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 10/20/19 6:38 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class SurveyManagerImpl implements SurveyManager {
    private static final Logger LOG = LoggerFactory.getLogger(StoreProductManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        SurveyEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    public SurveyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(SurveyEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(SurveyEntity object) {

    }

    @Override
    public SurveyEntity getRecentOverallRating(String bizNameId) {
        return mongoTemplate.findAndModify(
            query(where("BN").is(bizNameId).and("FE").is(false)).with(new Sort(ASC, "C")),
            update("FE", true),
            FindAndModifyOptions.options().returnNew(true),
            SurveyEntity.class,
            TABLE
        );
    }

    @Override
    public void updateSentiment(String id, SentimentTypeEnum sentimentType) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            update("ST", sentimentType),
            SurveyEntity.class,
            TABLE
        );
    }
}
