package com.noqapp.health.domain;

import com.noqapp.health.domain.types.HealthStatusEnum;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 11/07/17 10:13 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "API_HEALTH_NOW")
@CompoundIndexes({
        @CompoundIndex(name = "api_health_now_idx", def = "{'API': 1, 'ME': 1, 'CL': 1}", unique = false)
})
public class ApiHealthNowEntity {
    @Id
    protected String id;

    @NotNull
    @Field("API")
    private String api;

    @Field("ME")
    private String methodName;

    @Field("CL")
    private String clazzName;

    @Field("DU")
    private long duration;

    @Field("HS")
    private HealthStatusEnum healthStatus;

    /* Auto delete records in 15 days. */
    @Indexed(name = "continuous_auto_expire_idx", expireAfterSeconds = 1296000)
    @Field("C")
    private Date created = new Date();

    public String getApi() {
        return api;
    }

    public ApiHealthNowEntity setApi(String api) {
        this.api = api;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ApiHealthNowEntity setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public ApiHealthNowEntity setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public ApiHealthNowEntity setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public HealthStatusEnum getHealthStatus() {
        return healthStatus;
    }

    public ApiHealthNowEntity setHealthStatus(HealthStatusEnum healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }
}
