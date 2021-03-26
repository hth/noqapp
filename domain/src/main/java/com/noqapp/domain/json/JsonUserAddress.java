package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.UserAddressEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.annotation.Transient;

import org.elasticsearch.common.geo.GeoPoint;

import java.util.StringJoiner;

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

    @JsonProperty("lat")
    private String latitude;

    @JsonProperty("lng")
    private String longitude;

    @JsonProperty("pa")
    private boolean primaryAddress;

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

    public String getArea() {
        return area;
    }

    public JsonUserAddress setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonUserAddress setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public JsonUserAddress setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public JsonUserAddress setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public JsonUserAddress setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getLatitude() {
        return latitude;
    }

    public JsonUserAddress setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public JsonUserAddress setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public boolean isPrimaryAddress() {
        return primaryAddress;
    }

    public JsonUserAddress setPrimaryAddress(boolean primaryAddress) {
        this.primaryAddress = primaryAddress;
        return this;
    }

    @Transient
    public double[] getCoordinate() {
        return CommonUtil.getCoordinates(longitude, latitude);
    }

    @Transient
    public String geoHash() {
        /* Latitude and then Longitude. */
        double[] coordinate = getCoordinate();
        return new GeoPoint(coordinate[1], coordinate[0]).getGeohash();
    }

    @Transient
    public static JsonUserAddress populateAsJson(UserAddressEntity userAddress) {
        return new JsonUserAddress()
            .setId(userAddress.getId())
            .setAddress(userAddress.getAddress())
            .setArea(userAddress.getArea())
            .setTown(userAddress.getTown())
            .setDistrict(userAddress.getDistrict())
            .setState(userAddress.getState())
            .setStateShortName(userAddress.getStateShortName())
            .setCountryShortName(userAddress.getCountryShortName())
            .setPrimaryAddress(userAddress.isPrimaryAddress())
            .setGeoHash(userAddress.getGeoHash())
            .setLatitude(String.valueOf(userAddress.getCoordinate()[1]))
            .setLongitude(String.valueOf(userAddress.getCoordinate()[0]));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonUserAddress.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .add("address='" + address + "'")
            .add("geoHash='" + geoHash + "'")
            .add("countryShortName='" + countryShortName + "'")
            .add("area='" + area + "'")
            .add("town='" + town + "'")
            .add("district='" + district + "'")
            .add("state='" + state + "'")
            .add("stateShortName='" + stateShortName + "'")
            .add("latitude='" + latitude + "'")
            .add("longitude='" + longitude + "'")
            .add("primaryAddress=" + primaryAddress)
            .toString();
    }
}
