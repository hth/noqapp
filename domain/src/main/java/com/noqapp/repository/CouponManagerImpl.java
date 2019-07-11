package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.types.CouponGroupEnum;
import com.noqapp.domain.types.CouponTypeEnum;

import com.mongodb.client.result.UpdateResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public List<CouponEntity> findNearByCoupon(double longitude, double latitude) {
        Instant midnight = DateUtil.nowMidnightDate().toInstant();
        Query q = query(where("QID").exists(false)
            .and("CG").is(CouponGroupEnum.C)
            .and("SD").lte(midnight)
            .and("ED").gte(midnight)
            .and("A").is(true)
        );

        Point location = new Point(longitude, latitude);
        NearQuery query = NearQuery.near(location).maxDistance(new Distance(150, Metrics.KILOMETERS)).query(q);

        GeoResults<CouponEntity> geoResults = mongoTemplate.geoNear(query, CouponEntity.class, TABLE);
        return geoResults.getContent().stream().map(GeoResult::getContent).collect(Collectors.toList());
    }

    @Override
    public List<CouponEntity> findActiveCouponByBizNameId(String bizNameId, CouponGroupEnum couponGroup) {
        Instant midnight = DateUtil.nowMidnightDate().toInstant();
        return mongoTemplate.find(
            query(
                where("BN").is(bizNameId)
                    .and("CG").is(couponGroup)
                    .and("SD").lte(midnight)
                    .and("ED").gte(midnight)
                    .and("A").is(true)
            ).with(new Sort(DESC, "ED")),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public List<CouponEntity> findUpcomingCouponByBizNameId(String bizNameId, CouponGroupEnum couponGroup) {
        Instant midnight = DateUtil.nowMidnightDate().toInstant();
        return mongoTemplate.find(
            query(
                where("BN").is(bizNameId)
                    .and("CG").is(couponGroup)
                    .and("SD").gt(midnight)
                    .and("A").is(true)
            ).with(new Sort(DESC, "ED")),
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
    public long countActiveBusinessCouponByDiscountId(String discountId) {
        Instant midnight = DateUtil.nowMidnightDate().toInstant();
        return mongoTemplate.count(
            query(where("DI").is(discountId).and("ED").gte(midnight).and("CG").is(CouponGroupEnum.M).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public CouponEntity findById(String couponId) {
        return mongoTemplate.findById(couponId, CouponEntity.class, TABLE);
    }

    @Override
    public List<CouponEntity> findActiveClientCouponByQid(String qid) {
        Instant midnight = DateUtil.nowMidnightDate().toInstant();
        return mongoTemplate.find(
            query(where("QID").is(qid)
                .andOperator(
                    where("SD").lt(midnight),
                    where("ED").gte(midnight)
                ).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public List<CouponEntity> findActiveClientCouponByQid(String qid, String bizNameId) {
        Instant midnight = DateUtil.nowMidnightDate().toInstant();
        return mongoTemplate.find(
            query(where("QID").is(qid).and("BN").is(bizNameId)
                .andOperator(
                    where("SD").lt(midnight),
                    where("ED").gte(midnight)
                ).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public long countDiscountUsage(String discountId) {
        return mongoTemplate.count(
            query(where("DI").is(discountId).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public boolean checkIfCouponExistsForQid(String discountId, String qid) {
        return mongoTemplate.exists(
            query(where("DI").is(discountId).and("QID").is(qid).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }

    @Override
    public List<CouponEntity> findAllGlobalCouponForClient(String bizNameId) {
        Instant midnight = DateUtil.nowMidnightDate().toInstant();
        return mongoTemplate.find(
            query(where("BN").is(bizNameId)
                .and("CT").is(CouponTypeEnum.G)
                .and("CG").is(CouponGroupEnum.C)
                .andOperator(
                    where("SD").lt(midnight),
                    where("ED").gte(midnight)
                ).and("A").is(true)),
            CouponEntity.class,
            TABLE
        );
    }
}
