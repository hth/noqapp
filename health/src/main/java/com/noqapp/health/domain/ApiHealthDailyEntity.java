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
 * hitender
 * 11/18/17 6:03 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "API_HEALTH_DAILY")
@CompoundIndexes({
        @CompoundIndex(name = "api_health_daily_idx", def = "{'API': 1, 'ME': 1, 'CL': 1}", unique = false)
})
public class ApiHealthDailyEntity {
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

    /* Auto delete records in 2 months. */
    @Indexed(name = "api_health_daily_auto_expire_idx", expireAfterSeconds = 5184000)
    @Field("C")
    private Date created = new Date();
}
