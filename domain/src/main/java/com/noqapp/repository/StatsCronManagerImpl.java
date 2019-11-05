package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StatsCronEntity;

import com.mongodb.client.DistinctIterable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 12/10/16 8:01 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class StatsCronManagerImpl implements StatsCronManager {
    private static final Logger LOG = LoggerFactory.getLogger(StatsCronManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        StatsCronEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public StatsCronManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(StatsCronEntity object) {
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(StatsCronEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    @SuppressWarnings("unchecked")
    public DistinctIterable<String> getUniqueCronTasks() {
        return mongoTemplate.getCollection(TABLE).distinct("TN", String.class);
    }

    @Override
    public List<StatsCronEntity> getHistoricalData(String task, int limit) {
        return mongoTemplate.find(
            query(where("TN").is(task)).with(Sort.by(Sort.Direction.DESC, "C")).limit(10),
            StatsCronEntity.class,
            TABLE
        );
    }
}
