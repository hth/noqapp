package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.AmenityEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryTypeEnum;
import com.noqapp.domain.types.FacilityEnum;
import com.noqapp.domain.types.PaymentTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty ("c")
    private String codeQR;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

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

    @JsonProperty ("de")
    private int delayedInMinutes;

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

    @JsonProperty ("at")
    private int availableTokenCount;

    @JsonProperty ("bc")
    private String bizCategoryId;

    @JsonProperty("ff")
    private String famousFor;

    @JsonProperty ("dd")
    private int discount = 15;

    @JsonProperty ("md")
    private int minimumDeliveryOrder = 100;

    @JsonProperty ("si")
    private Set<String> storeServiceImages = new LinkedHashSet<>();

    @JsonProperty ("ii")
    private Set<String> storeInteriorImages = new LinkedHashSet<String>() {{add("https://noqapp.com/imgs/60x60/e.jpeg"); add("https://noqapp.com/imgs/60x60/c.png");}};

    @JsonProperty ("pm")
    private List<PaymentTypeEnum> paymentTypes = new LinkedList<PaymentTypeEnum>() {{add(PaymentTypeEnum.AP); add(PaymentTypeEnum.CA); add(PaymentTypeEnum.CC); add(PaymentTypeEnum.DC); }};

    @JsonProperty ("dm")
    private List<DeliveryTypeEnum> deliveryTypes = new LinkedList<DeliveryTypeEnum>() {{add(DeliveryTypeEnum.HD); add(DeliveryTypeEnum.TO);}};

    @JsonProperty ("am")
    private List<AmenityEnum> amenities = new LinkedList<>();

    @JsonProperty ("fa")
    private List<FacilityEnum> facilities = new LinkedList<>();

    public JsonQueue() {
        //Required default constructor
    }

    public JsonQueue(String bizStoreId, String codeQR) {
        this.bizStoreId = bizStoreId;
        this.codeQR = codeQR;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public JsonQueue setGeoHash(String geoHash) {
        this.geoHash = geoHash;
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

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonQueue setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public JsonQueue setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getArea() {
        return area;
    }

    public JsonQueue setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonQueue setTown(String town) {
        this.town = town;
        return this;
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

    public int getDelayedInMinutes() {
        return delayedInMinutes;
    }

    public JsonQueue setDelayedInMinutes(int delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
        return this;
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

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public JsonQueue setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonQueue setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getFamousFor() {
        return famousFor;
    }

    public JsonQueue setFamousFor(String famousFor) {
        this.famousFor = famousFor;
        return this;
    }

    public int getDiscount() {
        return discount;
    }

    public JsonQueue setDiscount(int discount) {
        this.discount = discount;
        return this;
    }

    public int getMinimumDeliveryOrder() {
        return minimumDeliveryOrder;
    }

    public JsonQueue setMinimumDeliveryOrder(int minimumDeliveryOrder) {
        this.minimumDeliveryOrder = minimumDeliveryOrder;
        return this;
    }

    public Set<String> getStoreServiceImages() {
        return storeServiceImages;
    }

    public JsonQueue setStoreServiceImages(Set<String> storeServiceImages) {
        this.storeServiceImages = storeServiceImages;
        return this;
    }

    public Set<String> getStoreInteriorImages() {
        return storeInteriorImages;
    }

    public JsonQueue setStoreInteriorImages(Set<String> storeInteriorImages) {
        this.storeInteriorImages = storeInteriorImages;
        return this;
    }

    public List<PaymentTypeEnum> getPaymentTypes() {
        return paymentTypes;
    }

    public JsonQueue setPaymentTypes(List<PaymentTypeEnum> paymentTypes) {
        this.paymentTypes = paymentTypes;
        return this;
    }

    public List<DeliveryTypeEnum> getDeliveryTypes() {
        return deliveryTypes;
    }

    public JsonQueue setDeliveryTypes(List<DeliveryTypeEnum> deliveryTypes) {
        this.deliveryTypes = deliveryTypes;
        return this;
    }

    public List<AmenityEnum> getAmenities() {
        return amenities;
    }

    public JsonQueue setAmenities(List<AmenityEnum> amenities) {
        this.amenities = amenities;
        return this;
    }

    public List<FacilityEnum> getFacilities() {
        return facilities;
    }

    public JsonQueue setFacilities(List<FacilityEnum> facilities) {
        this.facilities = facilities;
        return this;
    }
}
