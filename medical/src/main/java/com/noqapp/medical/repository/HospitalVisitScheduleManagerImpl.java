package com.noqapp.medical.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.BooleanReplacementEnum;
import com.noqapp.domain.types.medical.HospitalVisitForEnum;
import com.noqapp.medical.domain.HospitalVisitScheduleEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 2019-07-19 13:15
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class HospitalVisitScheduleManagerImpl implements HospitalVisitScheduleManager {
    private static final Logger LOG = LoggerFactory.getLogger(HospitalVisitScheduleManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        HospitalVisitScheduleEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public HospitalVisitScheduleManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(HospitalVisitScheduleEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public List<HospitalVisitScheduleEntity> findAll(String qid) {
        return mongoTemplate.find(
            query(where("QID").is(qid)).with(Sort.by(Sort.Direction.ASC, "ED")),
            HospitalVisitScheduleEntity.class,
            TABLE
        );
    }

    @Override
    public List<HospitalVisitScheduleEntity> findAll(String qid, HospitalVisitForEnum hospitalVisitFor) {
        return mongoTemplate.find(
            query(where("QID").is(qid).and("HV").is(hospitalVisitFor)).with(Sort.by(Sort.Direction.ASC, "ED")),
            HospitalVisitScheduleEntity.class,
            TABLE
        );
    }

    @Override
    public HospitalVisitScheduleEntity markAsVisited(String id, String qid, String performedByQid) {
        return mongoTemplate.findAndModify(
            query(where("id").is(id).and("QID").is(qid)),
            entityUpdate(update("VD", new Date()).set("PQ", performedByQid)),
            FindAndModifyOptions.options().returnNew(true),
            HospitalVisitScheduleEntity.class,
            TABLE
        );
    }

    @Override
    public HospitalVisitScheduleEntity removeVisit(String id, String qid) {
        return mongoTemplate.findAndModify(
            query(where("id").is(id).and("QID").is(qid)),
            entityUpdate(new Update().unset("VD").unset("PQ")),
            FindAndModifyOptions.options().returnNew(true),
            HospitalVisitScheduleEntity.class,
            TABLE
        );
    }

    public HospitalVisitScheduleEntity modifyVisitingFor(String id, String qid, String visitingFor, BooleanReplacementEnum booleanReplacement, String performedByQid) {
        return mongoTemplate.findAndModify(
            query(where("id").is(id).and("QID").is(qid).and("VF." + visitingFor).exists(true)),
            entityUpdate(update("VF." + visitingFor, booleanReplacement).set("VD", new Date()).set("PQ", performedByQid)),
            FindAndModifyOptions.options().returnNew(true),
            HospitalVisitScheduleEntity.class,
            TABLE
        );
    }

    @Override
    public Stream<HospitalVisitScheduleEntity> notifyAllUpComingHospitalVisit() {
        return mongoTemplate.find(
            query(where("ED").lte(DateUtil.plusDays(7)).and("NC").is(0)),
            HospitalVisitScheduleEntity.class,
            TABLE
        ).stream();
    }

    @Override
    public void increaseNotificationCount(String id) {
        mongoTemplate.updateFirst(
            query(where("id").is(id)),
            entityUpdate(new Update().inc("NC", 1)),
            HospitalVisitScheduleEntity.class,
            TABLE
        );
    }

    @Override
    public void deleteHard(HospitalVisitScheduleEntity object) {

    }
}
