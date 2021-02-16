package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.InviteEntity;
import com.noqapp.domain.aggregate.GroupedValue;

import com.mongodb.client.result.UpdateResult;

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

import java.util.List;

/**
 * User: hitender
 * Date: 3/29/17 10:40 PM
 */
@SuppressWarnings({
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
    public int computePoints(String qid) {
        try {
            Assert.hasLength(qid, "QID cannot be empty");

            int sum = 0;

            /* To group additional field add next to QID with comma separated like "QID", "XYZ" */
            GroupOperation groupByStateAndSumPop = group("QID")
                .sum("RJQ").as("summation");

            MatchOperation filterStates = match(where("QID").is(qid).and("A").is(true));
            Aggregation aggregation = newAggregation(filterStates, groupByStateAndSumPop);
            AggregationResults<GroupedValue> result = mongoTemplate.aggregate(aggregation, TABLE, GroupedValue.class);
            List<GroupedValue> groupedValues = result.getMappedResults();
            if (0 < groupedValues.size()) {
                sum = groupedValues.iterator().next().getSummation();
            }

            groupByStateAndSumPop = group("IID")
                .sum("RJI").as("summation");

            filterStates = match(where("IID").is(qid).and("A").is(true));
            aggregation = newAggregation(filterStates, groupByStateAndSumPop);
            result = mongoTemplate.aggregate(aggregation, TABLE, GroupedValue.class);
            groupedValues = result.getMappedResults();
            if (0 < groupedValues.size()) {
                sum += groupedValues.iterator().next().getSummation();
            }

            return sum;
        } catch (Exception e) {
            LOG.error("Failed computing remote join reason={}", e.getLocalizedMessage(), e);
            return 0;
        }
    }

    public boolean deductPoints(String qid) {
        try {
            Assert.hasLength(qid, "QID cannot be empty");

            InviteEntity invite = mongoTemplate.findOne(
                query(where("A").is(true)
                    .orOperator(
                        where("QID").is(qid).and("RJQ").gt(0),
                        where("IID").is(qid).and("RJI").gt(0)
                    )
                ),
                InviteEntity.class,
                TABLE);

            boolean updated = false;
            if (invite.getQueueUserId().equalsIgnoreCase(qid)) {
                invite.deductPointsForQueueUserCount();
                updated = true;
            } else if (invite.getInviterId().equalsIgnoreCase(qid)) {
                invite.deductPointsForInviterCount();
                updated = true;
            }

            if (0 == invite.getPointsForQueueUserCount() && 0 == invite.getPointsForInviterCount()) {
                invite.inActive();
            }

            save(invite);
            return updated;
        } catch (Exception e) {
            LOG.error("Failed deducting from remote join reason={}", e.getLocalizedMessage(), e);
            return false;
        }
    }

    public long increasePoints(int maxRemoteJoin) {
        UpdateResult updateResult = mongoTemplate.updateMulti(
            query(where("RJQ").lte(10).andOperator(isActive())),
            entityUpdate(update("RJQ", maxRemoteJoin)),
            InviteEntity.class,
            TABLE
        );

        return updateResult.getModifiedCount();
    }
}
