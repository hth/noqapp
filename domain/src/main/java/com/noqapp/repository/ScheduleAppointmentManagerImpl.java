package com.noqapp.repository;

import static com.noqapp.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.QueueJoinDeniedEnum;

import com.mongodb.client.result.UpdateResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.context.InvalidPersistentPropertyPath;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    public List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, String scheduleDate) {
        LOG.info("ScheduleDate={} codeQR={}", scheduleDate, codeQR);
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("SD").is(scheduleDate).and("AS").in(AppointmentStatusEnum.A, AppointmentStatusEnum.U)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public List<ScheduleAppointmentEntity> findBookedWalkinAppointmentsForDay(String codeQR, String scheduleDate) {
        LOG.info("ScheduleDate={} codeQR={}", scheduleDate, codeQR);
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("SD").is(scheduleDate)
                .andOperator(
                    where("AS").in(AppointmentStatusEnum.A, AppointmentStatusEnum.U),
                    where("AS").ne(AppointmentStatusEnum.W)
                )
            ).with(Sort.by(ASC, "ST")),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public List<ScheduleAppointmentEntity> findBookedFlexAppointmentsForDay(String codeQR, String scheduleDate, int startTime) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("PS").is(AppointmentStateEnum.F).and("ST").lte(startTime).and("SD").is(scheduleDate)
                .andOperator(
                    where("AS").in(AppointmentStatusEnum.A, AppointmentStatusEnum.U),
                    where("AS").ne(AppointmentStatusEnum.W)
                )
            ).with(Sort.by(ASC, "ST")),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public long countBookedFlexAppointmentsForDay(String codeQR, String scheduleDate, int startTime) {
        return mongoTemplate.count(
            query(where("QR").is(codeQR).and("PS").is(AppointmentStateEnum.F).and("ST").lte(startTime).and("SD").is(scheduleDate)
                .andOperator(
                    where("AS").in(AppointmentStatusEnum.A, AppointmentStatusEnum.U),
                    where("AS").ne(AppointmentStatusEnum.W)
                )
            ).with(Sort.by(ASC, "ST")),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public List<ScheduleAppointmentEntity> findScheduleForDay(String codeQR, String scheduleDate) {
        LOG.info("ScheduleDate={} codeQR={}", scheduleDate, codeQR);
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("SD").is(scheduleDate).and("AS").ne(AppointmentStatusEnum.C)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    /**
     * Counts number of records in each schedule. Sums the count in field TA.
     */
    @Override
    public List<ScheduleAppointmentEntity> findBookedAppointmentsForMonth(String codeQR, String startOfMonth, String endOfMonth) {
        LOG.info("codeQR={} {} {}", codeQR, startOfMonth, endOfMonth);
        try {
            TypedAggregation<ScheduleAppointmentEntity> agg = newAggregation(ScheduleAppointmentEntity.class,
                match(where("QR").is(codeQR).and("SD").gte(startOfMonth).lte(endOfMonth).and("AS").ne(AppointmentStatusEnum.C)),
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
    public boolean cancelAppointment(String id, String qid, String codeQR) {
        UpdateResult updateResult = mongoTemplate.updateFirst(
            query(where("id").is(id).and("QID").is(qid).and("QR").is(codeQR)),
            entityUpdate(update("AS", AppointmentStatusEnum.C)),
            ScheduleAppointmentEntity.class,
            TABLE
        );

        return updateResult.wasAcknowledged();
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
    public boolean doesAppointmentExists(String qid, String codeQR, String scheduleDate) {
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
    public List<ScheduleAppointmentEntity> findAllPastAppointments(String qid, int untilDaysInPast) {
        return mongoTemplate.find(
            query(where("QID").is(qid).and("SD").lte(DateUtil.dateToString(DateUtil.nowMidnightDate())).gte(DateUtil.dateToString(DateUtil.minusDays(untilDaysInPast)))),
            ScheduleAppointmentEntity.class,
            TABLE);
    }

    @Override
    public List<ScheduleAppointmentEntity> findAllUpComingAppointments(String qid, int untilDaysInFuture) {
        Query query;
        if (untilDaysInFuture > 0) {
            query = query(where("QID").is(qid)
                .and("AS").nin(AppointmentStatusEnum.C, AppointmentStatusEnum.R, AppointmentStatusEnum.S, AppointmentStatusEnum.W)
                .and("SD").gte(DateUtil.dateToString(DateUtil.nowMidnightDate())).lte(DateUtil.dateToString(DateUtil.plusDays(untilDaysInFuture))));
        } else {
            query = query(where("QID").is(qid)
                .and("AS").nin(AppointmentStatusEnum.C, AppointmentStatusEnum.R, AppointmentStatusEnum.S, AppointmentStatusEnum.W)
                .and("SD").gte(DateUtil.dateToString(DateUtil.nowMidnightDate())));
        }
        return mongoTemplate.find(query, ScheduleAppointmentEntity.class, TABLE);
    }

    @Override
    public List<ScheduleAppointmentEntity> findAllUpComingAppointments(String qid) {
        return findAllUpComingAppointments(qid, 0);
    }

    @Override
    public ScheduleAppointmentEntity findAppointment(String id, String qid, String codeQR) {
        return mongoTemplate.findOne(
            query(where("id").is(id).and("QID").is(qid).and("QR").is(codeQR)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public long countNumberOfAppointments(String codeQR, String day) {
        return mongoTemplate.count(
            query(where("QR").is(codeQR).and("SD").is(day).and("AS").in(AppointmentStatusEnum.A, AppointmentStatusEnum.U)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public Stream<ScheduleAppointmentEntity> findAllUpComingAppointmentsByBizStore(String codeQR, String day) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("SD").gte(day).and("AS").in(AppointmentStatusEnum.A, AppointmentStatusEnum.U)),
            ScheduleAppointmentEntity.class,
            TABLE
        ).stream();
    }

    @Override
    public void changeAppointmentStatusOnTokenIssued(String id) {
        mongoTemplate.updateFirst(query(where("id").is(id)), update("AS",  AppointmentStatusEnum.W), ScheduleAppointmentEntity.class, TABLE);
    }

    @Override
    public void changeAppointmentStatusOnTokenNotIssued(String id, QueueJoinDeniedEnum queueJoinDenied) {
        mongoTemplate.updateFirst(query(where("id").is(id)), update("AS", AppointmentStatusEnum.R).set("QJD", queueJoinDenied), ScheduleAppointmentEntity.class, TABLE);
    }
}
