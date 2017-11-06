package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.domain.AbstractDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Response shown when application is working.
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
public class JsonHealthCheck extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueue.class);

    @JsonIgnore
    private Instant created = Instant.now();

    @JsonProperty("serviceUp")
    private int serviceUp;

    @JsonProperty("services")
    private List<JsonHealthServiceCheck> jsonHealthServiceChecks = new ArrayList<>();

    @JsonProperty("health")
    public String health() {
        return String.format("%d of %d", serviceUp, jsonHealthServiceChecks.size());
    }

    @JsonIgnore
    public void increaseServiceUpCount() {
        serviceUp++;
    }

    public List<JsonHealthServiceCheck> getJsonHealthServiceChecks() {
        return jsonHealthServiceChecks;
    }

    public JsonHealthCheck setJsonHealthServiceChecks(List<JsonHealthServiceCheck> jsonHealthServiceChecks) {
        this.jsonHealthServiceChecks = jsonHealthServiceChecks;
        return this;
    }

    public JsonHealthCheck addJsonHealthServiceChecks(JsonHealthServiceCheck jsonHealthServiceCheck) {
        this.jsonHealthServiceChecks.add(jsonHealthServiceCheck);
        return this;
    }

    @JsonProperty("lastChecked")
    public String lastChecked() {
        return String.format("%d m", Duration.between(created, Instant.now()).toMinutes());
    }
}
