package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BizCategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * hitender
 * 12/20/17 3:55 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BizCategoryManagerImpl implements BizCategoryManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizCategoryManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizCategoryEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizCategoryManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BizCategoryEntity object) {
        if (StringUtils.isNotBlank(object.getCategoryName()) && StringUtils.isNotBlank(object.getBizNameId())) {
            if (null != object.getId()) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } else {
            LOG.error("Cannot save BizCategory with empty name");
            throw new RuntimeException("Found no name for category");
        }
    }

    @Override
    public List<BizCategoryEntity> getByBizNameId(String bizNameId) {
        return mongoTemplate.find(
                query(where("BN").is(bizNameId)).with(new Sort(ASC, "CN")),
                BizCategoryEntity.class,
                TABLE
        );
    }

    @Override
    public boolean existCategory(String categoryName, String bizNameId) {
        return mongoTemplate.exists(
                query(where("BN").is(bizNameId).and("CN").regex(Pattern.compile("^" + categoryName + "$", CASE_INSENSITIVE))),
                BizCategoryEntity.class,
                TABLE
        );
    }

    @Override
    public BizCategoryEntity findById(String id) {
        return mongoTemplate.findById(id, BizCategoryEntity.class, TABLE);
    }

    @Override
    public void updateBizCategoryName(String bizCategoryId, String categoryName) {
        mongoTemplate.updateFirst(
                query(where("id").is(new ObjectId(bizCategoryId))),
                Update.update("CN", categoryName),
                BizCategoryEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteHard(BizCategoryEntity object) {
        mongoTemplate.remove(object, TABLE);
    }
}
