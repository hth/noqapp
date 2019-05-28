package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: hitender
 * Date: 2019-05-28 12:40
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
public class JsonQueueDisplay extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueueDisplay.class);

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("p")
    private String storePhone;

    @JsonProperty("f")
    private int tokenAvailableFrom;

    /* Store business start hour. */
    @JsonProperty("b")
    private int startHour;

    @JsonProperty("m")
    private int tokenNotAvailableFrom;

    /* Store business end hour. */
    @JsonProperty("e")
    private int endHour;

    @JsonProperty("de")
    private int delayedInMinutes;

    @JsonProperty("pj")
    private boolean preventJoining;

    @JsonProperty("dc")
    private boolean dayClosed = false;

    public JsonQueueDisplay() {
        //Required default constructor
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonQueueDisplay setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonQueueDisplay setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonQueueDisplay setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonQueueDisplay setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getArea() {
        return area;
    }

    public JsonQueueDisplay setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonQueueDisplay setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonQueueDisplay setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public JsonQueueDisplay setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public JsonQueueDisplay setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public JsonQueueDisplay setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public JsonQueueDisplay setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public JsonQueueDisplay setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public int getDelayedInMinutes() {
        return delayedInMinutes;
    }

    public JsonQueueDisplay setDelayedInMinutes(int delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
        return this;
    }

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public JsonQueueDisplay setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public JsonQueueDisplay setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public static JsonQueueDisplay populate(BizStoreEntity bizStore, StoreHourEntity storeHour) {
        return new JsonQueueDisplay()
            .setBusinessName(bizStore.getBizName().getBusinessName())
            .setDisplayName(bizStore.getDisplayName())
            .setBusinessType(bizStore.getBusinessType())
            .setStoreAddress(bizStore.getAddress())
            .setArea(bizStore.getArea())
            .setTown(bizStore.getTown())
            .setCountryShortName(bizStore.getCountryShortName())
            .setStorePhone(bizStore.getPhoneFormatted())
            .setTokenAvailableFrom(storeHour.getTokenAvailableFrom())
            .setStartHour(storeHour.getStartHour())
            .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
            .setEndHour(storeHour.getEndHour())
            .setDelayedInMinutes(storeHour.getDelayedInMinutes())
            .setPreventJoining(storeHour.isPreventJoining())
            .setDayClosed(bizStore.getBizName().isDayClosed() || storeHour.isDayClosed() || storeHour.isTempDayClosed());
    }
}
