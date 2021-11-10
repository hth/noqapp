package com.noqapp.domain.json.fcm.data;

import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.IncidentEventEnum;
import com.noqapp.domain.types.MessageOriginEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 5/21/21 3:39 PM
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
public class JsonIncidentEventData extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

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

    JsonIncidentEventData(FirebaseMessageTypeEnum firebaseMessageType, MessageOriginEnum messageOrigin) {
        super(firebaseMessageType);
        this.messageOrigin = messageOrigin;
    }

    public IncidentEventEnum getIncidentEvent() {
        return incidentEvent;
    }

    public JsonIncidentEventData setIncidentEvent(IncidentEventEnum incidentEvent) {
        this.incidentEvent = incidentEvent;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public JsonIncidentEventData setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public JsonIncidentEventData setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getArea() {
        return area;
    }

    public JsonIncidentEventData setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonIncidentEventData setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public JsonIncidentEventData setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public JsonIncidentEventData setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public JsonIncidentEventData setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public JsonIncidentEventData setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public JsonIncidentEventData setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonIncidentEventData setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }
}
