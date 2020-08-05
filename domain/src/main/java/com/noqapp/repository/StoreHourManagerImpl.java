package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.StoreHourEntity;

import com.mongodb.client.result.UpdateResult;

import org.bson.types.ObjectId;

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

/**
 * User: hitender
 * Date: 6/13/17 6:44 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class StoreHourManagerImpl implements StoreHourManager {
    private static final Logger LOG = LoggerFactory.getLogger(StoreHourManagerImpl.class);
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
            query(where("BS").is(bizStoreId)),
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
            query(where("BS").is(bizStoreId).and("DW").is(dayOfWeek)),
            StoreHourEntity.class,
            TABLE
        );
    }

    @Override
    public List<StoreHourEntity> findAll(String bizStoreId) {
        return mongoTemplate.find(
            query(where("BS").is(bizStoreId)).with(Sort.by(Sort.Direction.ASC, "DW")),
            StoreHourEntity.class,
            TABLE
        );
    }

    @Override
    public StoreHourEntity modifyOne(
        String bizStoreId,
        DayOfWeek dayOfWeek,
        int tokenAvailableFrom,
        int startHour,
        int tokenNotAvailableFrom,
        int endHour,
        int lunchTimeStart,
        int lunchTimeEnd,
        boolean dayClosed,
        boolean tempDayClosed,
        boolean preventJoining,
        int delayedInMinutes
    ) {
        LOG.info("Hour Change for bizStoreId={} " +
                "dayOfWeek={} " +
                "tokenAvailableFrom={} " +
                "startHour={} " +
                "tokenNotAvailableFrom={} " +
                "endHour={} " +
                "lunchTimeStart={} " +
                "lunchTimeEnd={} " +
                "dayClosed={} " +
                "tempDayClosed={} " +
                "preventJoining={} " +
                "delayedInMinutes={}",
            bizStoreId,
            dayOfWeek.getValue(),
            tokenAvailableFrom,
            startHour,
            tokenNotAvailableFrom,
            endHour,
            lunchTimeStart,
            lunchTimeEnd,
            dayClosed,
            tempDayClosed,
            preventJoining,
            delayedInMinutes);
        return mongoTemplate.findAndModify(
            query(where("BS").is(bizStoreId).and("DW").is(dayOfWeek.getValue())),
            entityUpdate(update("TF", tokenAvailableFrom)
                .set("SH", startHour)
                .set("TE", tokenNotAvailableFrom)
                .set("EH", endHour)
                .set("LS", lunchTimeStart)
                .set("LE", lunchTimeEnd)
                .set("DC", dayClosed)
                .set("TC", tempDayClosed)
                .set("PJ", preventJoining)
                .set("DE", delayedInMinutes)),
            FindAndModifyOptions.options().returnNew(true),
            StoreHourEntity.class,
            TABLE
        );
    }

    @Override
    public boolean resetTemporarySettingsOnStoreHour(String id) {
        UpdateResult updateResult = mongoTemplate.updateFirst(
            query(where("id").is(new ObjectId(id))),
            entityUpdate(
                update("PJ", false)
                    .set("DE", 0)
                    .set("TC", false)
            ),
            StoreHourEntity.class,
            TABLE
        );

        LOG.info("ResetStoreHour id={} ack={} modifiedCount={}", id, updateResult.wasAcknowledged(), updateResult.getModifiedCount());
        return updateResult.wasAcknowledged();
    }

    public void resetQueueSettingWhenQueueStarts(String bizStoreId, DayOfWeek dayOfWeek) {
        UpdateResult result = mongoTemplate.updateFirst(
            query(where("BS").is(bizStoreId).and("DW").is(dayOfWeek.getValue())),
            entityUpdate(update("DE", 0)),
            StoreHourEntity.class,
            TABLE
        );

        LOG.info("ResetQueueSettingWhenQueueStarts ack={} modifiedCount={}", result.wasAcknowledged(), result.getModifiedCount());
    }
}
