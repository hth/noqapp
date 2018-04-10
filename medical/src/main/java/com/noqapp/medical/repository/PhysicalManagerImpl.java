package com.noqapp.medical.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.PhysicalEntity;
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
 * 4/5/18 12:48 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class PhysicalManagerImpl implements PhysicalManager {
    private static final Logger LOG = LoggerFactory.getLogger(PhysicalManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            PhysicalEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PhysicalManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PhysicalEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(PhysicalEntity object) {

    }

    @Override
    public List<PhysicalEntity> findAll() {
        return mongoTemplate.findAll(PhysicalEntity.class, TABLE);
    }

    @Override
    public long totalNumberOfRecords() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public boolean existsName(String name) {
        return mongoTemplate.exists(
                Query.query(where("NA").regex("^" + name + "$", "i")),
                PhysicalEntity.class,
                TABLE
        );
    }
}
