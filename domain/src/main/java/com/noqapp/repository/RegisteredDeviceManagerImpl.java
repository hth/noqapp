package com.noqapp.repository;

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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.RegisteredDeviceEntity;

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

    @Value ("${device.lastAccessed.now:OFF}")
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
    public RegisteredDeviceEntity find(String rid, String did) {
        Query query;
        if (StringUtils.isBlank(rid)) {
            query = query(where("DID").is(did));
        } else {
            query = query(where("RID").is(rid).and("DID").is(did));
        }
        return mongoTemplate.findOne(
                query,
                RegisteredDeviceEntity.class,
                TABLE);
    }

    @Override
    public String findFCMToken(String rid, String did) {
        Query query;
        if (StringUtils.isBlank(rid)) {
            query = query(where("DID").is(did));
        } else {
            query = query(where("RID").is(rid).and("DID").is(did));
        }
        query.fields().include("TK");
        RegisteredDeviceEntity registeredDevice = mongoTemplate.findOne(
                query,
                RegisteredDeviceEntity.class,
                TABLE
        );

        if (registeredDevice == null) {
            LOG.warn("Device not registered rid={} did={}", rid, did);
            return null;
        }

        return registeredDevice.getToken();
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
        return  lastAccessed(
                rid,
                did,
                update("U", "ON".equals(deviceLastAccessedNow) ? new Date() : DateTime.now().minusYears(1).toDate()));
    }

    @Override
    public RegisteredDeviceEntity lastAccessed(String rid, String did, String token) {
        return lastAccessed(
                rid,
                did,
                update("U", "ON".equals(deviceLastAccessedNow) ? new Date() : DateTime.now().minusYears(1).toDate()).set("TK", token));
    }

    private RegisteredDeviceEntity lastAccessed(String rid, String did, Update update) {
        Query query;
        if (StringUtils.isBlank(rid)) {
            query = query(where("DID").is(did));
        } else {
            query = query(where("RID").is(rid).and("DID").is(did));
        }

        return mongoTemplate.findAndModify(
                query,
                update,
                RegisteredDeviceEntity.class,
                TABLE
        );
    }
}
