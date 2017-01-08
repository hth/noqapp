package com.token.domain;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.util.Assert;

import com.token.utils.CommonUtil;

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
        @CompoundIndex (name = "biz_store_idx", def = "{'AD': 1, 'PH': 1}", unique = true),
        @CompoundIndex (name = "biz_store_qr_idx", def = "{'QR': 1}", unique = true),
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

    @Field ("PR")
    private float placeRating;

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

    @Field ("TF")
    private int tokenAvailableFrom;

    @Field ("SH")
    private int startHour;

    @Field ("EH")
    private int endHour;

    //TODO Change to false after sending notification of change
    @Field ("CQ")
    private boolean changedCodeQR = false;

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

    public float getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(float placeRating) {
        this.placeRating = placeRating;
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

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public void setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
    }

    @Transient
    public String getTopic() {
        Assert.notNull(countryShortName, "Country short name null for bizStore id=" + id);
        return countryShortName + UNDER_SCORE + codeQR;
    }
}
