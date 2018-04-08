package com.noqapp.repository;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.model.Filters;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.BizNameEntity;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * User: hitender
 * Date: 11/23/16 4:43 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class BizNameManagerImpl implements BizNameManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizNameManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizNameEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizNameManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(BizNameEntity object) {
        if (StringUtils.isNotBlank(object.getBusinessName())) {
            if (null != object.getId()) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } else {
            LOG.error("Cannot save BizName with empty name");
            throw new RuntimeException("Found no name for business");
        }
    }

    @Override
    public BizNameEntity getById(String id) {
        Assert.hasText(id, "Id empty for BizNameEntity");
        return mongoTemplate.findOne(query(where("id").is(new ObjectId(id))), BizNameEntity.class, TABLE);
    }

    @Override
    public BizNameEntity findByPhone(String phone) {
        return mongoTemplate.findOne(query(where("PH").is(phone)), BizNameEntity.class, TABLE);
    }

    @Override
    public void deleteHard(BizNameEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public BizNameEntity noName() {
        return mongoTemplate.findOne(query(where("N").is("")), BizNameEntity.class, TABLE);
    }

    @Override
    public List<BizNameEntity> findAllBizWithMatchingName(String businessName) {
        return mongoTemplate.find(query(where("N").regex("^" + businessName, "i")), BizNameEntity.class, TABLE);
    }

    /**
     * This method is replacement for the method listed in the link below as it reduces a step to
     * list business names as string.
     * <p>
     * TODO Needs to be tested for result and speed
     * <p>
     * {@link #findAllBizWithMatchingName}
     * {@link #findAllDistinctBizStr}
     *
     * @param businessName
     * @return
     */
    public Set<String> findDistinctBizWithMatchingName(String businessName) {
        DistinctIterable<String> distinctIterable = mongoTemplate.getCollection(TABLE).distinct(
                "N",
                Filters.regex("N", "^" + businessName, "i"),
                String.class);

        Set<String> businessNames = new HashSet<>();
        for (String foundName : distinctIterable) {
            businessNames.add(foundName);
        }

        return businessNames;
    }

    @Override
    public Set<String> findAllDistinctBizStr(String businessName) {
        return findAllBizWithMatchingName(businessName).stream().map(BizNameEntity::getBusinessName).collect(Collectors.toSet());
    }

    public List<BizNameEntity> findAll(int skip, int limit) {
        return mongoTemplate.find(
                new Query().skip(skip).limit(limit),
                BizNameEntity.class
        );
    }

    public List<BizNameEntity> findByInviteeCode(String inviteCode) {
        return mongoTemplate.find(query(where("IC").is(inviteCode)).with(new Sort(DESC, "C")), BizNameEntity.class, TABLE);
    }

    @Override
    public BizNameEntity findByCodeQR(String codeQR) {
        return mongoTemplate.findOne(
                query(where("QR").is(codeQR)),
                BizNameEntity.class,
                TABLE
        );
    }

    @Override
    public boolean isValidCodeQR(String codeQR) {
        return mongoTemplate.exists(query(where("QR").is(codeQR)), BizNameEntity.class);
    }

    @Override
    public boolean doesWebLocationExists(String webLocation, String id) {
        Query query;
        if (StringUtils.isBlank(id)) {
            query = query(where("WL").is(webLocation));
        } else {
            query = query(where("WL").is(webLocation).and("_id").ne(id));
        }
        return mongoTemplate.exists(
                query,
                BizNameEntity.class,
                TABLE
        );
    }
}
