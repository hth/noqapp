package com.token.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.RegisteredDeviceEntity;
import com.token.domain.types.DeviceTypeEnum;

import java.util.Date;

/**
 * User: hitender
 * Date: 3/1/17 12:27 PM
 */
@SuppressWarnings ({
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

    @Value ("${device.lastAccessed.now:ON}")
    private String deviceLastAccessedNow;

    private MongoTemplate mongoTemplate;

    @Autowired
    public RegisteredDeviceManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(RegisteredDeviceEntity object) {
        mongoTemplate.save(object);
    }

    @Override
    public void deleteHard(RegisteredDeviceEntity object) {

    }

    @Override
    public RegisteredDeviceEntity find(String rid, String did) {
        if (StringUtils.isBlank(rid)) {
            return mongoTemplate.findOne(
                    query(where("DID").is(did)),
                    RegisteredDeviceEntity.class,
                    TABLE);
        }
        return mongoTemplate.findOne(
                query(where("RID").is(rid).and("DID").is(did)),
                RegisteredDeviceEntity.class,
                TABLE);
    }

    @Override
    public RegisteredDeviceEntity registerDevice(String rid, String did, DeviceTypeEnum deviceType, String token) {
        RegisteredDeviceEntity newRegisteredDevice;
        RegisteredDeviceEntity registeredDevice;

        if (StringUtils.isBlank(rid)) {
            newRegisteredDevice = RegisteredDeviceEntity.newInstance(did, deviceType, token);
            registeredDevice = find(null, did);
        } else {
            newRegisteredDevice = RegisteredDeviceEntity.newInstance(rid, did, deviceType, token);
            registeredDevice = find(rid, did);
        }

        if (null == registeredDevice) {
            save(newRegisteredDevice);
            LOG.info("registered device for rid={} did={}", rid, did);
        } else if (StringUtils.isNotBlank(token)) {
            registeredDevice.setDeviceType(deviceType);
            registeredDevice.setToken(token);
            save(newRegisteredDevice);
            LOG.info("updated registered device for rid={} did={} token={}", rid, did, token);
        }
        return newRegisteredDevice;
    }

    /**
     * Returns old document with old date when last accessed. And updates with new date
     *
     * @param rid
     * @param did
     * @return
     */
    @Override
    public RegisteredDeviceEntity lastAccessed(String rid, String did) {
        return lastAccessed(rid, did, update("U", "ON".equals(deviceLastAccessedNow) ? new Date() : DateTime.now().minusYears(1).toDate()));
    }

    @Override
    public RegisteredDeviceEntity lastAccessed(String rid, String did, String token) {
        return lastAccessed(rid, did, update("U", "ON".equals(deviceLastAccessedNow) ? new Date() : DateTime.now().minusYears(1).toDate()).set("TK", token));
    }

    private RegisteredDeviceEntity lastAccessed(String rid, String did, Update update) {
        return mongoTemplate.findAndModify(
                query(where("RID").is(rid).and("DID").is(did)),
                update,
                RegisteredDeviceEntity.class,
                TABLE
        );
    }
}
