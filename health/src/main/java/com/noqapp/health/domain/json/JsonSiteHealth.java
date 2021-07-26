package com.noqapp.health.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class JsonSiteHealth extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonSiteHealth.class);

    @JsonIgnore
    private Instant created = Instant.now();

    @JsonProperty("serviceUp")
    private int serviceUp;

    @JsonProperty("services")
    private List<JsonSiteHealthService> jsonSiteHealthServices = new ArrayList<>();

    @JsonProperty("health")
    public String health() {
        return String.format("%d of %d", serviceUp, jsonSiteHealthServices.size());
    }

    @JsonIgnore
    public void increaseServiceUpCount() {
        serviceUp++;
    }

    public List<JsonSiteHealthService> getJsonSiteHealthServices() {
        return jsonSiteHealthServices;
    }

    public JsonSiteHealth setJsonSiteHealthServices(List<JsonSiteHealthService> jsonSiteHealthServices) {
        this.jsonSiteHealthServices = jsonSiteHealthServices;
        return this;
    }

    public JsonSiteHealth addJsonHealthServiceChecks(JsonSiteHealthService jsonSiteHealthService) {
        this.jsonSiteHealthServices.add(jsonSiteHealthService);
        return this;
    }

    @JsonProperty("lastChecked")
    public String lastChecked() {
        return String.format("%d m", Duration.between(created, Instant.now()).toMinutes());
    }
}
