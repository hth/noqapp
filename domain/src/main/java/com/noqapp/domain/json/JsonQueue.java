package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.QueueStatusEnum;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 12/1/16 9:28 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
public class JsonQueue extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueue.class);

    @JsonProperty ("c")
    private String codeQR;

    @JsonProperty ("cor")
    private double[] coordinate;

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("sa")
    private String storeAddress;

    @JsonProperty ("cs")
    private String countryShortName;

    @JsonProperty ("p")
    private String storePhone;

    @JsonProperty ("f")
    private int tokenAvailableFrom;

    /* Store business start hour. */
    @JsonProperty ("b")
    private int startHour;

    @JsonProperty ("m")
    private int tokenNotAvailableFrom;

    /* Store business end hour. */
    @JsonProperty ("e")
    private int endHour;

    @JsonProperty ("pj")
    private boolean preventJoining;

    @JsonProperty ("dc")
    private boolean dayClosed = false;

    @JsonProperty ("o")
    private String topic;

    @JsonProperty ("s")
    private int servingNumber;

    @JsonProperty ("l")
    private int lastNumber;

    @JsonProperty ("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty ("se")
    private String serviceEndTime;

    @JsonProperty ("rj")
    private int remoteJoinCount;

    @JsonProperty ("u")
    private String created;

    @JsonProperty ("ra")
    private float rating;

    @JsonProperty ("rc")
    private int ratingCount;

    @JsonProperty ("as")
    private long averageServiceTime;

    @JsonProperty ("ja")
    private boolean remoteJoinAvailable = false;

    @JsonProperty ("lu")
    private boolean allowLoggedInUser = false;

    public JsonQueue(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public JsonQueue setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public JsonQueue setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonQueue setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonQueue setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonQueue setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonQueue setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public JsonQueue setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public JsonQueue setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public JsonQueue setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public JsonQueue setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public JsonQueue setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public JsonQueue setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public JsonQueue setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public JsonQueue setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public JsonQueue setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonQueue setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public JsonQueue setServiceEndTime(Date serviceEndTime) {
        this.serviceEndTime = DateFormatUtils.format(serviceEndTime, ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        return this;
    }

    public int getRemoteJoinCount() {
        return remoteJoinCount;
    }

    public void setRemoteJoinCount(int remoteJoinCount) {
        this.remoteJoinCount = remoteJoinCount;
    }

    public String getCreated() {
        return created;
    }

    public JsonQueue setCreated(Date created) {
        this.created = DateFormatUtils.format(created, ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        return this;
    }

    public float getRating() {
        return rating;
    }

    public JsonQueue setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public JsonQueue setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public JsonQueue setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
        return this;
    }

    public boolean isRemoteJoinAvailable() {
        return remoteJoinAvailable;
    }

    public JsonQueue setRemoteJoinAvailable(boolean remoteJoinAvailable) {
        this.remoteJoinAvailable = remoteJoinAvailable;
        return this;
    }

    public boolean isAllowLoggedInUser() {
        return allowLoggedInUser;
    }

    public JsonQueue setAllowLoggedInUser(boolean allowLoggedInUser) {
        this.allowLoggedInUser = allowLoggedInUser;
        return this;
    }
}
