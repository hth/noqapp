package com.token.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.InviteEntity;
import com.token.domain.aggregate.GroupedValue;

import java.util.List;

/**
 * User: hitender
 * Date: 3/29/17 10:40 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class InviteManagerImpl implements InviteManager {
    private static final Logger LOG = LoggerFactory.getLogger(EmailValidateManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            InviteEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public InviteManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(InviteEntity object) {
        mongoTemplate.save(object);
    }

    @Override
    public void deleteHard(InviteEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public int getRemoteScanCount(String rid) {
        int sum = 0;
        /* To group additional field add next to RID with comma separated like "RID", "XYZ" */
        GroupOperation groupByStateAndSumPop = group("RID")
                .sum("RSR").as("summation");

        MatchOperation filterStates = match(where("A").is(true).and("RID").is(rid));
        Aggregation aggregation = newAggregation(filterStates, groupByStateAndSumPop);
        AggregationResults<GroupedValue> result = mongoTemplate.aggregate(aggregation, TABLE, GroupedValue.class);
        List<GroupedValue> groupedValues = result.getMappedResults();
        if (groupedValues.size() > 0) {
            sum = groupedValues.iterator().next().getSummation();
        }

        groupByStateAndSumPop = group("IID")
                .sum("RSI").as("summation");

        filterStates = match(where("A").is(true).and("IID").is(rid));
        aggregation = newAggregation(filterStates, groupByStateAndSumPop);
        result = mongoTemplate.aggregate(aggregation, TABLE, GroupedValue.class);
        groupedValues = result.getMappedResults();
        if (groupedValues.size() > 0) {
            sum += groupedValues.iterator().next().getSummation();
        }
        return sum;
    }
}
