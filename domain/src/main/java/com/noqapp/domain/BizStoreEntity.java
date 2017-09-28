package com.noqapp.domain;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.utils.CommonUtil;
import com.noqapp.utils.Formatter;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 11/23/16 4:28 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BIZ_STORE")
@CompoundIndexes (value = {
        /** Business name with address and phone makes it a unique store. */
        @CompoundIndex (name = "biz_store_ph_idx", def = "{'PH': 1}", unique = true),
        @CompoundIndex (name = "biz_store_qr_idx", def = "{'QR': 1}", unique = true),
        @CompoundIndex (name = "biz_store_wl_idx", def = "{'WL': 1}", unique = false),
        @CompoundIndex (name = "biz_store_cor_cs_idx", def = "{'COR': '2d', 'CS': 1}"),
})
public class BizStoreEntity extends BaseEntity {

    /** Field name */
    public static final String ADDRESS_FIELD_NAME = "AD";
    public static final String PHONE_FIELD_NAME = "PH";
    private static final String UNDER_SCORE = "_";

    /** Better to add a BLANK PHONE then to add nothing when biz does not have a phone number */
    @Value ("${phoneNumberBlank:000_000_0000}")
    private String phoneNumberBlank;

    @NotNull
    @Field ("AD")
    private String address;

    @Field ("TO")
    private String town;

    @Field ("DT")
    private String district;

    @Field ("ST")
    private String state;

    @Field ("SS")
    private String stateShortName;

    /* Postal code could be empty for few countries. */
    @Field ("PC")
    private String postalCode;

    @Field ("CC")
    private String country;

    @NotNull
    @Field ("CS")
    private String countryShortName;

    /* Phone number saved with country code. */
    @NotNull
    @Field ("PH")
    private String phone;

    /* To not loose user entered phone number. */
    @Field ("PR")
    private String phoneRaw;

    /* Format Longitude and then Latitude. */
    @Field ("COR")
    private double[] coordinate;

    @Field ("PI")
    private String placeId;

    @Field ("PT")
    private String[] placeType;

    @Field ("RA")
    private float rating;

    @Field ("RC")
    private int ratingCount;

    @Field("AS")
    private long averageServiceTime;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @Field ("EA")
    private boolean validatedUsingExternalAPI;

    @Field ("VC")
    private int validationCount;

    @Field ("DN")
    private String displayName;

    @Field ("QR")
    private String codeQR;

    @Field ("TZ")
    private String timeZone;

    /* Used when running cron job. */
    @Field ("QH")
    private Date queueHistory = new Date();

    //TODO(hth) Change to false after sending notification of change
    @Field ("CQ")
    private boolean changedCodeQR = false;

    @NotNull
    @Field ("AO")
    private AddressOriginEnum addressOrigin;

    @Field ("WL")
    private String webLocation;

    @Field ("RJ")
    private boolean remoteJoin = false;

    @Field ("LU")
    private boolean allowLoggedInUser = false;

    @Transient
    private List<StoreHourEntity> storeHours;

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

    public String getAddressWrappedMore() {
        return getAddressWrapped().replaceFirst(",", "<br/>");
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

    public void setAddress(String address) {
        this.address = StringUtils.strip(address);
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public void setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Remove everything other than numbers. Do the formatting on client side.
     *
     * @param phone
     */
    public void setPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            this.phone = Formatter.phoneCleanup(phoneNumberBlank);
        } else {
            this.phone = Formatter.phoneCleanup(phone);
        }
    }

    public String getPhoneFormatted() {
        return Formatter.phoneFormatter(phone, countryShortName);
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public void setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
    }

    public boolean isValidatedUsingExternalAPI() {
        return validatedUsingExternalAPI;
    }

    public void setValidatedUsingExternalAPI(boolean validatedUsingExternalAPI) {
        this.validatedUsingExternalAPI = validatedUsingExternalAPI;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String[] getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String[] placeType) {
        this.placeType = placeType;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
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

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        this.changedCodeQR = true;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Date getQueueHistory() {
        return queueHistory;
    }

    public void setQueueHistory(Date queueHistory) {
        this.queueHistory = queueHistory;
    }

    public String getWebLocation() {
        return webLocation;
    }

    public void setWebLocation(String webLocation) {
        this.webLocation = webLocation;
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

    public void setAllowLoggedInUser(boolean allowLoggedInUser) {
        this.allowLoggedInUser = allowLoggedInUser;
    }

    public AddressOriginEnum getAddressOrigin() {
        return addressOrigin;
    }

    public BizStoreEntity setAddressOrigin(AddressOriginEnum addressOrigin) {
        this.addressOrigin = addressOrigin;
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
    public int getEndHour(DayOfWeek dayOfWeek) {
        return storeHours.get(dayOfWeek.getValue() - 1).getEndHour();
    }
}
