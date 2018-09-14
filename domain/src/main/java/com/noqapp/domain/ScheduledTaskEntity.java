package com.noqapp.domain;

import com.noqapp.domain.types.ScheduleTaskEnum;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 9/6/18 11:01 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "SCHEDULED_TASK")
public class ScheduledTaskEntity extends BaseEntity {

    @Field("FR")
    private String from;

    @Field("UN")
    private String until;

    @Field("TA")
    private ScheduleTaskEnum scheduleTask;

    public String getFrom() {
        return from;
    }

    public ScheduledTaskEntity setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getUntil() {
        return until;
    }

    public ScheduledTaskEntity setUntil(String until) {
        this.until = until;
        return this;
    }

    public ScheduleTaskEnum getScheduleTask() {
        return scheduleTask;
    }

    public ScheduledTaskEntity setScheduleTask(ScheduleTaskEnum scheduleTask) {
        this.scheduleTask = scheduleTask;
        return this;
    }
}
