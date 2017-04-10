package com.noqapp.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
import org.springframework.util.Assert;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.InviteEntity;
import com.noqapp.domain.aggregate.GroupedValue;

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
        Assert.hasLength(rid, "RID cannot be empty");

        int sum = 0;

        /* To group additional field add next to RID with comma separated like "RID", "XYZ" */
        GroupOperation groupByStateAndSumPop = group("RID")
                .sum("RSR").as("summation");

        MatchOperation filterStates = match(where("RID").is(rid).and("A").is(true));
        Aggregation aggregation = newAggregation(filterStates, groupByStateAndSumPop);
        AggregationResults<GroupedValue> result = mongoTemplate.aggregate(aggregation, TABLE, GroupedValue.class);
        List<GroupedValue> groupedValues = result.getMappedResults();
        if (0 < groupedValues.size()) {
            sum = groupedValues.iterator().next().getSummation();
        }

        groupByStateAndSumPop = group("IID")
                .sum("RSI").as("summation");

        filterStates = match(where("IID").is(rid).and("A").is(true));
        aggregation = newAggregation(filterStates, groupByStateAndSumPop);
        result = mongoTemplate.aggregate(aggregation, TABLE, GroupedValue.class);
        groupedValues = result.getMappedResults();
        if (0 < groupedValues.size()) {
            sum += groupedValues.iterator().next().getSummation();
        }

        return sum;
    }

    public boolean deductRemoteScanCount(String rid) {
        Assert.hasLength(rid, "RID cannot be empty");

        InviteEntity invite = mongoTemplate.findOne(
                query(where("A").is(true)
                        .orOperator(
                                where("RID").is(rid).and("RSR").gt(0),
                                where("IID").is(rid).and("RSI").gt(0)
                        )
                ),
                InviteEntity.class,
                TABLE);

        boolean updated = false;
        if (invite.getReceiptUserId().equalsIgnoreCase(rid)) {
            invite.deductRemoteScanForReceiptUserCount();
            updated = true;
        } else if (invite.getInviterId().equalsIgnoreCase(rid)) {
            invite.deductRemoteScanForInviterCount();
            updated = true;
        }

        if (0 == invite.getRemoteScanForReceiptUserCount() && 0 == invite.getRemoteScanForInviterCount()) {
            invite.inActive();
        }

        save(invite);
        return updated;
    }
}
