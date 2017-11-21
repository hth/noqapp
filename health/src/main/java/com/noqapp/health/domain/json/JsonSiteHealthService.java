package com.noqapp.health.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.health.domain.types.HealthStatusEnum;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

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
public class JsonSiteHealthService extends AbstractDomain {

    @JsonProperty("service")
    private String name;

    @JsonIgnore
    private Instant start;

    @JsonIgnore
    private Instant end;

    @JsonProperty("status")
    private HealthStatusEnum healthStatus;

    public JsonSiteHealthService(String name) {
        this.name = name;
        this.start = Instant.now();
    }

    public JsonSiteHealthService ended() {
        this.end = Instant.now();
        return this;
    }

    public HealthStatusEnum getHealthStatus() {
        return healthStatus;
    }

    public JsonSiteHealthService setHealthStatus(HealthStatusEnum healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }

    /**
     * Nano to milliseconds divide by 1_000_000.
     *
     * @return
     */
    @JsonProperty("duration")
    public String duration() {
        return String.format("%d ms", TimeUnit.NANOSECONDS.toMillis(Duration.between(start, end).toNanos()));
    }
}
