package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.domain.AbstractDomain;
import com.noqapp.domain.types.HealthStatusEnum;

import java.time.Duration;
import java.time.Instant;

/**
 * User: hitender
 * Date: 11/07/17 12:28 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonHealthServiceCheck extends AbstractDomain {

    @JsonProperty("service")
    private String name;

    @JsonIgnore
    private Instant start;

    @JsonIgnore
    private Instant end;

    @JsonProperty("status")
    private HealthStatusEnum healthStatus;

    public JsonHealthServiceCheck(String name) {
        this.name = name;
        this.start = Instant.now();
    }

    public JsonHealthServiceCheck ended() {
        this.end = Instant.now();
        return this;
    }

    public HealthStatusEnum getHealthStatus() {
        return healthStatus;
    }

    public JsonHealthServiceCheck setHealthStatus(HealthStatusEnum healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }

    @JsonProperty("duration")
    public long duration() {
        return Duration.between(start, end).toNanos() / 1000000;
    }
}
