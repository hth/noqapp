package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.medical.HospitalVisitForEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    @Field("HV")
    private HospitalVisitForEnum hospitalVisitFor;

    @Field("VF")
    private List<String> visitingFor = new LinkedList<>();

    @Field("VH")
    private String header;

    @Field("ED")
    private Date expectedDate;

    @Field("VD")
    private Date visitedDate;

    @Field("PQ")
    private String performedByQid;

    @Field ("NS")
    private boolean notifyForUpcomingVisit = false;

    public String getQueueUserId() {
        return queueUserId;
    }

    public HospitalVisitScheduleEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public HospitalVisitForEnum getHospitalVisitFor() {
        return hospitalVisitFor;
    }

    public HospitalVisitScheduleEntity setHospitalVisitFor(HospitalVisitForEnum hospitalVisitFor) {
        this.hospitalVisitFor = hospitalVisitFor;
        return this;
    }

    public List<String> getVisitingFor() {
        return visitingFor;
    }

    public HospitalVisitScheduleEntity setVisitingFor(List<String> visitingFor) {
        this.visitingFor = visitingFor;
        return this;
    }

    public HospitalVisitScheduleEntity addVisitingFor(String visitName) {
        this.visitingFor.add(visitName);
        return this;
    }

    public String getHeader() {
        return header;
    }

    public HospitalVisitScheduleEntity setHeader(String header) {
        this.header = header;
        return this;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public HospitalVisitScheduleEntity setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
        return this;
    }

    public Date getVisitedDate() {
        return visitedDate;
    }

    public HospitalVisitScheduleEntity setVisitedDate(Date visitedDate) {
        this.visitedDate = visitedDate;
        return this;
    }

    public String getPerformedByQid() {
        return performedByQid;
    }

    public HospitalVisitScheduleEntity setPerformedByQid(String performedByQid) {
        this.performedByQid = performedByQid;
        return this;
    }

    public boolean isNotifyForUpcomingVisit() {
        return notifyForUpcomingVisit;
    }

    public HospitalVisitScheduleEntity setNotifyForUpcomingVisit(boolean notifyForUpcomingVisit) {
        this.notifyForUpcomingVisit = notifyForUpcomingVisit;
        return this;
    }
}
