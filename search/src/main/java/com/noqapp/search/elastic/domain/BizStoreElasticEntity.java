package com.noqapp.search.elastic.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.domain.shared.GeoPointOfQ;

import java.util.Arrays;

/**
 * Json for elastic search.
 *
 * User: hitender
 * Date: 11/15/17 4:28 PM
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
public class BizStoreElasticEntity extends AbstractDomain {

    public static final String TYPE = "BIZ_STORE".toLowerCase();
    public static final String INDEX = ElasticsearchClientConfiguration.INDEX + "_" + TYPE;

    @JsonIgnore
    private String id;

    @JsonProperty("N")
    private String businessName;

    @JsonProperty ("AD")
    private String address;

    @JsonProperty ("TO")
    private String town;

    @JsonProperty ("DT")
    private String district;

    @JsonProperty ("ST")
    private String state;

    @JsonProperty ("SS")
    private String stateShortName;

    /* Postal code could be empty for few countries. */
    @JsonProperty ("PC")
    private String postalCode;

    @JsonProperty ("CC")
    private String country;

    @JsonProperty ("CS")
    private String countryShortName;

    /* Phone number saved with country code. */
    @JsonProperty ("PH")
    private String phone;

    /* To not loose user entered phone number. */
    @JsonProperty ("PR")
    private String phoneRaw;

    @JsonProperty ("COR")
    private GeoPointOfQ geoPointOfQ;

    @JsonProperty ("PI")
    private String placeId;

    @JsonProperty ("PT")
    private String[] placeType;

    @JsonProperty ("RA")
    private float rating;

    @JsonProperty ("RC")
    private int ratingCount;

    @JsonProperty ("BIZ_NAME")
    private String bizNameId;

    @JsonProperty("DN")
    private String displayName;

    @JsonProperty ("QR")
    private String codeQR;

    @JsonProperty ("TZ")
    private String timeZone;

    @JsonProperty ("GH")
    private String geoHash;


    public String getId() {
        return id;
    }

    public BizStoreElasticEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public BizStoreElasticEntity setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public BizStoreElasticEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getTown() {
        return town;
    }

    public BizStoreElasticEntity setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public BizStoreElasticEntity setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public BizStoreElasticEntity setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public BizStoreElasticEntity setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public BizStoreElasticEntity setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public BizStoreElasticEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public BizStoreElasticEntity setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public BizStoreElasticEntity setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public BizStoreElasticEntity setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
        return this;
    }

    public GeoPointOfQ getGeoPointOfQ() {
        return geoPointOfQ;
    }

    public BizStoreElasticEntity setGeoPointOfQ(GeoPointOfQ geoPointOfQ) {
        this.geoPointOfQ = geoPointOfQ;
        return this;
    }

    public String getPlaceId() {
        return placeId;
    }

    public BizStoreElasticEntity setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public String[] getPlaceType() {
        return placeType;
    }

    public BizStoreElasticEntity setPlaceType(String[] placeType) {
        this.placeType = placeType;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public BizStoreElasticEntity setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public BizStoreElasticEntity setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public BizStoreElasticEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BizStoreElasticEntity setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public BizStoreElasticEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public BizStoreElasticEntity setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public BizStoreElasticEntity setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    @Override
    public String toString() {
        return "BizStoreElasticEntity{" +
                "id='" + id + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                ", town='" + town + '\'' +
                ", district='" + district + '\'' +
                ", state='" + state + '\'' +
                ", stateShortName='" + stateShortName + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", phone='" + phone + '\'' +
                ", phoneRaw='" + phoneRaw + '\'' +
                ", geoPointOfQ=" + geoPointOfQ +
                ", placeId='" + placeId + '\'' +
                ", placeType=" + Arrays.toString(placeType) +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                ", bizNameId='" + bizNameId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", codeQR='" + codeQR + '\'' +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }
}
