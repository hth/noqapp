package com.token.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.RegisteredDeviceEntity;

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

    }

    @Override
    public RegisteredDeviceEntity find(String did, String token) {
        return mongoTemplate.findOne(
                query(where("DID").is(did).and("TK").is(token)),
                RegisteredDeviceEntity.class,
                TABLE);
    }
}
