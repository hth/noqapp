package com.noqapp.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.types.AppointmentStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.context.InvalidPersistentPropertyPath;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

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
    public List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, String day) {
        return mongoTemplate.find(
            query(where("QR").is(codeQR).and("DY").is(day)),
            ScheduleAppointmentEntity.class,
            TABLE
        );
    }

    @Override
    public List<ScheduleAppointmentEntity> findBookedAppointmentsForMonth(String codeQR, Date startOfMonth, Date endOfMonth) {
        try {
            TypedAggregation<ScheduleAppointmentEntity> agg = newAggregation(ScheduleAppointmentEntity.class,
                match(where("QR").is(codeQR).and("AS").ne(AppointmentStatusEnum.R)),
                group("day")
                    .first("day").as("DY")
                    .sum("totalAppointments").as("TA")
            );
            /* Above totalAppointments in group is used as a place holder to count the number of records that has TA > 0. */
            List<ScheduleAppointmentEntity> scheduleAppointments = mongoTemplate.aggregate(agg, TABLE, ScheduleAppointmentEntity.class).getMappedResults();
            if (scheduleAppointments.size() > 0) {
                LOG.info("Computing appointments for each day {}", scheduleAppointments.get(0));
                return scheduleAppointments;
            }

            return null;
        } catch (InvalidPersistentPropertyPath e) {
            LOG.error("Failed finding appointments for codeQR={} reason={}", codeQR, e.getLocalizedMessage(), e);
            return null;
        }
    }
}
