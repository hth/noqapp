package com.token.repository;

import static com.token.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteConcern;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.token.domain.BaseEntity;
import com.token.domain.QueueEntity;
import com.token.domain.types.QueueUserStateEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 1/2/17 8:32 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class QueueManagerImpl implements QueueManager {
    private static final Logger LOG = LoggerFactory.getLogger(QueueManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            QueueEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public QueueManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(QueueEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        if (mongoTemplate.getDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void insert(QueueEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.insert(object, TABLE);
    }

    @Override
    public void abort(String id) {
        if (mongoTemplate.getDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }

        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(update("QS", QueueUserStateEnum.A).set("A", false)),
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity findOne(String codeQR, String did, String rid) {
        Query query;
        if (StringUtils.isNotBlank(rid)) {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("RID").is(rid));
        } else {
            query = query(where("QR").is(codeQR).and("DID").is(did));
        }

        return mongoTemplate.findOne(
                query,
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public QueueEntity findToAbort(String codeQR, String did, String rid) {
        Query query;
        if (StringUtils.isNotBlank(rid)) {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("QS").is(QueueUserStateEnum.Q).and("RID").is(rid));
        } else {
            query = query(where("QR").is(codeQR).and("DID").is(did).and("QS").is(QueueUserStateEnum.Q));
        }

        return mongoTemplate.findOne(
                query,
                QueueEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteHard(QueueEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public QueueEntity updateAndGetNextInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState) {
        boolean status = mongoTemplate.updateFirst(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber)),
                entityUpdate(update("QS", queueUserState).set("A", false)),
                QueueEntity.class,
                TABLE).getN() > 1;
        LOG.debug("serving status={} codeQR={} tokenNumber={}", status, codeQR, tokenNumber);
        return getNext(codeQR);
    }

    @Override
    public QueueEntity getNext(String codeQR) {
        return mongoTemplate.findOne(
                query(
                        where("QR").is(codeQR)
                                .and("QS").is(QueueUserStateEnum.Q)
                ).with(new Sort(ASC, "TN")),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllByDid(String did) {
        return mongoTemplate.find(
                query(where("DID").is(did).and("QS").is(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllByRid(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid).and("QS").is(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public boolean isQueued(int tokenNumber, String codeQR) {
        return mongoTemplate.exists(
                query(where("QR").is(codeQR).and("TN").is(tokenNumber).and("QS").is(QueueUserStateEnum.Q)),
                QueueEntity.class,
                TABLE);
    }

    public List<QueueEntity> findAllClientServiced() {
        return mongoTemplate.find(
                query(where("NS").is(false)
                        .orOperator(
                                where("QS").is(QueueUserStateEnum.S),
                                where("QS").is(QueueUserStateEnum.N)
                        )
                ),
                QueueEntity.class,
                TABLE
        );
    }

    public List<QueueEntity> findByCodeQR(String codeQR) {
        return mongoTemplate.find(
                query(where("QR").is(codeQR)),
                QueueEntity.class,
                TABLE
        );
    }

    public int deleteByCodeQR(String codeQR) {
        return mongoTemplate.remove(
                query(where("QR").is(codeQR)),
                QueueEntity.class,
                TABLE
        ).getN();
    }
}
