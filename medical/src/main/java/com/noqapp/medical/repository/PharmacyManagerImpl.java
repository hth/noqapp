package com.noqapp.medical.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.PharmacyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * hitender
 * 4/7/18 7:05 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class PharmacyManagerImpl implements PharmacyManager {
    private static final Logger LOG = LoggerFactory.getLogger(PharmacyManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            PharmacyEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PharmacyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PharmacyEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(PharmacyEntity object) {

    }

    @Override
    public List<PharmacyEntity> findAll() {
        return mongoTemplate.findAll(PharmacyEntity.class, TABLE);
    }

    @Override
    public long totalNumberOfRecords() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public boolean existsName(String name) {
        return mongoTemplate.exists(
                Query.query(where("NA").regex("^" + name + "$", "i")),
                PharmacyEntity.class,
                TABLE
        );
    }
}
