package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BizStoreDailyStatEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 6/16/17 4:48 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BizStoreDailyStatManagerImpl implements BizStoreDailyStatManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreDailyStatManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizStoreDailyStatEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizStoreDailyStatManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BizStoreDailyStatEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(BizStoreDailyStatEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public float computeRatingForEachQueue(String bizStoreId) {
        TypedAggregation<BizStoreDailyStatEntity> agg = newAggregation(BizStoreDailyStatEntity.class,
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
        List<BizStoreDailyStatEntity> bizStoreDailyStats = mongoTemplate.aggregate(agg, TABLE, BizStoreDailyStatEntity.class).getMappedResults();
        if (bizStoreDailyStats.size() > 0) {
            LOG.info("{}", bizStoreDailyStats.get(0));
            return (float) bizStoreDailyStats.get(0).getTotalRating() / bizStoreDailyStats.get(0).getTotalCustomerRated();
        }

        return 0;
    }

    @Override
    public float computeRatingForBiz(String bizNameId) {
        return 0;
    }
}
