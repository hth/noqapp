package com.noqapp.medical.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.medical.domain.UserMedicalProfileHistoryEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 2019-01-13 15:55
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class UserMedicalProfileHistoryManagerImpl implements UserMedicalProfileHistoryManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserMedicalProfileHistoryManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        UserMedicalProfileHistoryEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserMedicalProfileHistoryManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserMedicalProfileHistoryEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(UserMedicalProfileHistoryEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
