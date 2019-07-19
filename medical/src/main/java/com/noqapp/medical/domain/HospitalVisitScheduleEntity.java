package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * User: hitender
 * Date: 2019-07-19 10:38
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "HOSPITAL_VISIT_SCHEDULE")
@CompoundIndexes(value = {
    @CompoundIndex(name = "hospital_visit_schedule_idx", def = "{'QID' : 1}", unique = false),
})
public class HospitalVisitScheduleEntity extends BaseEntity {

    @Field("QID")
    private String queueUserId;

    @Field("VN")
    private String visitName;

    @Field("VH")
    private String header;

    @Field("VD")
    private Date visitedDate;

    @Field("ED")
    private Date expectedDate;

    @Field("PQ")
    private String performedByQid;

    public String getQueueUserId() {
        return queueUserId;
    }

    public HospitalVisitScheduleEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getVisitName() {
        return visitName;
    }

    public HospitalVisitScheduleEntity setVisitName(String visitName) {
        this.visitName = visitName;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public HospitalVisitScheduleEntity setHeader(String header) {
        this.header = header;
        return this;
    }

    public Date getVisitedDate() {
        return visitedDate;
    }

    public HospitalVisitScheduleEntity setVisitedDate(Date visitedDate) {
        this.visitedDate = visitedDate;
        return this;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public HospitalVisitScheduleEntity setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
        return this;
    }

    public String getPerformedByQid() {
        return performedByQid;
    }

    public HospitalVisitScheduleEntity setPerformedByQid(String performedByQid) {
        this.performedByQid = performedByQid;
        return this;
    }
}
