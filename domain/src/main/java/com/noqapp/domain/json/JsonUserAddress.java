package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 5/16/18 10:07 AM
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
public class JsonUserAddress extends AbstractDomain {

    @JsonProperty("id")
    private String id;

    @JsonProperty("ad")
    private String address;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty("cs")
    private String countryShortName;

    public String getId() {
        return id;
    }

    public JsonUserAddress setId(String id) {
        this.id = id;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public JsonUserAddress setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public JsonUserAddress setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonUserAddress setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }
}
