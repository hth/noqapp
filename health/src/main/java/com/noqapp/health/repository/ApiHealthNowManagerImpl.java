package com.noqapp.health.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.health.domain.ApiHealthNowEntity;
import com.noqapp.repository.UserProfileManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 11/07/17 10:13 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class ApiHealthNowManagerImpl implements ApiHealthNowManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ApiHealthNowEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ApiHealthNowManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ApiHealthNowEntity object) {
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(ApiHealthNowEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
