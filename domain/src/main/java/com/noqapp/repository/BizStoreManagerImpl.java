package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.domain.types.ServicePaymentEnum;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/23/16 4:45 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class BizStoreManagerImpl implements BizStoreManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizStoreEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizStoreManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BizStoreEntity object) {
        if (null != object.getBizName() && null != object.getBizName().getId()) {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } else {
            LOG.error("Cannot save bizStore without bizName");
            throw new RuntimeException("Missing BizName for BizStore " + object.getAddress());
        }
    }

    @Override
    public BizStoreEntity getById(String id) {
        try {
            Assert.hasText(id, "Id empty for BizStore");
            return mongoTemplate.findOne(query(where("id").is(id)), BizStoreEntity.class);
        } catch (Exception e) {
            LOG.error("Failed to find BizStoreId={} reason={}", id, e.getLocalizedMessage(), e);
            return null;
        }
    }

    @Override
    public boolean exists(String id) {
        try {
            Assert.hasText(id, "Id empty for BizStore");
            return mongoTemplate.exists(query(where("id").is(id)), BizStoreEntity.class);
        } catch (Exception e) {
            LOG.error("Failed to find BizStoreId={} reason={}", id, e.getLocalizedMessage(), e);
            return false;
        }
    }

    @Override
    public void deleteHard(BizStoreEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    public BizStoreEntity noStore() {
        return mongoTemplate.findOne(query(where("AD").is("")), BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> findAllWithAnyAddressAnyPhone(
            String bizAddress,
            String bizPhone,
            BizNameEntity bizName
    ) {
        Criteria criteriaA = new Criteria();
        if (StringUtils.isNotBlank(bizAddress)) {
            criteriaA.and("AD").regex(bizAddress, "i");
        }
        if (StringUtils.isNotBlank(bizPhone)) {
            criteriaA.and("PH").regex(bizPhone, "i");
        }

        if (bizName != null && StringUtils.isNotBlank(bizName.getId())) {
            Criteria criteriaB = where("BIZ_NAME.$id").is(new ObjectId(bizName.getId()));
            return mongoTemplate.find(
                    query(criteriaB).addCriteria(criteriaA).limit(PaginationEnum.TEN.getLimit()),
                    BizStoreEntity.class
            );
        } else {
            return mongoTemplate.find(
                    query(criteriaA).limit(PaginationEnum.TEN.getLimit()),
                    BizStoreEntity.class
            );
        }
    }

    @Override
    public List<BizStoreEntity> findAllWithStartingAddressStartingPhone(
            String bizAddress,
            String bizPhone,
            BizNameEntity bizName
    ) {
        Query query = null;
        if (StringUtils.isNotBlank(bizAddress) && StringUtils.isNotBlank(bizPhone)) {
            query = query(
                    new Criteria().orOperator(
                            where("AD").regex("^" + bizAddress, "i"),
                            where("PH").regex("^" + bizPhone, "i"))
            );
        } else if (StringUtils.isNotBlank(bizAddress)) {
            query = query(where("AD").regex("^" + bizAddress, "i"));
        } else if (StringUtils.isNotBlank(bizPhone)) {
            query = query(where("PH").regex("^" + bizPhone, "i"));
        }

        if (bizName != null && StringUtils.isNotBlank(bizName.getId())) {
            Criteria criteriaA = where("BIZ_NAME.$id").is(new ObjectId(bizName.getId()));
            if (null == query) {
                query = query(criteriaA);
            } else {
                query.addCriteria(criteriaA);
            }
        }
        Assert.notNull(query, "Query cannot be null");
        return mongoTemplate.find(query.limit(PaginationEnum.TEN.getLimit()), BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> getAllWithJustSpecificField(
            String bizPhone,
            String bizAddress,
            String bizId,
            String fieldName
    ) {
        Query query;
        if (StringUtils.isBlank(bizAddress) && StringUtils.isBlank(bizPhone)) {
            Criteria criteriaC = where("BIZ_NAME.$id").is(new ObjectId(bizId));
            query = query(criteriaC);
        } else if (StringUtils.isNotBlank(bizAddress) && StringUtils.isBlank(bizPhone)) {
            Criteria criteriaB = where("AD").regex("^" + bizAddress, "i");
            Criteria criteriaC = where("BIZ_NAME.$id").is(new ObjectId(bizId));

            query = query(criteriaC).addCriteria(criteriaB);
        } else if (StringUtils.isNotBlank(bizPhone) && StringUtils.isBlank(bizAddress)) {
            Criteria criteriaA = where("PH").regex("^" + bizPhone, "i");
            Criteria criteriaC = where("BIZ_NAME.$id").is(new ObjectId(bizId));

            query = query(criteriaC).addCriteria(criteriaA);
        } else {
            Criteria criteriaA = where("PH").regex("^" + bizPhone, "i");
            Criteria criteriaB = where("AD").regex("^" + bizAddress, "i");
            Criteria criteriaC = where("BIZ_NAME.$id").is(new ObjectId(bizId));

            query = query(criteriaC).addCriteria(criteriaB).addCriteria(criteriaA);
        }
        query.fields().include(fieldName);
        return mongoTemplate.find(query, BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> findAllAddress(BizNameEntity bizNameEntity, int limit) {
        return mongoTemplate.find(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId())))
                        .with(new Sort(Sort.Direction.DESC, "C"))
                        .limit(limit),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public BizStoreEntity findOne(String bizNameId) {
        return mongoTemplate.findOne(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameId))).with(new Sort(Sort.Direction.DESC, "C")),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public List<BizStoreEntity> getAll(int skip, int limit) {
        return mongoTemplate.find(new Query().skip(skip).limit(limit), BizStoreEntity.class, TABLE);
    }

    @Override
    public List<BizStoreEntity> getAllWhereNotValidatedUsingExternalAPI(int validationCountTry, int skip, int limit) {
        return mongoTemplate.find(
                query(
                        where("EA").is(false)
                                .orOperator(
                                        where("VC").exists(false),
                                        where("VC").lt(validationCountTry)
                                )
                ).skip(skip).limit(limit),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public long getCountOfStore(String bizNameId) {
        return mongoTemplate.count(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameId)).andOperator(isNotDeleted())),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public List<BizStoreEntity> getAllBizStores(String bizNameId) {
        return mongoTemplate.find(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameId)).andOperator(isNotDeleted())).with(new Sort(ASC, "DN")),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public List<BizStoreEntity> getAllBizStores(String bizNameId, Point point, double maxDistance) {
        return mongoTemplate.find(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameId)).and("COR").near(point).maxDistance(maxDistance).andOperator(isNotDeleted())),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public List<BizStoreEntity> getAllBizStoresMatchingAddress(String bizStoreAddress, String bizNameId) {
        return mongoTemplate.find(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameId)).and("AD").is(bizStoreAddress).and("D").is(false)),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public BizStoreEntity findByCodeQR(String codeQR) {
        return mongoTemplate.findOne(query(where("QR").is(codeQR)), BizStoreEntity.class, TABLE);
    }

    @Override
    public boolean isValidCodeQR(String codeQR) {
        return mongoTemplate.exists(query(where("QR").is(codeQR)), BizStoreEntity.class, TABLE);
    }

    @Override
    public boolean updateNextRun(String id, String zoneId, Date archiveNextRun) {
        return updateNextRunAndRatingWithAverageServiceTime(id, zoneId, archiveNextRun, 0, 0, 0);
    }

    @Override
    public boolean updateNextRunAndRatingWithAverageServiceTime(
            String id,
            String zoneId,
            Date archiveNextRun,
            float rating,
            int ratingCount,
            long averageServiceTime
    ) {
        LOG.info("Set next run for id={} zoneId={} archiveNextRun={} rating={} averageServiceTime={}",
                id,
                zoneId,
                archiveNextRun,
                rating,
                averageServiceTime);

        Update update;
        if (rating == 0 && ratingCount == 0) {
            update = entityUpdate(update("TZ", zoneId)
                    .set("QH", archiveNextRun));
        } else {
            update = entityUpdate(update("TZ", zoneId)
                    .set("QH", archiveNextRun)
                    .set("RC", ratingCount)
                    .set("RA", rating));
        }

        /* Do not update the average service time when its zero. */
        if (0 != averageServiceTime) {
            update.set("AS", averageServiceTime);
        }

        return mongoTemplate.updateFirst(
                query(where("id").is(id)),
                update,
                BizStoreEntity.class,
                TABLE
        ).getModifiedCount() > 0;
    }

    @Override
    public List<BizStoreEntity> findAllQueueEndedForTheDay(Date now) {
        LOG.info("Fetch past now={}", now);
        return mongoTemplate.find(
                query(where("QH").lte(now).and("BT").in(BusinessTypeEnum.getSelectedMessageOrigin(MessageOriginEnum.Q)).and("A").is(true).and("D").is(false)),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public List<BizStoreEntity> findAllOrderEndedForTheDay(Date now) {
        LOG.info("Fetch past now={}", now);
        return mongoTemplate.find(
            query(where("QH").lte(now).and("BT").in(BusinessTypeEnum.getSelectedMessageOrigin(MessageOriginEnum.O)).and("A").is(true).and("D").is(false)),
            BizStoreEntity.class,
            TABLE
        );
    }

    @Override
    public Stream<BizStoreEntity> findAllWithStream() {
        return mongoTemplate.find(
            query(where("A").is(true).and("D").is(false)),
            BizStoreEntity.class,
            TABLE
        ).stream();
    }

    @Override
    public void updateBizStoreAvailableTokenCount(int availableTokenCount, String codeQR) {
        mongoTemplate.updateFirst(
                query(where("QR").is(codeQR)),
                entityUpdate(update("AT", availableTokenCount)),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public long countCategoryUse(String bizCategoryId, String bizNameId) {
        return mongoTemplate.count(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameId)).and("BC").is(bizCategoryId)),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public List<BizStoreEntity> getBizStoresByCategory(String bizCategoryId, String bizNameId) {
        return mongoTemplate.find(
                query(where("BIZ_NAME.$id").is(new ObjectId(bizNameId)).and("BC").is(bizCategoryId)).with(new Sort(ASC, "DN")),
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public boolean doesWebLocationExists(String webLocation, String id) {
        Query query;
        if (StringUtils.isBlank(id)) {
            query = query(where("WL").is(webLocation));
        } else {
            query = query(where("WL").is(webLocation).and("_id").ne(id));
        }
        return mongoTemplate.exists(
                query,
                BizStoreEntity.class,
                TABLE
        );
    }

    @Override
    public void unsetScheduledTask(String id) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(new Update().unset("TA")),
            BizStoreEntity.class,
            TABLE
        );
    }

    @Override
    public void setScheduleTaskId(String codeQR, String scheduleTaskId) {
        mongoTemplate.updateFirst(
            query(where("QR").is(codeQR)),
            entityUpdate(update("TA", scheduleTaskId)),
            BizStoreEntity.class,
            TABLE
        );
    }

    @Override
    public void activeInActive(String id, boolean active) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(update("A", active)),
            BizStoreEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteSoft(String id) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(update("D", true)),
            BizStoreEntity.class,
            TABLE
        );
    }

    @Override
    public BizStoreEntity disableServiceCost(String codeQR) {
        return mongoTemplate.findAndModify(
            query(where("QR").is(codeQR)),
            entityUpdate(update("EP", false)),
            FindAndModifyOptions.options().returnNew(true),
            BizStoreEntity.class,
            TABLE
        );
    }

    @Override
    public BizStoreEntity updateServiceCost(String codeQR, int productPrice, int cancellationPrice, ServicePaymentEnum servicePayment) {
        return mongoTemplate.findAndModify(
            query(where("QR").is(codeQR)),
            entityUpdate(update("EP", true)
                .set("PP", productPrice)
                .set("CF", cancellationPrice)
                .set("SP", servicePayment)),
            FindAndModifyOptions.options().returnNew(true),
            BizStoreEntity.class,
            TABLE
        );
    }

    //TODO add query to for near and for nearBy with distance
    //db.getCollection('BIZ_STORE').find({COR : {$near : [27.70,74.46] }})
    //KM
    //db.getCollection('BIZ_STORE').find( { COR : { $near : [50,50] , $maxDistance : 1/111.12 } } )
    //Miles 69

}
