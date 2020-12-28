package com.noqapp.search.elastic.domain;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.json.JsonNameDatePair;
import com.noqapp.domain.shared.GeoPointOfQ;
import com.noqapp.domain.types.AccreditationEnum;
import com.noqapp.domain.types.AmenityEnum;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.FacilityEnum;
import com.noqapp.domain.types.SupportedDeliveryEnum;
import com.noqapp.domain.types.SupportedPaymentEnum;
import com.noqapp.domain.types.WalkInStateEnum;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.search.elastic.helper.BusinessImageHolder;
import com.noqapp.search.elastic.helper.DomainConversion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Json for elastic search.
 * <p>
 * User: hitender
 * Date: 11/15/17 4:28 PM
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
public class BizStoreElastic extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreElastic.class);

    public static final String TYPE = "BIZ_STORE".toLowerCase();
    public static final String INDEX = ElasticsearchClientConfiguration.INDEX + UNDER_SCORE + TYPE;

    @JsonIgnore
    private String id;

    @JsonProperty("N")
    private String businessName;

    @JsonProperty("BT")
    BusinessTypeEnum businessType;

    @JsonProperty("BC")
    private String bizCategoryName;

    @Transient
    @JsonProperty("BDI")
    private String bizCategoryDisplayImage;

    @JsonProperty("BCI")
    String bizCategoryId;

    @JsonProperty("SA")
    String address;

    @JsonProperty("AR")
    private String area;

    @JsonProperty("TO")
    private String town;

    @JsonProperty("DT")
    private String district;

    @JsonProperty("ST")
    private String state;

    @JsonProperty("SS")
    private String stateShortName;

    /* Postal code could be empty for few countries. */
    @JsonProperty("PC")
    private String postalCode;

    @JsonProperty("CC")
    private String country;

    @JsonProperty("CS")
    private String countryShortName;

    /* Phone number saved with country code. */
    @JsonProperty("PH")
    private String phone;

    /* To not loose user entered phone number. */
    @JsonProperty("PR")
    private String phoneRaw;

    @JsonProperty("COR")
    private GeoPointOfQ geoPointOfQ;

    @JsonProperty("PI")
    String placeId;

    @JsonProperty("PT")
    private String[] placeType;

    @JsonProperty("RA")
    private float rating;

    @JsonProperty("RC")
    private int ratingCount;

    @JsonProperty("BID")
    String bizNameId;

    @JsonProperty("DN")
    private String displayName;

    @JsonProperty("QR")
    private String codeQR;

    @JsonProperty("TZ")
    private String timeZone;

    @JsonProperty("GH")
    private String geoHash;

    @JsonProperty("WL")
    private String webLocation;

    @JsonProperty("FF")
    private String famousFor;

    @JsonProperty("DI")
    private String displayImage;

    @JsonProperty("SH")
    private List<StoreHourElastic> storeHourElasticList = new ArrayList<>();

    @JsonProperty("EP")
    private boolean enabledPayment = false;

    @JsonProperty("PP")
    private int productPrice;

    @JsonProperty("WS")
    private WalkInStateEnum walkInState;

    @JsonProperty("PS")
    private AppointmentStateEnum appointmentState;

    @JsonProperty("PD")
    private int appointmentDuration;

    @JsonProperty("PF")
    private int appointmentOpenHowFar;

    @Transient
    @JsonProperty("AP")
    private Set<SupportedPaymentEnum> acceptedPayments;

    @Transient
    @JsonProperty("AD")
    private Set<SupportedDeliveryEnum> acceptedDeliveries;

    @Transient
    @JsonProperty("BI")
    private Set<String> bizServiceImages = new LinkedHashSet<>();

    @Transient
    @JsonProperty("AM")
    private List<AmenityEnum> amenities = new LinkedList<>();

    @Transient
    @JsonProperty("FA")
    private List<FacilityEnum> facilities = new LinkedList<>();

    @Transient
    @JsonProperty("AC")
    private List<AccreditationEnum> accreditation = new LinkedList<>() {{
        if (BusinessTypeEnum.DO == businessType) {
            add(AccreditationEnum.NABHP);
        }
    }};

    @Transient
    @JsonProperty("FS")
    private Date freshStockArrivalDate;

    /** WP is populated when the BT is of type BusinessTypeEnum.DO. */
    @Transient
    @JsonProperty("WP")
    private String webProfileId;

    /** ED is populated when the BT is of type BusinessTypeEnum.DO. */
    @Transient
    @JsonProperty("ED")
    private List<JsonNameDatePair> education;

    /** This field is normally used in level-up condition, other times its anyway deleted/removed from elastic. */
    @Transient
    @JsonProperty("A")
    private boolean active = true;

    public String getId() {
        return id;
    }

    public BizStoreElastic setId(String id) {
        this.id = id;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public BizStoreElastic setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public BizStoreElastic setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBizCategoryName() {
        return bizCategoryName;
    }

    public BizStoreElastic setBizCategoryName(String bizCategoryName) {
        this.bizCategoryName = bizCategoryName;
        return this;
    }

    public String getBizCategoryDisplayImage() {
        return bizCategoryDisplayImage;
    }

    public BizStoreElastic setBizCategoryDisplayImage(String bizCategoryDisplayImage) {
        this.bizCategoryDisplayImage = bizCategoryDisplayImage;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public BizStoreElastic setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public BizStoreElastic setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getArea() {
        return area;
    }

    public BizStoreElastic setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public BizStoreElastic setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public BizStoreElastic setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public BizStoreElastic setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public BizStoreElastic setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public BizStoreElastic setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public BizStoreElastic setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public BizStoreElastic setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public BizStoreElastic setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public BizStoreElastic setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
        return this;
    }

    public GeoPointOfQ getGeoPointOfQ() {
        return geoPointOfQ;
    }

    public BizStoreElastic setGeoPointOfQ(GeoPointOfQ geoPointOfQ) {
        this.geoPointOfQ = geoPointOfQ;
        return this;
    }

    public String getPlaceId() {
        return placeId;
    }

    public BizStoreElastic setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public String[] getPlaceType() {
        return placeType;
    }

    public BizStoreElastic setPlaceType(String[] placeType) {
        this.placeType = placeType;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public BizStoreElastic setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public BizStoreElastic setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public BizStoreElastic setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BizStoreElastic setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public BizStoreElastic setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public BizStoreElastic setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public BizStoreElastic setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getWebLocation() {
        return webLocation;
    }

    public BizStoreElastic setWebLocation(String webLocation) {
        this.webLocation = webLocation;
        return this;
    }

    public String getFamousFor() {
        return famousFor;
    }

    public BizStoreElastic setFamousFor(String famousFor) {
        this.famousFor = famousFor;
        return this;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public BizStoreElastic setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
        return this;
    }

    public List<StoreHourElastic> getStoreHourElasticList() {
        return storeHourElasticList;
    }

    public BizStoreElastic setStoreHourElasticList(List<StoreHourElastic> storeHourElasticList) {
        this.storeHourElasticList = storeHourElasticList;
        return this;
    }

    public boolean isEnabledPayment() {
        return enabledPayment;
    }

    public BizStoreElastic setEnabledPayment(boolean enabledPayment) {
        this.enabledPayment = enabledPayment;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public BizStoreElastic setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public WalkInStateEnum getWalkInState() {
        return walkInState;
    }

    public BizStoreElastic setWalkInState(WalkInStateEnum walkInState) {
        this.walkInState = walkInState;
        return this;
    }

    public AppointmentStateEnum getAppointmentState() {
        return appointmentState;
    }

    public BizStoreElastic setAppointmentState(AppointmentStateEnum appointmentState) {
        this.appointmentState = appointmentState;
        return this;
    }

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public BizStoreElastic setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
        return this;
    }

    public int getAppointmentOpenHowFar() {
        return appointmentOpenHowFar;
    }

    public BizStoreElastic setAppointmentOpenHowFar(int appointmentOpenHowFar) {
        this.appointmentOpenHowFar = appointmentOpenHowFar;
        return this;
    }

    public Set<SupportedPaymentEnum> getAcceptedPayments() {
        return acceptedPayments;
    }

    public BizStoreElastic setAcceptedPayments(Set<SupportedPaymentEnum> acceptedPayments) {
        this.acceptedPayments = acceptedPayments;
        return this;
    }

    public Set<SupportedDeliveryEnum> getAcceptedDeliveries() {
        return acceptedDeliveries;
    }

    public BizStoreElastic setAcceptedDeliveries(Set<SupportedDeliveryEnum> acceptedDeliveries) {
        this.acceptedDeliveries = acceptedDeliveries;
        return this;
    }

    public Set<String> getBizServiceImages() {
        return bizServiceImages;
    }

    public BizStoreElastic setBizServiceImages(Set<String> bizServiceImages) {
        this.bizServiceImages = bizServiceImages;
        return this;
    }

    public List<AmenityEnum> getAmenities() {
        return amenities;
    }

    public BizStoreElastic setAmenities(List<AmenityEnum> amenities) {
        this.amenities = amenities;
        return this;
    }

    public List<FacilityEnum> getFacilities() {
        return facilities;
    }

    public BizStoreElastic setFacilities(List<FacilityEnum> facilities) {
        this.facilities = facilities;
        return this;
    }

    public List<AccreditationEnum> getAccreditation() {
        return accreditation;
    }

    public BizStoreElastic setAccreditation(List<AccreditationEnum> accreditation) {
        this.accreditation = accreditation;
        return this;
    }

    public Date getFreshStockArrivalDate() {
        return freshStockArrivalDate;
    }

    public BizStoreElastic setFreshStockArrivalDate(Date freshStockArrivalDate) {
        this.freshStockArrivalDate = freshStockArrivalDate;
        return this;
    }

    public String getWebProfileId() {
        return webProfileId;
    }

    public BizStoreElastic setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public BizStoreElastic setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public boolean isActive() {
        switch (businessType.getMessageOrigin()) {
            case Q:
                if (walkInState != WalkInStateEnum.E && appointmentState == AppointmentStateEnum.O) {
                    active = false;
                }
                return active;
            case O:
            default:
                return active;
        }
    }

    public BizStoreElastic setActive(boolean active) {
        this.active = active;
        return this;
    }

    /**
     * Note: This is missing Elastic Id. Should not be used for persistence in Elastic.
     *
     * @param bizStore
     * @return
     */
    @Transient
    public static BizStoreElastic getThisFromBizStore(BizStoreEntity bizStore) {
        /* Image populated here. */
        BusinessImageHolder businessImageHolder = DomainConversion.populateBizAndStoreImages(bizStore);

        BizStoreElastic bizStoreElastic = new BizStoreElastic()
            //.setId missing intentionally
            .setBusinessName(bizStore.getBizName().getBusinessName())
            .setBusinessType(bizStore.getBusinessType())
            .setBizCategoryId(bizStore.getBizCategoryId())
            .setAddress(bizStore.getAddress())
            .setArea(bizStore.getArea())
            .setTown(bizStore.getTown())
            .setDistrict(bizStore.getDistrict())
            .setState(bizStore.getState())
            .setStateShortName(bizStore.getStateShortName())
            .setPostalCode(bizStore.getPostalCode())
            .setCountry(bizStore.getCountry())
            .setCountryShortName(bizStore.getCountryShortName())
            .setPhone(bizStore.getPhone())
            .setPhoneRaw(bizStore.getPhoneRaw())
            .setGeoPointOfQ(bizStore.getGeoPointOfQ())
            .setPlaceId(bizStore.getPlaceId())
            .setPlaceType(bizStore.getPlaceType())
            .setRating(bizStore.getRating())
            .setRatingCount(bizStore.getReviewCount())
            .setBizNameId(bizStore.getBizName().getId())
            .setDisplayName(bizStore.getDisplayName())
            .setEnabledPayment(bizStore.isEnabledPayment())
            .setProductPrice(bizStore.getProductPrice())
            .setWalkInState(bizStore.getWalkInState())
            .setAppointmentState(bizStore.getAppointmentState())
            .setAppointmentDuration(bizStore.getAppointmentDuration())
            .setAppointmentOpenHowFar(bizStore.getAppointmentOpenHowFar())
            .setAcceptedPayments(bizStore.getAcceptedPayments())
            .setAcceptedDeliveries(bizStore.getAcceptedDeliveries())
            .setCodeQR(bizStore.getCodeQR())
            .setTimeZone(bizStore.getTimeZone())
            .setGeoHash(bizStore.getGeoPoint().getGeohash())
            .setWebLocation(bizStore.getWebLocation())
            .setFamousFor(bizStore.getFamousFor())
            .setDisplayImage(businessImageHolder.getBannerImage())
            .setBizServiceImages(businessImageHolder.getServiceImages())
            .setAmenities(bizStore.getAmenities())
            .setFacilities(bizStore.getFacilities())
            .setFreshStockArrivalDate(bizStore.getFreshStockArrivalDate())
            .setActive(bizStore.isActive());

        switch (bizStore.getBusinessType()) {
            case CD:
            case CDQ:
                bizStoreElastic.setAddress(FileUtil.DASH);
                bizStoreElastic.setArea(FileUtil.DASH);
                bizStoreElastic.setTown(FileUtil.DASH);
            default:
                //Do nothing
        }

        return bizStoreElastic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BizStoreElastic that = (BizStoreElastic) o;

        switch (businessType) {
            case DO:
            case HS:
                return Objects.equals(bizNameId, that.bizNameId);
            case BK:
            case CD:
            case CDQ:
                return Objects.equals(bizNameId, that.bizNameId) &&
                    Objects.equals(placeId, that.placeId);
            default:
                return Objects.equals(bizNameId, that.bizNameId) &&
                    Objects.equals(bizCategoryId, that.bizCategoryId) &&
                    Objects.equals(address, that.address);
        }
    }

    @Override
    public int hashCode() {
        switch (businessType) {
            case DO:
            case HS:
                return Objects.hash(bizNameId);
            case BK:
            case CD:
            case CDQ:
                return Objects.hash(bizNameId, placeId);
            default:
                return Objects.hash(bizNameId, bizCategoryId, address);
        }
    }

    @Override
    public String toString() {
        return "BizStoreElastic{" +
            "id='" + id + '\'' +
            ", businessName='" + businessName + '\'' +
            ", businessType=" + businessType +
            ", bizCategoryName='" + bizCategoryName + '\'' +
            ", bizCategoryDisplayImage='" + bizCategoryDisplayImage + '\'' +
            ", bizCategoryId='" + bizCategoryId + '\'' +
            ", address='" + address + '\'' +
            ", area='" + area + '\'' +
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
            ", geoHash='" + geoHash + '\'' +
            ", webLocation='" + webLocation + '\'' +
            ", famousFor='" + famousFor + '\'' +
            ", displayImage='" + displayImage + '\'' +
            ", storeHourElasticList=" + storeHourElasticList +
            ", bizServiceImages=" + bizServiceImages +
            ", amenities=" + amenities +
            ", facilities=" + facilities +
            ", webProfileId='" + webProfileId + '\'' +
            ", education=" + education +
            '}';
    }
}
