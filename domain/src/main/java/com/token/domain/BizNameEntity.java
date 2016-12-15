package com.token.domain;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.NumberFormat;

import com.token.domain.types.BusinessTypeEnum;
import com.token.utils.CommonUtil;

import java.util.ArrayList;
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
@Document (collection = "BIZ_NAME")
@CompoundIndexes (value = {
        @CompoundIndex (name = "biz_name_idx", def = "{'N': 1}", unique = false),
})
public class BizNameEntity extends BaseEntity {

    @NotNull
    @Field ("N")
    private String businessName;

    @Field ("BT")
    private List<BusinessTypeEnum> businessTypes = new ArrayList<>();

    /** Better to add a BLANK PHONE then to add nothing when biz does not have a phone number */
    @Value ("${phoneNumberBlank:000_000_0000}")
    private String phoneNumberBlank;

    @NotNull
    @Field ("AD")
    private String address;

    @NotNull
    @Field ("FA")
    private String formattedAddress;

    @Field ("TO")
    private String town;

    @Field ("DT")
    private String district;

    @Field ("ST")
    private String state;

    /* Postal code could be empty for few countries. */
    @Field ("PC")
    private String postalCode;

    @Field ("CC")
    private String country;

    @NotNull
    @Field ("CS")
    private String countryShortName;

    @NotNull
    @Field ("PH")
    private String phone;

    /** Format Longitude and then Latitude. */
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

    @Field ("MS")
    private boolean multiStore = false;

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

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
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
            this.phone = CommonUtil.phoneCleanup(phoneNumberBlank);
        } else {
            this.phone = CommonUtil.phoneCleanup(phone);
        }
    }

    public String getPhoneFormatted() {
        return CommonUtil.phoneFormatter(phone, countryShortName);
    }

    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public double getLng() {
        if (null != coordinate) {
            return coordinate[0];
        } else {
            return 0.0;
        }
    }

    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public double getLat() {
        if (null != coordinate) {
            return coordinate[1];
        } else {
            return 0.0;
        }
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

    @Override
    public String toString() {
        return "BizNameEntity{" +
                "businessName='" + businessName + '\'' +
                '}';
    }
}