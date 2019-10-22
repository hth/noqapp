package com.noqapp.repository;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.QuestionnaireEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 10/20/19 2:35 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class QuestionnaireManagerImpl implements QuestionnaireManager {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionnaireManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        QuestionnaireEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public QuestionnaireManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(QuestionnaireEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(QuestionnaireEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public List<QuestionnaireEntity> findAll(String bizNameId) {
        return mongoTemplate.find(
            query(where("BN").is(bizNameId)).with(new Sort(DESC, "C")),
            QuestionnaireEntity.class,
            TABLE
        );
    }

    @Override
    public QuestionnaireEntity findLatest(String bizNameId) {
        return mongoTemplate.findOne(
            query(where("BN").is(bizNameId)).with(new Sort(DESC, "C")),
            QuestionnaireEntity.class,
            TABLE
        );
    }
}
