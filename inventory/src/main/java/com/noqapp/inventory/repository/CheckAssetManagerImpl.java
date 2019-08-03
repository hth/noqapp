package com.noqapp.inventory.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.inventory.domain.CheckAssetEntity;
import com.noqapp.repository.UserProfileManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-29 23:26
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class CheckAssetManagerImpl implements CheckAssetManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        CheckAssetEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public CheckAssetManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(CheckAssetEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void insertOrUpdate(CheckAssetEntity object) {
        mongoTemplate.upsert(
            query(where("_id").is(object.getId())),
            entityUpdate(update("AN", object.getAssetName())
                .set("RN", object.getRoomNumber())
                .set("FL", object.getFloor())
                .set("BN", object.getBizNameId())
                .set("C", new Date())
                .set("A", true)
                .set("D", false)),
            TABLE);
    }

    @Override
    public List<String> findDistinctFloors(String bizNameId) {
        return mongoTemplate.findDistinct(
            query(where("BN").is(bizNameId)),
            "FL",
            CheckAssetEntity.class,
            String.class
        );
    }

    @Override
    public List<String> findDistinctRoomsOnFloor(String bizNameId, String floor) {
        return mongoTemplate.findDistinct(
            query(where("BN").is(bizNameId).and("FL").is(floor)),
            "RN",
            CheckAssetEntity.class,
            String.class
        );
    }

    @Override
    public List<CheckAssetEntity> findAssetInRoom(String bizNameId, String floor, String room) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("FL").is(floor).and("RN").is(room)),
            CheckAssetEntity.class,
            TABLE
        );
    }

    @Override
    public CheckAssetEntity findById(String id) {
        return mongoTemplate.findById(id, CheckAssetEntity.class, TABLE);
    }

    @Override
    public List<CheckAssetEntity> findAllByBizNameId(String bizNameId) {
        return mongoTemplate.find(query(where("BN").is(bizNameId)), CheckAssetEntity.class, TABLE);
    }

    @Override
    public void deleteHard(CheckAssetEntity object) {
        mongoTemplate.remove(object);
    }
}
