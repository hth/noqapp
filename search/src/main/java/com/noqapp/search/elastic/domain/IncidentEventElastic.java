package com.noqapp.search.elastic.domain;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.IncidentEventEnum;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * hitender
 * 5/30/21 5:51 PM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentEventElastic extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(IncidentEventElastic.class);

    public static final String TYPE = "INCIDENT_EVENT".toLowerCase();
    public static final String INDEX = ElasticsearchClientConfiguration.INDEX + UNDER_SCORE + TYPE;

    @JsonIgnore
    private String id;

    @JsonProperty("IE")
    private IncidentEventEnum incidentEvent;

    @JsonProperty("IED")
    private String incidentEventDescription;

    @JsonProperty("GH")
    private String geoHash;

    @JsonProperty("TI")
    private String title;

    @JsonProperty("C")
    private Date created;

    public String getId() {
        return id;
    }

    public IncidentEventElastic setId(String id) {
        this.id = id;
        return this;
    }

    public IncidentEventEnum getIncidentEvent() {
        return incidentEvent;
    }

    public IncidentEventElastic setIncidentEvent(IncidentEventEnum incidentEvent) {
        this.incidentEvent = incidentEvent;
        this.incidentEventDescription = incidentEvent.getDescription();
        return this;
    }

    public String getIncidentEventDescription() {
        return incidentEventDescription;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public IncidentEventElastic setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public IncidentEventElastic setTitle(String title) {
        this.title = title;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public IncidentEventElastic setCreated(Date created) {
        this.created = created;
        return this;
    }
}
