package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteResult;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.utils.Formatter;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 4:45 PM
 */
@SuppressWarnings ({
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
            mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
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
        Assert.hasText(id, "Id empty for BizStore");
        return mongoTemplate.findOne(query(where("id").is(id)), BizStoreEntity.class);
    }

    @Override
    public void deleteHard(BizStoreEntity object) {
        mongoTemplate.remove(object);
    }

    public BizStoreEntity noStore() {
        return mongoTemplate.findOne(query(where("AD").is("")), BizStoreEntity.class);
    }

    public BizStoreEntity findByPhone(String phone) {
        return mongoTemplate.findOne(query(where("PH").is(Formatter.phoneCleanup(phone))), BizStoreEntity.class);
    }

    @Override
    public List<BizStoreEntity> findAllWithAnyAddressAnyPhone(
            String bizAddress,
            String bizPhone,
            BizNameEntity bizName
    ) {
        Criteria criteriaA = new Criteria();
        if (StringUtils.isNotEmpty(bizAddress)) {
            criteriaA.and("AD").regex(bizAddress, "i");
        }
        if (StringUtils.isNotEmpty(bizPhone)) {
            criteriaA.and("PH").regex(bizPhone, "i");
        }

        if (bizName != null && StringUtils.isNotEmpty(bizName.getId())) {
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

        if (bizName != null && StringUtils.isNotEmpty(bizName.getId())) {
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
                query(
                        where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId())))
                        .with(new Sort(Sort.Direction.DESC, "C"))
                        .limit(limit),
                BizStoreEntity.class
        );
    }

    @Override
    public BizStoreEntity findOne(String bizNameId) {
        return mongoTemplate.findOne(
                query(
                        where("BIZ_NAME.$id").is(new ObjectId(bizNameId)))
                        .with(new Sort(Sort.Direction.DESC, "C"))
                ,
                BizStoreEntity.class
        );
    }

    @Override
    public List<BizStoreEntity> getAll(int skip, int limit) {
        return mongoTemplate.find(new Query().skip(skip).limit(limit), BizStoreEntity.class);
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
                BizStoreEntity.class
        );
    }

    @Override
    public long getCountOfStore(String bizNameId) {
        return mongoTemplate.count(
                query(
                        where("BIZ_NAME.$id").is(new ObjectId(bizNameId))
                                .andOperator(isNotDeleted())
                ),
                BizStoreEntity.class
        );
    }

    @Override
    public List<BizStoreEntity> getAllBizStores(String bizNameId) {
        return mongoTemplate.find(
                query(
                        where("BIZ_NAME.$id").is(new ObjectId(bizNameId))
                                .andOperator(isNotDeleted())
                ),
                BizStoreEntity.class
        );
    }

    @Override
    public BizStoreEntity findByCodeQR(String codeQR) {
        return mongoTemplate.findOne(query(where("QR").is(codeQR)), BizStoreEntity.class);
    }

    @Override
    public boolean isValidCodeQR(String codeQR) {
        return mongoTemplate.exists(query(where("QR").is(codeQR)), BizStoreEntity.class);
    }

    @Override
    public boolean setNextRun(String id, String zoneId, Date queueHistory) {
        LOG.info("Set next run for id={} zoneId={} queueHistory={}", id, zoneId, queueHistory);
        WriteResult writeResult = mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(update("TZ", zoneId).set("QH", queueHistory)),
                BizStoreEntity.class,
                TABLE
        );

        return writeResult.getN() > 0;
    }

    @Override
    public List<BizStoreEntity> findAllQueueEndedForTheDay(Date now) {
        return mongoTemplate.find(
                query(where("QH").lte(now).and("A").is(true)),
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
