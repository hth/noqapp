package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 8/12/18 1:52 PM
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
public class JsonPreferredBusiness extends AbstractDomain {

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("bc")
    private String bizCategoryId;

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

    @JsonProperty("a")
    private boolean active;

    public JsonPreferredBusiness(BizStoreEntity bizStore) {
        this.bizStoreId = bizStore.getId();
        this.codeQR = bizStore.getCodeQR();
        this.geoHash = bizStore.getGeoPoint().getGeohash();
        this.businessName = bizStore.getBizName().getBusinessName();
        this.displayName = bizStore.getDisplayName();
        this.businessType = bizStore.getBusinessType();
        this.bizCategoryId = bizStore.getBizCategoryId();
        this.storeAddress = bizStore.getAddress();
        this.area = bizStore.getArea();
        this.town = bizStore.getTown();
        this.countryShortName = bizStore.getCountryShortName();
        this.storePhone = bizStore.getPhoneFormatted();
        this.active = bizStore.isActive();
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

    public String getBusinessName() {
        return businessName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getArea() {
        return area;
    }

    public String getTown() {
        return town;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public boolean isActive() {
        return active;
    }
}
