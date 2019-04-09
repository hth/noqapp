package com.noqapp.domain;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.MathUtil;
import com.noqapp.domain.shared.GeoPointOfQ;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.AmenityEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.FacilityEnum;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.ServicePaymentEnum;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import org.elasticsearch.common.geo.GeoPoint;

import java.time.DayOfWeek;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 11/23/16 4:28 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "BIZ_STORE")
@CompoundIndexes(value = {
        /* Business name with address and phone makes it a unique store. */
        @CompoundIndex(name = "biz_store_ph_idx", def = "{'PH': 1}", unique = false),
        @CompoundIndex(name = "biz_store_qr_idx", def = "{'QR': 1}", unique = true),
        @CompoundIndex(name = "biz_store_wl_idx", def = "{'WL': 1}", unique = true),
        @CompoundIndex(name = "biz_store_cor_cs_idx", def = "{'COR': '2d', 'CS': 1}"),
})
public class BizStoreEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreEntity.class);

    /** Field name */
    public static final String ADDRESS_FIELD_NAME = "AD";
    public static final String PHONE_FIELD_NAME = "PH";
    public static final String UNDER_SCORE = "_";

    /** Better to add a BLANK PHONE then to add nothing when biz does not have a phone number */
    @Value("${phoneNumberBlank:000_000_0000}")
    private String phoneNumberBlank;

    @NotNull
    @Field("AD")
    private String address;

    @Field("AR")
    private String area;

    @Field("TO")
    private String town;

    @Field("DT")
    private String district;

    @Field("ST")
    private String state;

    @Field("SS")
    private String stateShortName;

    /* Postal code could be empty for few countries. */
    @Field("PC")
    private String postalCode;

    @Field("CC")
    private String country;

    @NotNull
    @Field("CS")
    private String countryShortName;

    /* Phone number saved with country code. */
    @NotNull
    @Field("PH")
    private String phone;

    /* To not loose user entered phone number. */
    @Field("PR")
    private String phoneRaw;

    @Field("BT")
    private BusinessTypeEnum businessType;

    /* Format Longitude and then Latitude. */
    @Field("COR")
    private double[] coordinate;

    @Field("PI")
    private String placeId;

    @Field("PT")
    private String[] placeType;

    @Field("RA")
    private float rating;

    @Field("RC")
    private int reviewCount;

    @Field("AS")
    private long averageServiceTime;

    @DBRef
    @Field("BIZ_NAME")
    private BizNameEntity bizName;

    @Field("EA")
    private boolean validatedUsingExternalAPI;

    @Field("VC")
    private int validationCount;

    @Field("DN")
    private String displayName;

    @Field("QR")
    private String codeQR;

    @Field("TZ")
    private String timeZone;

    /* Used when running cron job. */
    @Field("QH")
    private Date queueHistory = new Date();

    @NotNull
    @Field("AO")
    private AddressOriginEnum addressOrigin;

    @Field("WL")
    private String webLocation;

    @Field("BC")
    private String bizCategoryId;

    @Field("FF")
    private String famousFor;

    @Field("DD")
    private int discount;

    @Field("MD")
    private int minimumDeliveryOrder;

    @Field("DR")
    private int deliveryRange = 5;

    @Field("SI")
    private Set<String> storeServiceImages = new LinkedHashSet<>();

    @Field("II")
    private Set<String> storeInteriorImages = new LinkedHashSet<>();

    @Field("PM")
    private List<PaymentModeEnum> paymentModes = new LinkedList<>();

    @Field("DM")
    private List<DeliveryModeEnum> deliveryModes = new LinkedList<>();

    @Field("AM")
    private List<AmenityEnum> amenities = new LinkedList<>();

    @Field("FA")
    private List<FacilityEnum> facilities = new LinkedList<>();

    //***************************/
    //*  Queue Settings Starts. */
    //***************************/
    @Field("RJ")
    private boolean remoteJoin = false;

    @Field("LU")
    private boolean allowLoggedInUser = false;

    @Field("AT")
    private int availableTokenCount;
    //***************************/
    //*  Queue Settings Ends.   */
    //***************************/

    //*********************************/
    //*  Queue Price Setting Starts.  */
    //*********************************/
    @Field("EP")
    private boolean enabledPayment = false;

    @Field("PP")
    private int productPrice;

    @Field("CF")
    private int cancellationPrice;

    @Field("SP")
    private ServicePaymentEnum servicePayment = ServicePaymentEnum.O;

    @Field("FD")
    private int freeWithinDays = 3;
    //*********************************/
    //*  Queue Price Settings Ends.   */
    //*********************************/

    /* Contains Id if a task is assigned. */
    @Field("TA")
    private String scheduledTaskId;

    @Transient
    private List<StoreHourEntity> storeHours;

    public BizStoreEntity() {
        //Default constructor, required to keep bean happy
    }

    public static BizStoreEntity newInstance() {
        return new BizStoreEntity();
    }

    /**
     * For web display of the address.
     *
     * @return
     */
    @Transient
    public String getAddressWrapped() {
        return address.replaceFirst(",", "<br/>");
    }

    @Transient
    public String getLocation() {
        String[] split = StringUtils.split(address, ",");
        return split[split.length - 3] + ", " + (split[split.length - 2]).trim().split(" ")[0];
    }

    @Deprecated
    public String getAddressWrappedMore() {
        return getAddressWrapped().replaceFirst(",", "<br/>");
    }

    @Transient
    public String getAddressWrappedFunky() {
        if (StringUtils.isNotBlank(area)) {
            String[] split = address.split(area);
            if (split.length > 1) {
                String address = split[0].length() > 0 ? split[0] + "<br/>" : "";
                /* Compare name of area and town to ignore repeat of it. */
                if (StringUtils.isNotBlank(area) && !area.equalsIgnoreCase(town)) {
                    address += area + ", ";
                }

                if (StringUtils.isNotBlank(area)) {
                    address += town + "," + "<br/>";
                }

                address += split[1].replace(", " + town + ",", "").replaceFirst(",", "").trim();
                LOG.debug("Address={}", address);
                return address;
            } else {
                if (StringUtils.countMatches(address, ",") > 3) {
                    split = address.split(",", 3);
                    return split[0] + "<br/>" + split[1] + "<br/>" + split[2];
                }
            }
        }
        LOG.warn("Returning old address wrapping bizId={} {} {}", id, displayName, bizName.getBusinessName());
        return getAddressWrappedMore();
    }

    /** Something like Sunnyvale, California. */
    @Transient
    public String getAreaAndTown() {
        return area + ", " + town;
    }

    /**
     * Escape String for Java Script.
     *
     * @return
     */
    public String getSafeJSAddress() {
        return StringEscapeUtils.escapeEcmaScript(address);
    }

    public String getAddress() {
        return address;
    }

    public BizStoreEntity setAddress(String address) {
        this.address = StringUtils.strip(address);
        return this;
    }

    public String getArea() {
        return area;
    }

    public BizStoreEntity setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public BizStoreEntity setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public BizStoreEntity setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public BizStoreEntity setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public BizStoreEntity setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public BizStoreEntity setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public BizStoreEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public BizStoreEntity setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Remove everything other than numbers. Do the formatting on client side.
     *
     * @param phone
     */
    public BizStoreEntity setPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            this.phone = Formatter.phoneCleanup(phoneNumberBlank);
        } else {
            this.phone = Formatter.phoneCleanup(phone);
        }
        return this;
    }

    public String getPhoneFormatted() {
        return Formatter.phoneFormatter(phone, countryShortName);
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public BizStoreEntity setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public BizStoreEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public BizStoreEntity setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
        return this;
    }

    public boolean isValidatedUsingExternalAPI() {
        return validatedUsingExternalAPI;
    }

    public BizStoreEntity setValidatedUsingExternalAPI(boolean validatedUsingExternalAPI) {
        this.validatedUsingExternalAPI = validatedUsingExternalAPI;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public BizStoreEntity setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public String getPlaceId() {
        return placeId;
    }

    public BizStoreEntity setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public String[] getPlaceType() {
        return placeType;
    }

    public BizStoreEntity setPlaceType(String[] placeType) {
        this.placeType = placeType;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public BizStoreEntity setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public BizStoreEntity setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public BizStoreEntity setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
        return this;
    }

    public int getValidationCount() {
        return validationCount;
    }

    public BizStoreEntity setValidationCount(int validationCount) {
        this.validationCount = validationCount;
        return this;
    }

    public void increaseValidationCount() {
        this.validationCount += 1;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public BizStoreEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BizStoreEntity setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public BizStoreEntity setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Date getQueueHistory() {
        return queueHistory;
    }

    public BizStoreEntity setQueueHistory(Date queueHistory) {
        this.queueHistory = queueHistory;
        return this;
    }

    public AddressOriginEnum getAddressOrigin() {
        return addressOrigin;
    }

    public BizStoreEntity setAddressOrigin(AddressOriginEnum addressOrigin) {
        this.addressOrigin = addressOrigin;
        return this;
    }

    public String getWebLocation() {
        return webLocation;
    }

    public BizStoreEntity setWebLocation(String webLocation) {
        this.webLocation = webLocation;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public BizStoreEntity setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getFamousFor() {
        return famousFor;
    }

    public BizStoreEntity setFamousFor(String famousFor) {
        this.famousFor = famousFor;
        return this;
    }

    public int getDiscount() {
        return discount;
    }

    public BizStoreEntity setDiscount(int discount) {
        this.discount = discount;
        return this;
    }

    public int getMinimumDeliveryOrder() {
        return minimumDeliveryOrder;
    }

    public BizStoreEntity setMinimumDeliveryOrder(int minimumDeliveryOrder) {
        this.minimumDeliveryOrder = minimumDeliveryOrder;
        return this;
    }

    public int getDeliveryRange() {
        return deliveryRange;
    }

    public BizStoreEntity setDeliveryRange(int deliveryRange) {
        this.deliveryRange = deliveryRange;
        return this;
    }

    public Set<String> getStoreServiceImages() {
        return storeServiceImages;
    }

    public BizStoreEntity setStoreServiceImages(Set<String> storeServiceImages) {
        this.storeServiceImages = storeServiceImages;
        return this;
    }

    public BizStoreEntity addStoreServiceImage(String storeServiceImage) {
        this.storeServiceImages.add(storeServiceImage);
        return this;
    }

    public Set<String> getStoreInteriorImages() {
        return storeInteriorImages;
    }

    public BizStoreEntity setStoreInteriorImages(Set<String> storeInteriorImages) {
        this.storeInteriorImages = storeInteriorImages;
        return this;
    }

    public List<PaymentModeEnum> getPaymentModes() {
        return paymentModes;
    }

    public BizStoreEntity setPaymentModes(List<PaymentModeEnum> paymentModes) {
        this.paymentModes = paymentModes;
        return this;
    }

    public List<DeliveryModeEnum> getDeliveryModes() {
        return deliveryModes;
    }

    public BizStoreEntity setDeliveryModes(List<DeliveryModeEnum> deliveryModes) {
        this.deliveryModes = deliveryModes;
        return this;
    }

    public List<AmenityEnum> getAmenities() {
        return amenities;
    }

    public BizStoreEntity setAmenities(List<AmenityEnum> amenities) {
        this.amenities = amenities;
        return this;
    }

    public List<FacilityEnum> getFacilities() {
        return facilities;
    }

    public BizStoreEntity setFacilities(List<FacilityEnum> facilities) {
        this.facilities = facilities;
        return this;
    }

    public boolean isRemoteJoin() {
        return remoteJoin;
    }

    public BizStoreEntity setRemoteJoin(boolean remoteJoin) {
        this.remoteJoin = remoteJoin;
        return this;
    }

    public boolean isAllowLoggedInUser() {
        return allowLoggedInUser;
    }

    public BizStoreEntity setAllowLoggedInUser(boolean allowLoggedInUser) {
        this.allowLoggedInUser = allowLoggedInUser;
        return this;
    }

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public BizStoreEntity setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
        return this;
    }

    public boolean isEnabledPayment() {
        return enabledPayment;
    }

    public BizStoreEntity setEnabledPayment(boolean enabledPayment) {
        this.enabledPayment = enabledPayment;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public BizStoreEntity setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getCancellationPrice() {
        return cancellationPrice;
    }

    public BizStoreEntity setCancellationPrice(int cancellationPrice) {
        this.cancellationPrice = cancellationPrice;
        return this;
    }

    public ServicePaymentEnum getServicePayment() {
        return servicePayment;
    }

    public BizStoreEntity setServicePayment(ServicePaymentEnum servicePayment) {
        this.servicePayment = servicePayment;
        return this;
    }

    public int getFreeWithinDays() {
        return freeWithinDays;
    }

    public BizStoreEntity setFreeWithinDays(int freeWithinDays) {
        this.freeWithinDays = freeWithinDays;
        return this;
    }

    public String getScheduledTaskId() {
        return scheduledTaskId;
    }

    public BizStoreEntity setScheduledTaskId(String scheduledTaskId) {
        this.scheduledTaskId = scheduledTaskId;
        return this;
    }

    @Transient
    public String getTopic() {
        Assert.notNull(countryShortName, "Country short name null for bizStore id=" + id);
        return countryShortName + UNDER_SCORE + codeQR;
    }

    @Transient
    public String getCodeQRInALink() {
        return "https://q.noqapp.com/" + codeQR + "/q.htm";
    }

    @Transient
    public LatLng getLatLng() {
        return CommonUtil.getLatLng(coordinate);
    }

    @Transient
    public List<StoreHourEntity> getStoreHours() {
        //TODO(hth) add check when storeHours is empty or null
        return storeHours;
    }

    @Transient
    public void setStoreHours(List<StoreHourEntity> storeHours) {
        this.storeHours = storeHours;
    }

    @Transient
    public int getTokenAvailableFrom(DayOfWeek dayOfWeek) {
        return storeHours.get(dayOfWeek.getValue() - 1).getTokenAvailableFrom();
    }

    @Transient
    public int getStartHour(DayOfWeek dayOfWeek) {
        return storeHours.get(dayOfWeek.getValue() - 1).getStartHour();
    }

    @Transient
    public int getTokenNotAvailableFrom(DayOfWeek dayOfWeek) {
        return storeHours.get(dayOfWeek.getValue() - 1).getTokenNotAvailableFrom();
    }

    @Transient
    public int getEndHour(DayOfWeek dayOfWeek) {
        return storeHours.get(dayOfWeek.getValue() - 1).getEndHour();
    }

    @Transient
    public float getRatingFormatted() {
        return MathUtil.roundFloat(rating);
    }

    @Transient
    public String getAverageServiceTimeFormatted() {
        String time;
        if (averageServiceTime <= 0) {
            time = "N/A";
        } else {
            long seconds = averageServiceTime / 1000;
            if (seconds > 60) {
                time = String.valueOf(seconds / 60) + " min(s)";
            } else {
                time = String.valueOf(seconds) + " sec(s)";
            }
        }

        LOG.debug("Average Service time {} in milliSeconds={}", time, averageServiceTime);
        return time;
    }

    @Transient
    public GeoPointOfQ getGeoPointOfQ() {
        /* Latitude and then Longitude. */
        return new GeoPointOfQ(coordinate[1], coordinate[0]);
    }

    @Transient
    public GeoPoint getGeoPoint() {
        /* Longitude and then Latitude. */
        return new GeoPoint(coordinate[1], coordinate[0]);
    }

    @Transient
    public Point getPoint() {
        return new Point(coordinate[0], coordinate[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BizStoreEntity that = (BizStoreEntity) o;
        return Objects.equals(codeQR, that.codeQR);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeQR);
    }

    public String getCodeQRInBase64() {
        return Base64.getEncoder().encodeToString(codeQR.getBytes());
    }
}
