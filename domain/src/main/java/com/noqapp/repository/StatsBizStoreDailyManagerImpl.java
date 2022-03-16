package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.fields;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.context.InvalidPersistentPropertyPath;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 6/16/17 4:48 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class StatsBizStoreDailyManagerImpl implements StatsBizStoreDailyManager {
    private static final Logger LOG = LoggerFactory.getLogger(StatsBizStoreDailyManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        StatsBizStoreDailyEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public StatsBizStoreDailyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(StatsBizStoreDailyEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(StatsBizStoreDailyEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public StatsBizStoreDailyEntity computeRatingForEachQueue(String bizStoreId) {
        try {
            TypedAggregation<StatsBizStoreDailyEntity> agg = newAggregation(StatsBizStoreDailyEntity.class,
                match(where("BS").is(bizStoreId).and("TR").gt(0)
                    .andOperator(
                        isActive(),
                        isNotDeleted()
                    )),
                group("bizStoreId")
                    .first("bizStoreId").as("BS")
                    .sum("totalRating").as("TR")
                    .sum("totalCustomerRated").as("CR")
            );
            /* Above totalCustomerRated in group is used as a placeholder to count the number of records that has TR > 0. */
            List<StatsBizStoreDailyEntity> statsBizStores = mongoTemplate.aggregate(agg, TABLE, StatsBizStoreDailyEntity.class).getMappedResults();
            if (statsBizStores.size() > 0) {
                LOG.info("Computing rating for each queue {}", statsBizStores.get(0));
                return statsBizStores.get(0);
            }

            return null;
        } catch (InvalidPersistentPropertyPath e) {
            LOG.error("Failed compute daily stats for BizStore id={} reason={}", bizStoreId, e.getLocalizedMessage(), e);
            return null;
        }
    }

    @Override
    public float computeRatingForBiz(String bizNameId) {
        return 0;
    }

    @Override
    public List<StatsBizStoreDailyEntity> findStores(String bizNameId, Date since) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("C").gte(since)),
            StatsBizStoreDailyEntity.class,
            TABLE
        );
    }

    @Override
    public StatsBizStoreDailyEntity repeatAndNewCustomers(String codeQR) {
        try {
            Date sinceBeginningOfThisMonth = DateUtil.sinceBeginningOfThisMonth();
            TypedAggregation<StatsBizStoreDailyEntity> agg = newAggregation(StatsBizStoreDailyEntity.class,
                match(where("QR").is(codeQR).and("C").gte(sinceBeginningOfThisMonth)
                    //match(where("QR").is(codeQR).and("C").gte(DateUtil.midnight(DateUtil.minusDays(30)))
                    .andOperator(
                        isActive(),
                        isNotDeleted()
                    )),
                group("codeQR")
                    .first("bizStoreId").as("BS")
                    .sum("clientsPreviouslyVisitedThisStore").as("VS")
                    .sum("totalServiced").as("TS")
                    .sum("totalClient").as("TC")
            );
            List<StatsBizStoreDailyEntity> statsBizStores = mongoTemplate.aggregate(agg, TABLE, StatsBizStoreDailyEntity.class).getMappedResults();

            StatsBizStoreDailyEntity statsBizStoreDaily;
            if (statsBizStores.size() > 0) {
                LOG.info("Computing rating for each queue {}", statsBizStores.get(0));
                statsBizStoreDaily = statsBizStores.get(0);
                statsBizStoreDaily.setMonthOfYear(DateUtil.getMonthFromDate(sinceBeginningOfThisMonth));
            } else {
                statsBizStoreDaily = new StatsBizStoreDailyEntity()
                    .setMonthOfYear(DateUtil.getMonthFromDate(sinceBeginningOfThisMonth));
            }
            return statsBizStoreDaily;
        } catch (InvalidPersistentPropertyPath e) {
            LOG.error("Failed compute stats on new customer codeQR={} reason={}", codeQR, e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * Console Query
     *
     * db.STATS_BIZ_STORE_DAILY.aggregate([
     *     { $match: { QR: "5ba1e6e4b85cb7297fadcc8c" }},
     *     { $project: { TS: "$TS", MN: {$month: "$C"}, YY: {$year: "$C"} }},
     *     { $group: { _id: {MN: "$MN", YY: "$YY"}, TS: { $sum: "$TS"} }}
     * ]);
     *
     * Note:
     * Match: Is like a select query
     * Project: Are the field you would like to process on. Its can contain multiple fields.
     * Expression: Some sub expression on existing date field to compute for month. MonthOfYear was added just for this reason.
     * Group: On a TotalServiced and populate fields like monthOfYear and CodeQR. Do sum of TotalServiced.
     *
     * @param codeQR
     * @return
     */
    @Override
    public List<StatsBizStoreDailyEntity> lastTwelveMonthVisits(String codeQR) {
        try {
            TypedAggregation<StatsBizStoreDailyEntity> agg = newAggregation(StatsBizStoreDailyEntity.class,
                match(where("QR").is(codeQR).and("C").gte(DateUtil.sinceOneYearAgo())
                    .andOperator(
                        isActive(),
                        isNotDeleted()
                    )),
                project("totalServiced")
                    .andExpression("month(created)").as("monthOfYear")
                    .andExpression("year(created)").as("year"),
                group(fields().and("monthOfYear").and("year"))
                    .first("monthOfYear").as("MN")
                    .first("year").as("YY")
                    .sum("totalServiced").as("TS")
            );
            return mongoTemplate.aggregate(agg, TABLE, StatsBizStoreDailyEntity.class).getMappedResults();
        } catch (InvalidPersistentPropertyPath e) {
            LOG.error("Failed compute stats on new customer codeQR={} reason={}", codeQR, e.getLocalizedMessage(), e);
            return null;
        }
    }
}
