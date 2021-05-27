package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.IncidentEventEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 5/21/21 1:02 PM
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
public class JsonIncidentEvent extends AbstractDomain {

    @JsonProperty("ie")
    private IncidentEventEnum incidentEvent;

    /* Format Longitude and then Latitude. */
    @JsonProperty("cor")
    private double[] coordinate;

    @JsonProperty("ad")
    private String address;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("dt")
    private String district;

    @JsonProperty("st")
    private String state;

    @JsonProperty("ss")
    private String stateShortName;

    /* Postal code could be empty for few countries. */
    @JsonProperty("pc")
    private String postalCode;

    @JsonProperty("cc")
    private String country;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("ti")
    private String title;

    @JsonProperty("ds")
    private String description;

    public IncidentEventEnum getIncidentEvent() {
        return incidentEvent;
    }

    public JsonIncidentEvent setIncidentEvent(IncidentEventEnum incidentEvent) {
        this.incidentEvent = incidentEvent;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public JsonIncidentEvent setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public JsonIncidentEvent setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getArea() {
        return area;
    }

    public JsonIncidentEvent setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonIncidentEvent setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public JsonIncidentEvent setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public JsonIncidentEvent setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public JsonIncidentEvent setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public JsonIncidentEvent setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public JsonIncidentEvent setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonIncidentEvent setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JsonIncidentEvent setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JsonIncidentEvent setDescription(String description) {
        this.description = description;
        return this;
    }
}
