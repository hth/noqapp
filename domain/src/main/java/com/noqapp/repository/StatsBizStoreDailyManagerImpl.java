package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.context.InvalidPersistentPropertyPath;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
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
            /* Above totalCustomerRated in group is used as a place holder to count the number of records that has TR > 0. */
            List<StatsBizStoreDailyEntity> statsBizStores = mongoTemplate.aggregate(agg, TABLE, StatsBizStoreDailyEntity.class).getMappedResults();
            if (statsBizStores.size() > 0) {
                LOG.info("Computing rating for each queue {}", statsBizStores.get(0));
                return statsBizStores.get(0);
            }

            return null;
        } catch (InvalidPersistentPropertyPath e) {
            LOG.error("Failed compute daily stats for BizStore id={}", bizStoreId, e.getLocalizedMessage(), e);
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
            Query.query(where("BN").is(bizNameId).and("C").gte(since)),
            StatsBizStoreDailyEntity.class,
            TABLE
        );
    }
}
