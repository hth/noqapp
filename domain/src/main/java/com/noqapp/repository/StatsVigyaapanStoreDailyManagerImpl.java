package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StatsVigyaapanStoreDailyEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 2018-12-20 10:54
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class StatsVigyaapanStoreDailyManagerImpl implements StatsVigyaapanStoreDailyManager {
    private static final Logger LOG = LoggerFactory.getLogger(StatsVigyaapanStoreDailyManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        StatsVigyaapanStoreDailyEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public StatsVigyaapanStoreDailyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(StatsVigyaapanStoreDailyEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(StatsVigyaapanStoreDailyEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public void tagAsDisplayed(String codeQR, int dayOfWeek) {
        mongoTemplate.findAndModify(
            query(where("BS").is(codeQR).and("DW").is(dayOfWeek)),
            entityUpdate(new Update().inc("TD", 1)),
            StatsVigyaapanStoreDailyEntity.class,
            TABLE
        );
    }
}
