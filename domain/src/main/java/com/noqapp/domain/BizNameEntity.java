package com.noqapp.domain;

import com.noqapp.domain.shared.GeoPoint;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.domain.types.BillingPlanEnum;
import com.noqapp.domain.types.BillingStatusEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.common.utils.Formatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
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
        @CompoundIndex (name = "biz_ph_idx", def = "{'PH' : 1}", unique = true)
})
public class BizNameEntity extends BaseEntity {

    @NotNull
    @Field ("N")
    private String businessName;

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

    @Field ("MS")
    private boolean multiStore = false;

    @NotNull
    @Field ("AO")
    private AddressOriginEnum addressOrigin;

    @Field("BP")
    private BillingPlanEnum billingPlan;

    /* Billing status is set when business has been approved. */
    @Field("BS")
    private BillingStatusEnum billingStatus;

    public static BizNameEntity newInstance() {
        return new BizNameEntity();
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
    public void setBusinessName(String businessName) {
        this.businessName = WordUtils.capitalizeFully(StringUtils.trim(businessName));
    }

    public List<BusinessTypeEnum> getBusinessTypes() {
        return businessTypes;
    }

    @SuppressWarnings("unused")
    public void setBusinessTypes(List<BusinessTypeEnum> businessTypes) {
        this.businessTypes = businessTypes;
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

    public boolean isMultiStore() {
        return multiStore;
    }

    public void setMultiStore(boolean multiStore) {
        this.multiStore = multiStore;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getInviteeCode() {
        return inviteeCode;
    }

    public void setInviteeCode(String inviteeCode) {
        this.inviteeCode = inviteeCode;
    }

    public AddressOriginEnum getAddressOrigin() {
        return addressOrigin;
    }

    public BizNameEntity setAddressOrigin(AddressOriginEnum addressOrigin) {
        this.addressOrigin = addressOrigin;
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
    private GeoPoint getGeoPoint() {
        /* Latitude and then Longitude. */
        return new GeoPoint(coordinate[1], coordinate[0]);
    }

    @Override
    public String toString() {
        return "BizNameEntity{" +
                "businessName='" + businessName + '\'' +
                '}';
    }
}