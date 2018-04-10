package com.noqapp.medical.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.PathologyEntity;
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
 * 4/7/18 10:22 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class PathologyManagerImpl implements PathologyManager {
    private static final Logger LOG = LoggerFactory.getLogger(PathologyManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            PathologyEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PathologyManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PathologyEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(PathologyEntity object) {

    }

    @Override
    public List<PathologyEntity> findAll() {
        return mongoTemplate.findAll(PathologyEntity.class, TABLE);
    }

    @Override
    public long totalNumberOfRecords() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public boolean existsName(String name) {
        return mongoTemplate.exists(
                Query.query(where("NA").regex("^" + name + "$", "i")),
                PathologyEntity.class,
                TABLE
        );
    }
}
