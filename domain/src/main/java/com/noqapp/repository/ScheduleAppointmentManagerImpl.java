package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.types.AppointmentStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.context.InvalidPersistentPropertyPath;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-22 16:21
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class ScheduleAppointmentManagerImpl implements ScheduleAppointmentManager {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleAppointmentManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
        ScheduleAppointmentEntity.class,
        Document.class,
        "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ScheduleAppointmentManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ScheduleAppointmentEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(ScheduleAppointmentEntity object) {

    }

    @Override
    public List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, Date scheduleDate) {
        LOG.info("ScheduleDate={} codeQR={}", scheduleDate, codeQR);
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("SD").is(scheduleDate)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    /**
     * Counts number of records in each schedule. Sums the count in field TA.
     */
    @Override
    public List<ScheduleAppointmentEntity> findBookedAppointmentsForMonth(String codeQR, Date startOfMonth, Date endOfMonth) {
        LOG.info("codeQR={} {} {}", codeQR, startOfMonth, endOfMonth);
        try {
            TypedAggregation<ScheduleAppointmentEntity> agg = newAggregation(ScheduleAppointmentEntity.class,
                match(where("QR").is(codeQR).and("SD").gte(startOfMonth).lte(endOfMonth)),
                group("scheduleDate")
                    .first("scheduleDate").as("SD")
                    .count().as("TA")
            );
            /* Above totalAppointments in group is used as a place holder to count the number of records that has TA > 0. */
            List<ScheduleAppointmentEntity> scheduleAppointments = mongoTemplate.aggregate(agg, TABLE, ScheduleAppointmentEntity.class).getMappedResults();
            if (scheduleAppointments.size() > 0) {
                LOG.info("Computing appointments for each scheduleDate {}", scheduleAppointments.get(0));
                return scheduleAppointments;
            }

            return new ArrayList<>();
        } catch (InvalidPersistentPropertyPath e) {
            LOG.error("Failed finding appointments for codeQR={} reason={}", codeQR, e.getLocalizedMessage(), e);
            return null;
        }
    }

    @Override
    public void cancelAppointment(String id, String qid, String codeQR) {
        mongoTemplate.updateFirst(
            query(where("id").is(id).and("QID").is(qid).and("QR").is(codeQR)),
            entityUpdate(update("AS", AppointmentStatusEnum.C)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public ScheduleAppointmentEntity updateSchedule(String id, AppointmentStatusEnum appointmentStatus, String qid, String codeQR) {
        return mongoTemplate.findAndModify(
            query(where("id").is(id).and("QR").is(codeQR).and("QID").is(qid)),
            entityUpdate(update("AS", appointmentStatus)),
            FindAndModifyOptions.options().returnNew(true),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public boolean doesAppointmentExists(String qid, String codeQR, Date scheduleDate) {
        return mongoTemplate.exists(
            query(where("QID").is(qid)
                .and("QR").is(codeQR)
                .and("SD").is(scheduleDate)
                .and("AS").nin(AppointmentStatusEnum.C, AppointmentStatusEnum.R, AppointmentStatusEnum.S)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public List<ScheduleAppointmentEntity> findAllUpComingAppointments(String qid, int untilDaysInFuture) {
        Query query;
        if (untilDaysInFuture > 0) {
            query = query(where("QID").is(qid)
                .and("AS").nin(AppointmentStatusEnum.C, AppointmentStatusEnum.R, AppointmentStatusEnum.S)
                .and("SD").gte(DateUtil.nowMidnightDate()).lte(DateUtil.plusDays(untilDaysInFuture)));
        } else {
            query = query(where("QID").is(qid)
                .and("AS").nin(AppointmentStatusEnum.C, AppointmentStatusEnum.R, AppointmentStatusEnum.S)
                .and("SD").gte(DateUtil.nowMidnightDate()));
        }
        return mongoTemplate.find(query, ScheduleAppointmentEntity.class, TABLE);
    }

    @Override
    public List<ScheduleAppointmentEntity> findAllUpComingAppointments(String qid) {
        return findAllUpComingAppointments(qid, 0);
    }

    @Override
    public List<ScheduleAppointmentEntity> findAllPastAppointments(String qid, int untilDaysInPast) {
        return mongoTemplate.find(
            query(where("QID").is(qid).and("SD").lte(DateUtil.nowMidnightDate()).gte(DateUtil.plusDays(untilDaysInPast))),
            ScheduleAppointmentEntity.class,
            TABLE);
    }

    @Override
    public ScheduleAppointmentEntity findAppointment(String id, String qid, String codeQR) {
        return mongoTemplate.findOne(
            query(where("id").is(id).and("QID").is(qid).and("QR").is(codeQR)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }
}
