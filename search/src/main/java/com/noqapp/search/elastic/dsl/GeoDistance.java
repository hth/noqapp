package com.noqapp.search.elastic.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.shared.GeoPointOfQ;

/**
 * hitender
 * 11/22/17 11:45 AM
 */
@SuppressWarnings ({
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
public class GeoDistance extends AbstractDomain {

    @JsonProperty("distance")
    private String distance;

    @JsonProperty("GH")
    private String geoHash;

    @JsonProperty("COR")
    private GeoPointOfQ geoPointOfQ;

    public String getDistance() {
        return distance;
    }

    public GeoDistance setDistance(String distance) {
        this.distance = distance;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public GeoDistance setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public GeoPointOfQ getGeoPointOfQ() {
        return geoPointOfQ;
    }

    public GeoDistance setGeoPointOfQ(GeoPointOfQ geoPointOfQ) {
        this.geoPointOfQ = geoPointOfQ;
        return this;
    }
}
