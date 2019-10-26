package com.noqapp.repository;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.SurveyEntity;
import com.noqapp.domain.aggregate.SurveyGroupedValue;
import com.noqapp.domain.types.SentimentTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

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

    @Override
    public SurveyGroupedValue findOverallRating(String bizStoreId) {
        Assert.hasLength(bizStoreId, "bizStoreId cannot be empty");

        /* To group additional field add next to QID with comma separated like "QID", "XYZ" */
        GroupOperation groupByStateAndSumPop = group("BS")
            .avg("OR").as("summationOverallRating")
            .count().as("numberOfSurvey");

        MatchOperation filterStates = match(where("BS").is(bizStoreId).and("C").gte(DateUtil.minusDays(30)));
        Aggregation aggregation = newAggregation(filterStates, groupByStateAndSumPop);
        AggregationResults<SurveyGroupedValue> result = mongoTemplate.aggregate(aggregation, TABLE, SurveyGroupedValue.class);
        List<SurveyGroupedValue> groupedValues = result.getMappedResults();

        SurveyGroupedValue surveyGroupedValue = null;
        if (groupedValues.size() > 0) {
            surveyGroupedValue = groupedValues.iterator().next();

            aggregation = newAggregation(
                match(where("BS").is(bizStoreId).and("C").gte(DateUtil.minusDays(30)).and("ST").is(SentimentTypeEnum.P)),
                group("BS")
                    .count().as("sumOfPositiveSentiments"));

            result = mongoTemplate.aggregate(aggregation, TABLE, SurveyGroupedValue.class);
            groupedValues = result.getMappedResults();
            SurveyGroupedValue surveyGroupedValue1 = groupedValues.iterator().next();

            aggregation = newAggregation(
                match(where("BS").is(bizStoreId).and("C").gte(DateUtil.minusDays(30)).and("ST").is(SentimentTypeEnum.N)),
                group("BS")
                    .count().as("sumOfNegativeSentiments"));

            result = mongoTemplate.aggregate(aggregation, TABLE, SurveyGroupedValue.class);
            groupedValues = result.getMappedResults();
            SurveyGroupedValue surveyGroupedValue2 = groupedValues.iterator().next();

            surveyGroupedValue
                .setSumOfPositiveSentiments(surveyGroupedValue1.getSumOfPositiveSentiments())
                .setSumOfNegativeSentiments(surveyGroupedValue2.getSumOfNegativeSentiments())
                .setBizStoreId(bizStoreId);
        }
        return surveyGroupedValue;
    }
}
