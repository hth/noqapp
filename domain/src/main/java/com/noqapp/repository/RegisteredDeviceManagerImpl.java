package com.noqapp.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.RegisteredDeviceEntity;

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
    public RegisteredDeviceEntity find(String did, String token) {
        return mongoTemplate.findOne(
                query(where("DID").is(did).and("TK").is(token)),
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
}
