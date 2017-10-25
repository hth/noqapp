package com.noqapp.repository;

import com.mongodb.WriteResult;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.ForgotRecoverEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static com.noqapp.repository.util.AppendAdditionalFields.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * User: hitender
 * Date: 5/3/17 12:43 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class ForgotRecoverManagerImpl implements ForgotRecoverManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ForgotRecoverEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ForgotRecoverManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ForgotRecoverEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void invalidateAllEntries(String queueUserId) {
        mongoTemplate.updateMulti(
                query(where("QID").is(queueUserId)),
                entityUpdate(update("A", false)),
                ForgotRecoverEntity.class);
    }

    @Override
    public ForgotRecoverEntity findByAuthenticationKey(String key) {
        return mongoTemplate.findOne(
                query(where("AUTH").is(key)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ForgotRecoverEntity.class,
                TABLE);
    }

    @Override
    public int markInActiveAllOlderThanThreeHours() {
        Date date = Date.from(Instant.now().minus(Duration.ofHours(3)));
        WriteResult writeResult = mongoTemplate.updateMulti(
                query(where("C").lte(date)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                entityUpdate(update("A", false)),
                ForgotRecoverEntity.class,
                TABLE);

        return writeResult.getN();
    }

    @Override
    public void deleteHard(ForgotRecoverEntity object) {
        mongoTemplate.remove(object);
    }
}
