package com.noqapp.repository;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
}
