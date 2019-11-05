package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.AdvertisementDisplayEnum;
import com.noqapp.domain.types.AdvertisementTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-16 13:32
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class AdvertisementManagerImpl implements AdvertisementManager {
    private static final Logger LOG = LoggerFactory.getLogger(AdvertisementManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        AdvertisementEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public AdvertisementManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(AdvertisementEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public List<AdvertisementEntity> findAllAdvertisements(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId).and("D").is(false)).with(Sort.by(Sort.Direction.DESC, "C")),
            AdvertisementEntity.class,
            TABLE
        );
    }

    @Override
    public List<AdvertisementEntity> findApprovalPendingAdvertisements() {
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.P)
                .and("D").is(false)
                .and("A").is(true)
            ).with(Sort.by(Sort.Direction.ASC, "C")),
            AdvertisementEntity.class,
            TABLE
        );
    }

    @Override
    public AdvertisementEntity findById(String advertisementId) {
        return mongoTemplate.findById(
            advertisementId,
            AdvertisementEntity.class,
            TABLE
        );
    }

    @Override
    public long findApprovalPendingAdvertisementCount() {
        return mongoTemplate.count(
            query(where("VS").is(ValidateStatusEnum.P)),
            AdvertisementEntity.class,
            TABLE
        );
    }

    @Override
    public List<AdvertisementEntity> findAllMobileClientApprovedAdvertisements(int limit) {
        Date now = new Date();
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.A)
                .and("AD").is(AdvertisementDisplayEnum.MC)
                .and("AT").is(AdvertisementTypeEnum.MA)
                .and("PD").lte(now)
                .and("ED").gte(now)
                .and("D").is(false)
                .and("A").is(true)
            ).with(Sort.by(Sort.Direction.ASC, "C")).limit(limit),
            AdvertisementEntity.class,
            TABLE
        );
    }

    @Override
    public List<AdvertisementEntity> findAllMobileClientApprovedAdvertisements(Point point, double maxDistance, int limit) {
        Date now = new Date();
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.A)
                .and("AD").is(AdvertisementDisplayEnum.MC)
                .and("AT").is(AdvertisementTypeEnum.MA)
                .and("COR").near(point).maxDistance(maxDistance)
                .and("PD").lte(now)
                .and("ED").gte(now)
                .and("D").is(false)
                .and("A").is(true)
            ).with(Sort.by(Sort.Direction.ASC, "C")).limit(limit),
            AdvertisementEntity.class,
            TABLE
        );
    }

    @Override
    public List<AdvertisementEntity> findAllMobileMerchantApprovedAdvertisements(int limit) {
        Date now = new Date();
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.A)
                .and("AD").is(AdvertisementDisplayEnum.MM)
                .and("AT").is(AdvertisementTypeEnum.MA)
                .and("PD").lte(now)
                .and("ED").gte(now)
                .and("D").is(false)
                .and("A").is(true)
            ).with(Sort.by(Sort.Direction.ASC, "C")).limit(limit),
            AdvertisementEntity.class,
            TABLE
        );
    }

    @Override
    public List<AdvertisementEntity> findAllMobileTVApprovedAdvertisements(String bizNameId, int limit) {
        Date now = new Date();
        return mongoTemplate.find(
            query(where("VS").is(ValidateStatusEnum.A)
                .and("BN").is(bizNameId)
                .and("AD").is(AdvertisementDisplayEnum.TV)
                .and("PD").lte(now)
                .and("ED").gte(now)
                .and("D").is(false)
                .and("A").is(true)
            ).with(Sort.by(Sort.Direction.ASC, "C")).limit(limit),
            AdvertisementEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteHard(AdvertisementEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
