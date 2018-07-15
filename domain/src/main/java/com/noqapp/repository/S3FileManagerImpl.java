package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.S3FileEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 5/29/18 5:38 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class S3FileManagerImpl implements S3FileManager {
    private static final Logger LOG = LoggerFactory.getLogger(S3FileManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            S3FileEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public S3FileManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(S3FileEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(S3FileEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public List<S3FileEntity> findAllWithLimit() {
        return mongoTemplate.find(
                new Query().limit(100),
                S3FileEntity.class,
                TABLE
        );
    }
}
