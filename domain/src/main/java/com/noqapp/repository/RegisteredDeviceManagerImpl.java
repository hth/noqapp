package com.noqapp.repository;

import static com.noqapp.common.utils.Constants.UNSUPPORTED_DEVICE_IN_FUTURE;
import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static com.noqapp.repository.util.AppendAdditionalFields.isActive;
import static com.noqapp.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.types.AppFlavorEnum;
import com.noqapp.domain.types.DeviceTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 3/1/17 12:27 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class RegisteredDeviceManagerImpl implements RegisteredDeviceManager {

    private static final Logger LOG = LoggerFactory.getLogger(RegisteredDeviceManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        RegisteredDeviceEntity.class,
        Document.class,
        "collection");

    @Value("${device.lastAccessed.now}")
    private String deviceLastAccessedNow;

    private MongoTemplate mongoTemplate;

    @Autowired
    public RegisteredDeviceManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(RegisteredDeviceEntity object) {
        if (null != object.getId()) {
            object.setUpdated();
        }
        mongoTemplate.save(object);
    }

    @Override
    public void deleteHard(RegisteredDeviceEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public RegisteredDeviceEntity find(String qid, String did) {
        Query query;
        if (StringUtils.isBlank(qid)) {
            query = query(where("DID").is(did));
        } else {
            /* Apply condition only if field exist. Solved using orOperator. */
            query = query(
                where("DID").is(did)
                    .orOperator(
                        where("QID").exists(false),
                        where("QID").is(qid)
                    ));
        }
        return mongoTemplate.findOne(
            query,
            RegisteredDeviceEntity.class,
            TABLE);
    }

    @Override
    public boolean updateDevice(
        String id,
        String did,
        String qid,
        DeviceTypeEnum deviceType,
        AppFlavorEnum appFlavor,
        String token,
        String model,
        String osVersion,
        String deviceLanguage,
        String cityName,
        double[] coordinate,
        String ipAddress,
        boolean sinceBeginning
    ) {
        if (StringUtils.isBlank(qid)) {
            Update update = entityUpdate(update("DT", deviceType)
                .set("AF", appFlavor)
                .set("TK", token)
                .set("MO", model)
                .set("OS", osVersion)
                .set("DL", deviceLanguage)
                .set("PN", new GeoJsonPoint(coordinate[0], coordinate[1]))
                .addToSet("CH", coordinate)
                .set("IP", ipAddress)
                .addToSet("IH", ipAddress)
                .set("SB", sinceBeginning));

            if (StringUtils.isNotBlank(cityName)) {
                update.set("CT", cityName);
            }

            return mongoTemplate.updateFirst(
                query(where("id").is(new ObjectId(id)).and("DID").is(did)),
                update,
                RegisteredDeviceEntity.class,
                TABLE
            ).getModifiedCount() > 0;
        }
        Update update = entityUpdate(update("QID", qid)
            .set("DT", deviceType)
            .set("AF", appFlavor)
            .set("TK", token)
            .set("MO", model)
            .set("OS", osVersion)
            .set("DL", deviceLanguage)
            .set("PN", new GeoJsonPoint(coordinate[0], coordinate[1]))
            .addToSet("CH", coordinate)
            .set("IP", ipAddress)
            .addToSet("IH", ipAddress)
            .set("SB", sinceBeginning));

        if (StringUtils.isNotBlank(cityName)) {
            update.set("CT", cityName);
        }

        return mongoTemplate.updateFirst(
            query(where("id").is(new ObjectId(id)).and("DID").is(did)),
            update,
            RegisteredDeviceEntity.class,
            TABLE
        ).getModifiedCount() > 0;
    }

    @Override
    public List<RegisteredDeviceEntity> findAll(String qid, String did) {
        Query query;
        if (StringUtils.isBlank(qid)) {
            query = query(where("DID").is(did));
        } else {
            /* Apply condition only if field exist. Solved using orOperator. */
            query = query(
                where("DID").is(did)
                    .orOperator(
                        where("QID").exists(false),
                        where("QID").is(qid)
                    ).andOperator(
                    isActive(),
                    isNotDeleted()
                )
            );
        }
        return mongoTemplate.find(
            query,
            RegisteredDeviceEntity.class,
            TABLE);
    }

    @Override
    public RegisteredDeviceEntity findFCMToken(String qid, String did) {
        Query query;
        if (StringUtils.isBlank(qid)) {
            query = query(where("DID").is(did));
        } else {
            query = query(where("QID").is(qid).and("DID").is(did));
        }

        RegisteredDeviceEntity registeredDevice = mongoTemplate.findOne(
            query,
            RegisteredDeviceEntity.class,
            TABLE
        );

        if (registeredDevice == null) {
            LOG.warn("Device not registered qid={} did={}", qid, did);
            return null;
        }

        return registeredDevice;
    }

    @Override
    public List<RegisteredDeviceEntity> findAll(String qid) {
        return mongoTemplate.find(
            query(where("QID").is(qid)
                .andOperator(
                    isActive(),
                    isNotDeleted()
                )
            ),
            RegisteredDeviceEntity.class,
            TABLE
        );
    }

    /**
     * Returns old document with old date when last accessed. And updates with new date
     *
     * @param qid
     * @param did
     * @param token
     * @return
     */
    @Override
    public RegisteredDeviceEntity lastAccessed(String qid, String did, String token, String model, String osVersion, String appVersion, String ipAddress, String deviceLanguage, String cityName) {
        Update update = update("U", "ON".equals(deviceLastAccessedNow) ? new Date() : DateTime.now().minusYears(1).toDate())
            .set("TK", token)
            .set("MO", model)
            .set("OS", osVersion)
            .set("AV", appVersion)
            .set("IP", ipAddress)
            .set("DL", deviceLanguage)
            .addToSet("IH", ipAddress);

        if (StringUtils.isNotBlank(cityName)) {
            update.set("CT", cityName);
        }

        return lastAccessed(qid, did, update);
    }

    private RegisteredDeviceEntity lastAccessed(String qid, String did, Update update) {
        Query query;
        if (StringUtils.isBlank(qid)) {
            query = query(where("DID").is(did));
        } else {
            /* Apply condition only if field exist. Solved using orOperator. */
            query = query(
                where("DID").is(did)
                    .orOperator(
                        where("QID").exists(false),
                        where("QID").is(qid)
                    ));
        }

        return mongoTemplate.findAndModify(
            query,
            update,
            RegisteredDeviceEntity.class,
            TABLE
        );
    }

    @Override
    public boolean resetRegisteredDeviceWithNewDetails(
        String did,
        String qid,
        DeviceTypeEnum deviceType,
        AppFlavorEnum appFlavor,
        String token,
        String model,
        String osVersion,
        String deviceLanguage,
        String cityName,
        double[] coordinate,
        String ipAddress
    ) {
        Update update;
        if (StringUtils.isBlank(qid)) {
            update = update("U", DateTime.now().minusYears(100).toDate())
                .unset("QID")
                .set("DT", deviceType)
                .set("TK", token)
                .set("MO", model)
                .set("OS", osVersion)
                .set("DL", deviceLanguage)
                .set("AF", appFlavor)
                .set("PN", new GeoJsonPoint(coordinate[0], coordinate[1]))
                .addToSet("CH", coordinate)
                .set("IP", ipAddress)
                .addToSet("IH", ipAddress);
        } else {
            update = update("U", DateTime.now().minusYears(100).toDate())
                .set("QID", qid)
                .set("DT", deviceType)
                .set("TK", token)
                .set("MO", model)
                .set("OS", osVersion)
                .set("DL", deviceLanguage)
                .set("AF", appFlavor)
                .set("PN", new GeoJsonPoint(coordinate[0], coordinate[1]))
                .addToSet("CH", coordinate)
                .set("IP", ipAddress)
                .addToSet("IH", ipAddress);
        }

        if (StringUtils.isNotBlank(cityName)) {
            update.set("CT", cityName);
        }

        return mongoTemplate.updateFirst(
            query(where("DID").is(did)),
            update,
            RegisteredDeviceEntity.class,
            TABLE
        ).getModifiedCount() > 0;
    }

    @Override
    public void markFetchedSinceBeginningForDevice(String id) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(update("SB", false)),
            RegisteredDeviceEntity.class,
            TABLE);
    }

    @Override
    public void unsetQidForDevice(String id) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(new Update().unset("QID")),
            RegisteredDeviceEntity.class,
            TABLE);
    }

    @Override
    public long countRegisteredBetweenDates(Date from, Date to, DeviceTypeEnum deviceType) {
        Query query = new Query();
        if (null == deviceType) {
            query.addCriteria(where("C").gte(from).lt(to));
        } else {
            query.addCriteria(where("DT").is(deviceType)).addCriteria(where("C").gte(from).lt(to));
        }

        return mongoTemplate.count(
            query,
            RegisteredDeviceEntity.class,
            TABLE
        );
    }

    @Override
    public long countRegisteredBetweenDates(Date from, Date to, DeviceTypeEnum deviceType, AppFlavorEnum appFlavor) {
        Query query = new Query();
        if (null == deviceType) {
            query.addCriteria(where("C").gte(from).lt(to));
        } else {
            query.addCriteria(where("DT").is(deviceType).and("AF").is(appFlavor)).addCriteria(where("C").gte(from).lt(to));
        }

        return mongoTemplate.count(
            query,
            RegisteredDeviceEntity.class,
            TABLE
        );
    }

    @Override
    public RegisteredDeviceEntity findRecentDevice(String qid) {
        return mongoTemplate.findOne(
            query(where("QID").is(qid).and("DT").exists(true)).with(Sort.by(Sort.Direction.DESC, "U")),
            RegisteredDeviceEntity.class,
            TABLE
        );
    }

    @Override
    public Stream<RegisteredDeviceEntity> findAllTokenWithoutQID(AppFlavorEnum appFlavor) {
        Query query;
        if (null == appFlavor) {
            query = query(where("QID").exists(false).and("TK").ne("BLACKLISTED"));
        } else {
            query = query(where("QID").exists(false).and("TK").ne("BLACKLISTED").and("AF").is(appFlavor));
        }

        return mongoTemplate.find(
            query,
            RegisteredDeviceEntity.class,
            TABLE
        ).stream();
    }

    @Override
    public RegisteredDeviceEntity findByDid(String deviceId) {
        return mongoTemplate.findOne(
            query(where("DID").is(deviceId)),
            RegisteredDeviceEntity.class,
            TABLE
        );
    }

    @Override
    public void updateRegisteredDevice(String did, String qid, DeviceTypeEnum deviceType, boolean sinceBeginning) {
        mongoTemplate.updateFirst(
            query(where("DID").is(did)),
            entityUpdate(update("QID", qid).set("DT", deviceType).set("SB", sinceBeginning)),
            RegisteredDeviceEntity.class,
            TABLE
        );
    }

    @Override
    public List<RegisteredDeviceEntity> findAlmostObsoleteDevices() {
        TypedAggregation<RegisteredDeviceEntity> agg = newAggregation(
            RegisteredDeviceEntity.class,
            match(where("OS").in(UNSUPPORTED_DEVICE_IN_FUTURE).and("U").gte(DateUtil.inLastOneHour())),
            /*
             * Grouping on token makes it distinct and prevents sending multiple message on different device id's as device ids are generated by server and tokens are unique,
             * whereas grouping on id would just populate with mongo _id.
             */
            group("token")
                .first("deviceId").as("DID")
                .first("queueUserId").as("QID")
                .first("token").as("TK")
                .first("osVersion").as("OS")
                .first("deviceType").as("DT")
                .first("created").as("C")
                .first("updated").as("U"));

        return mongoTemplate.aggregate(agg, TABLE, RegisteredDeviceEntity.class).getMappedResults();
    }

    @Override
    public Stream<GeoResult<RegisteredDeviceEntity>> findDevicesWithinVicinity(double[] coordinate, double distanceToPropagateInformation) {
        Date date = Date.from(Instant.now().minus(Duration.ofDays(90)));
        Point location = new Point(coordinate[0], coordinate[1]);
        NearQuery query = NearQuery.near(location).maxDistance(new Distance(distanceToPropagateInformation, Metrics.KILOMETERS))
            .query(query(where("QID").exists(true).and("U").gte(date)));
        return mongoTemplate.geoNear(query, RegisteredDeviceEntity.class, TABLE).getContent().stream();
    }

    @Override
    public Stream<GeoResult<RegisteredDeviceEntity>> findInProximity(GeoJsonPoint point, double distanceInKiloMeters, int limitedToDays) {
        Date date = Date.from(Instant.now().minus(Duration.ofDays(limitedToDays)));
        NearQuery query = NearQuery.near(point).maxDistance(new Distance(distanceInKiloMeters, Metrics.KILOMETERS))
            .query(
                query(where("QID").exists(true).and("U").gte(date))
                    .with(Sort.by(Sort.Direction.DESC, "U")));
        return mongoTemplate.geoNear(query, RegisteredDeviceEntity.class, TABLE).getContent().stream();
    }
}
