package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.IncidentEventEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 5/17/21 4:12 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class IncidentEventManagerImpl implements IncidentEventManager {
    private static final Logger LOG = LoggerFactory.getLogger(IncidentEventManagerImpl.class);

    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        IncidentEventEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public IncidentEventManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(IncidentEventEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(IncidentEventEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
