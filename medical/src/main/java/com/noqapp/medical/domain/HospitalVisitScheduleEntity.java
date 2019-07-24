package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.BooleanReplacementEnum;
import com.noqapp.domain.types.medical.HospitalVisitForEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private Map<String, BooleanReplacementEnum> visitingFor = new LinkedHashMap<>();

    @Field("VH")
    private String header;

    @Field("ED")
    private Date expectedDate;

    @Field("VD")
    private Date visitedDate;

    @Field("PQ")
    private String performedByQid;

    @Field ("NC")
    private int notificationCount;

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

    public Map<String, BooleanReplacementEnum> getVisitingFor() {
        return visitingFor;
    }

    public HospitalVisitScheduleEntity setVisitingFor(Map<String, BooleanReplacementEnum> visitingFor) {
        this.visitingFor = visitingFor;
        return this;
    }

    public HospitalVisitScheduleEntity addVisitingFor(String visitName, BooleanReplacementEnum booleanReplacement) {
        this.visitingFor.put(visitName, booleanReplacement);
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

    public int getNotificationCount() {
        return notificationCount;
    }

    public HospitalVisitScheduleEntity setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
        return this;
    }
}
