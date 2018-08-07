package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.types.PurchaseOrderStateEnum;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.result.UpdateResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * hitender
 * 3/29/18 2:31 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class PurchaseOrderManagerImpl implements PurchaseOrderManager {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            PurchaseOrderEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public PurchaseOrderManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(PurchaseOrderEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(PurchaseOrderEntity object) {

    }

    @Override
    public List<PurchaseOrderEntity> findAllOpenOrder(String qid) {
        return mongoTemplate.find(
                query(where("QID").is(qid).and("PS").ne(PurchaseOrderStateEnum.OD)),
                PurchaseOrderEntity.class,
                TABLE
        );
    }

    @Override
    public List<PurchaseOrderEntity> findAllOpenOrderByCodeQR(String codeQR) {
        return mongoTemplate.find(
                query(where("QR").is(codeQR).and("PS").ne(PurchaseOrderStateEnum.OD)).with(new Sort(DESC, "C")),
                PurchaseOrderEntity.class,
                TABLE
        );
    }

    @Override
    public PurchaseOrderEntity findOne(String codeQR, int tokenNumber) {
        return mongoTemplate.findOne(
            query(where("QR").is(codeQR).and("TN").is(tokenNumber)),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public long countAllPlacedOrder(String codeQR) {
        return mongoTemplate.count(
            query(where("QR").is(codeQR).and("PS").is(PurchaseOrderStateEnum.PO)).with(new Sort(DESC, "C")),
            PurchaseOrderEntity.class,
            TABLE
        );
    }

    @Override
    public PurchaseOrderEntity getNext(String codeQR, String goTo, String sid) {
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setReadPreference(ReadPreference.primaryPreferred());
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }

        PurchaseOrderEntity queue = mongoTemplate.findOne(
            query(where("QR").is(codeQR)
                .orOperator(
                    where("PS").is(PurchaseOrderStateEnum.PO).and("SN").exists(false),
                    /*
                     * Second or condition will get you any of the skipped
                     * clients by the same server device id.
                     */
                    where("PS").is(PurchaseOrderStateEnum.PO).and("SE").exists(false).and("SID").is(sid)
                )
            ).with(new Sort(ASC, "TN")),
            PurchaseOrderEntity.class,
            TABLE);

        if (updateWhenNextInQueueAcquired(codeQR, goTo, sid, queue)) {
            return getNext(codeQR, goTo, sid);
        }

        return queue;
    }

    @Override
    public PurchaseOrderEntity getThisAsNext(String codeQR, String goTo, String sid, int tokenNumber) {
        if (mongoTemplate.getMongoDbFactory().getLegacyDb().getMongo().getAllAddress().size() > 2) {
            mongoTemplate.setReadPreference(ReadPreference.primaryPreferred());
            mongoTemplate.setWriteConcern(WriteConcern.W3);
        }

        PurchaseOrderEntity purchaseOrder = mongoTemplate.findOne(
            query(where("QR").is(codeQR).and("TN").is(tokenNumber)
                .orOperator(
                    where("PS").is(PurchaseOrderStateEnum.PO).and("SN").exists(false),
                    /*
                     * Second or condition will get you any of the skipped
                     * clients by the same server device id.
                     */
                    where("PS").is(PurchaseOrderStateEnum.PO).and("SE").exists(false).and("SID").is(sid)
                )
            ),
            PurchaseOrderEntity.class,
            TABLE);

        if (updateWhenNextInQueueAcquired(codeQR, goTo, sid, purchaseOrder)) {
            /*
             * Since could not get the specific token, going back to regular
             * cycle to acquire one in the descending order.
             */
            return getNext(codeQR, goTo, sid);
        }

        return purchaseOrder;
    }

    private boolean updateWhenNextInQueueAcquired(String codeQR, String goTo, String sid, PurchaseOrderEntity purchaseOrder) {
        if (null != purchaseOrder) {
            /* Mark as being served. */
            UpdateResult updateResult = mongoTemplate.updateFirst(
                /* Removed additional where clause as we just did it and found one. */
                query(where("id").is(purchaseOrder.getId()).and("PS").is(PurchaseOrderStateEnum.PO)),
                entityUpdate(
                    update("SN", goTo)
                        .set("SID", sid)
                        .set("PS", PurchaseOrderStateEnum.OP)
                        .push("OS", PurchaseOrderStateEnum.OP)
                        .set("SB", new Date())),
                PurchaseOrderEntity.class,
                TABLE
            );

            LOG.info("Next to serve modified={} matched={} queue={}",
                updateResult.getModifiedCount(),
                updateResult.getMatchedCount(),
                purchaseOrder);

            if (0 == updateResult.getMatchedCount()) {
                LOG.info("Could not lock since its already modified codeQR={} token={}, going to next in queue",
                    codeQR, purchaseOrder.getTokenNumber());

                return true;
            }
        }
        return false;
    }
}
