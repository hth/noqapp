package com.noqapp.repository.analytic;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.analytic.SignupByMerchantEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 6/20/18 7:20 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class SignupByMerchantManagerImpl implements SignupByMerchantManager {
    private static final Logger LOG = LoggerFactory.getLogger(SignupByMerchantManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            SignupByMerchantEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public SignupByMerchantManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(SignupByMerchantEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(SignupByMerchantEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
