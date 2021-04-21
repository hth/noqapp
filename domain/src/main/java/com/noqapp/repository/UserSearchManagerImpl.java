package com.noqapp.repository;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.UserSearchEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * hitender
 * 2/13/21 2:34 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class UserSearchManagerImpl implements UserSearchManager{
    private static final Logger LOG = LoggerFactory.getLogger(UserSearchManagerImpl.class);

    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        UserSearchEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public UserSearchManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(UserSearchEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public List<String> lastFewSearches(String qid, int limit) {
        return mongoTemplate.findDistinct(
            query(where("QID").is(qid)).with(Sort.by(DESC, "C")).limit(limit),
            "QY",
            UserSearchEntity.class,
            String.class);
    }

    @Override
    public void deleteHard(UserSearchEntity object) {
        mongoTemplate.remove(object, TABLE);
    }
}
