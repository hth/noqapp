package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.domain.AbstractDomain;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    @JsonProperty("lastChecked")
    private String lastChecked = DateFormatUtils.format(new Date(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

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
}
