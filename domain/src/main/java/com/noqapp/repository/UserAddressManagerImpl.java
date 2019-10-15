package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.UserAddressEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * hitender
 * 5/15/18 10:45 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class UserAddressManagerImpl implements UserAddressManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserAddressManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        UserAddressEntity.class,
        Document.class,
        "collection");

    private int numberOfAddressAllowed;
    private MongoTemplate mongoTemplate;

    @Autowired
    public UserAddressManagerImpl(
        @Value("${UserAddressManagerImpl.numberOfAddressAllowed}")
        int numberOfAddressAllowed,

        MongoTemplate mongoTemplate
    ) {
        this.numberOfAddressAllowed = numberOfAddressAllowed;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserAddressEntity object) {
        while (count(object.getQueueUserId()) >= numberOfAddressAllowed) {
            UserAddressEntity leastUsed = leastUsedAddress(object.getQueueUserId());
            mongoTemplate.remove(leastUsed);
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(UserAddressEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public void deleteAddress(String id, String qid) {
        mongoTemplate.remove(
            query(where("id").is(new ObjectId(id)).and("QID").is(qid)),
            UserAddressEntity.class,
            TABLE
        );
    }

    @Override
    public boolean doesAddressExists(String id, String qid) {
        return mongoTemplate.exists(
            query(where("id").is(new ObjectId(id)).and("QID").is(qid)),
            UserAddressEntity.class,
            TABLE
        );
    }

    @Override
    public List<UserAddressEntity> getAll(String qid) {
        return mongoTemplate.find(
            query(where("QID").is(qid)).with(new Sort(ASC, "LU")),
            UserAddressEntity.class,
            TABLE
        );
    }

    @Override
    public void updateLastUsedAddress(String address, String qid) {
        mongoTemplate.updateFirst(
            query(where("QID").is(qid).and("AD").regex("^" + address, "i")),
            entityUpdate(update("LU", new Date())),
            UserAddressEntity.class,
            TABLE
        );
    }

    private long count(String qid) {
        return mongoTemplate.count(
            query(where("QID").is(qid)),
            UserAddressEntity.class,
            TABLE
        );
    }

    private UserAddressEntity leastUsedAddress(String qid) {
        return mongoTemplate.findOne(
            query(where("QID").is(qid)).with(new Sort(DESC, "LU")),
            UserAddressEntity.class,
            TABLE
        );
    }
}
