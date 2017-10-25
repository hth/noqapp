package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StoreHourEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * User: hitender
 * Date: 6/13/17 6:44 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class StoreHourManagerImpl implements StoreHourManager {
    private static final Logger LOG = LoggerFactory.getLogger(TokenQueueManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            StoreHourEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public StoreHourManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(StoreHourEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void insertAll(List<StoreHourEntity> storeHours) {
        removeAll(storeHours.get(0).getBizStoreId());
        mongoTemplate.insertAll(storeHours);
    }

    @Override
    public void deleteHard(StoreHourEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public void removeAll(String bizStoreId) {
        mongoTemplate.remove(
                query(where("BZ").is(bizStoreId)),
                StoreHourEntity.class,
                TABLE);
    }

    @Override
    public StoreHourEntity findOne(String bizStoreId, DayOfWeek dayOfWeek) {
        return findOne(bizStoreId, dayOfWeek.getValue());
    }

    @Override
    public StoreHourEntity findOne(String bizStoreId, int dayOfWeek) {
        return mongoTemplate.findOne(
                query(where("BZ").is(bizStoreId).and("DW").is(dayOfWeek)),
                StoreHourEntity.class,
                TABLE
        );
    }

    @Override
    public List<StoreHourEntity> findAll(String bizStoreId) {
        return mongoTemplate.find(
                query(where("BZ").is(bizStoreId)).with(new Sort(Sort.Direction.ASC, "DW")),
                StoreHourEntity.class,
                TABLE
        );
    }

    @Override
    public StoreHourEntity modifyOne(
            String bizStoreId,
            DayOfWeek dayOfWeek,
            boolean preventJoining,
            boolean dayClosed
    ) {
        return mongoTemplate.findAndModify(
                query(where("BZ").is(bizStoreId).and("DW").is(dayOfWeek.getValue())),
                entityUpdate(update("PJ", preventJoining).set("DC", dayClosed)),
                FindAndModifyOptions.options().returnNew(true),
                StoreHourEntity.class,
                TABLE
        );
    }
}
