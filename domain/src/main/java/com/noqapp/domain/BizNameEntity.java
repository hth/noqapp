package com.noqapp.domain;

import com.noqapp.domain.shared.GeoPointOfQ;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.BillingPlanEnum;
import com.noqapp.domain.types.BillingStatusEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.common.utils.Formatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
@Document (collection = "BIZ_NAME")
@CompoundIndexes (value = {
        @CompoundIndex (name = "biz_ph_idx", def = "{'PH' : 1}", unique = true),
        @CompoundIndex (name = "biz_qr_idx", def = "{'QR' : 1}", unique = true, background = true)
//        @CompoundIndex (name = "biz_wl_idx", def = "{'WL': 1}", unique = true),
})
public class BizNameEntity extends BaseEntity {

    @NotNull
    @Field ("N")
    private String businessName;

    @Field ("QR")
    private String codeQR;

    @Field ("BT")
    private List<BusinessTypeEnum> businessTypes = new ArrayList<>();

    /* Better to add a BLANK PHONE then to add nothing when biz does not have a phone number */
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

    @Field ("EA")
    private boolean validatedUsingExternalAPI;

    @Field ("VC")
    private int validationCount;

    @Field ("TZ")
    private String timeZone;

    @Field ("IC")
    private String inviteeCode;

    @NotNull
    @Field ("AO")
    private AddressOriginEnum addressOrigin;

    @Field ("WL")
    private String webLocation;

    @Field("BP")
    private BillingPlanEnum billingPlan;

    /* Billing status is set when business has been approved. */
    @Field("BS")
    private BillingStatusEnum billingStatus;

    @SuppressWarnings("unused")
    public BizNameEntity() {
        //Default constructor, required to keep bean happy
    }

    public BizNameEntity(String codeQR) {
        this.codeQR = codeQR;
    }

    public static BizNameEntity newInstance(String codeQR) {
        return new BizNameEntity(codeQR);
    }

    public String getBusinessName() {
        return businessName;
    }

    /**
     * Cannot: Added Capitalize Fully feature to business businessName as the businessName has to be matching with
     * business style.
     *
     * @param businessName
     */
    public BizNameEntity setBusinessName(String businessName) {
        this.businessName = StringUtils.trim(businessName);
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public BizNameEntity setCodeQR(String codeQR) {
        //There is no setter code for setting codeQR
        //throw new UnsupportedOperationException("Cannot set CodeQR");
        this.codeQR = codeQR;
        return this;
    }

    public List<BusinessTypeEnum> getBusinessTypes() {
        return businessTypes;
    }

    @SuppressWarnings("unused")
    public BizNameEntity setBusinessTypes(List<BusinessTypeEnum> businessTypes) {
        this.businessTypes = businessTypes;
        return this;
    }

    public void addBusinessType(BusinessTypeEnum businessType) {
        this.businessTypes.add(businessType);
    }

    /**
     * Escape String for Java Script.
     *
     * @return
     */
    public String getSafeJSBusinessName() {
        return StringEscapeUtils.escapeEcmaScript(businessName);
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

    public BizNameEntity setAddress(String address) {
        this.address = StringUtils.strip(address);
        return this;
    }

    public String getTown() {
        return town;
    }

    public BizNameEntity setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public BizNameEntity setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public BizNameEntity setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public BizNameEntity setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public BizNameEntity setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public BizNameEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public BizNameEntity setCountryShortName(String countryShortName) {
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
    public BizNameEntity setPhone(String phone) {
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

    public BizNameEntity setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
        return this;
    }

    public boolean isValidatedUsingExternalAPI() {
        return validatedUsingExternalAPI;
    }

    public BizNameEntity setValidatedUsingExternalAPI(boolean validatedUsingExternalAPI) {
        this.validatedUsingExternalAPI = validatedUsingExternalAPI;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public BizNameEntity setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public String getPlaceId() {
        return placeId;
    }

    public BizNameEntity setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public String[] getPlaceType() {
        return placeType;
    }

    public BizNameEntity setPlaceType(String[] placeType) {
        this.placeType = placeType;
        return this;
    }

    public int getValidationCount() {
        return validationCount;
    }

    public BizNameEntity setValidationCount(int validationCount) {
        this.validationCount = validationCount;
        return this;
    }

    public void increaseValidationCount() {
        this.validationCount += 1;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public BizNameEntity setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public String getInviteeCode() {
        return inviteeCode;
    }

    public BizNameEntity setInviteeCode(String inviteeCode) {
        this.inviteeCode = inviteeCode;
        return this;
    }

    public AddressOriginEnum getAddressOrigin() {
        return addressOrigin;
    }

    public BizNameEntity setAddressOrigin(AddressOriginEnum addressOrigin) {
        this.addressOrigin = addressOrigin;
        return this;
    }

    public String getWebLocation() {
        return webLocation;
    }

    public BizNameEntity setWebLocation(String webLocation) {
        this.webLocation = webLocation;
        return this;
    }

    public BillingPlanEnum getBillingPlan() {
        return billingPlan;
    }

    public BizNameEntity setBillingPlan(BillingPlanEnum billingPeriod) {
        this.billingPlan = billingPeriod;
        return this;
    }

    public BillingStatusEnum getBillingStatus() {
        return billingStatus;
    }

    public BizNameEntity setBillingStatus(BillingStatusEnum billingStatus) {
        this.billingStatus = billingStatus;
        return this;
    }

    @Transient
    private GeoPointOfQ getGeoPointOfQ() {
        /* Latitude and then Longitude. */
        return new GeoPointOfQ(coordinate[1], coordinate[0]);
    }

    @Transient
    private GeoPoint getGeoPoint() {
        /* Latitude and then Longitude. */
        return new GeoPoint(coordinate[1], coordinate[0]);
    }

    @Transient
    public String getCodeQRInALink() {
        return "https://q.noqapp.com/" + codeQR + "/b.htm";
    }

    @Override
    public String toString() {
        return "BizNameEntity{" +
                "businessName='" + businessName + '\'' +
                '}';
    }
}