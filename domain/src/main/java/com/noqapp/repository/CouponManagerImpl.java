package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.CouponEntity;

import com.mongodb.client.result.UpdateResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 13:39
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class CouponManagerImpl implements CouponManager {
    private static final Logger LOG = LoggerFactory.getLogger(CouponManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        CouponEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    public CouponManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(CouponEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(CouponEntity object) {

    }

    @Override
    public List<CouponEntity> findActiveBusinessCouponByBizNameId(String bizNameId) {
        Date midnight = DateUtil.nowMidnightDate();
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("QID").exists(false)
                .andOperator(
                    where("SD").lte(midnight),
                    where("ED").gte(midnight)
                ).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public List<CouponEntity> findUpcomingBusinessCouponByBizNameId(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("QID").exists(false).and("SD").gte(DateUtil.nowMidnightDate()).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public long inActiveCouponWithDiscountId(String discountId) {
        UpdateResult updateResult = mongoTemplate.updateMulti(
            query(where("DI").is(discountId)),
            entityUpdate(update("A", false)),
            CouponEntity.class,
            TABLE
        );

        return updateResult.getModifiedCount();
    }

    @Override
    public List<CouponEntity> findExistingCouponWithDiscountId(String discountId) {
        return mongoTemplate.find(
            query(where("DI").is(discountId).and("ED").lte(DateUtil.nowMidnightDate()).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public CouponEntity findById(String couponId) {
        return mongoTemplate.findById(couponId, CouponEntity.class, TABLE);
    }

    @Override
    public List<CouponEntity> findActiveClientCouponByQID(String qid) {
        Date midnight = DateUtil.nowMidnightDate();
        return mongoTemplate.find(
            query(where("QID").is(qid)
                .andOperator(
                    where("SD").lte(midnight),
                    where("ED").gte(midnight)
                ).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }
}
