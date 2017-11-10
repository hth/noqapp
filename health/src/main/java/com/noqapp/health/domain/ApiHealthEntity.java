package com.noqapp.health.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * User: hitender
 * Date: 11/07/17 10:13 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "HEALTH_SERVICE")
@CompoundIndexes({
        @CompoundIndex(name = "health_service_idx", def = "{'API': 1, 'ME': 1, 'CL': 1}", unique = false)
})
public class ApiHealthEntity extends BaseEntity {
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

    /* Auto delete records in 2 days. */
    @Indexed(name="health_service_auto_delete_idx", expireAfterSeconds=172800)
    @Field ("E")
    private Date expire = new Date();

    public String getApi() {
        return api;
    }

    public ApiHealthEntity setApi(String api) {
        this.api = api;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ApiHealthEntity setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public ApiHealthEntity setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public ApiHealthEntity setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public HealthStatusEnum getHealthStatus() {
        return healthStatus;
    }

    public ApiHealthEntity setHealthStatus(HealthStatusEnum healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }
}
