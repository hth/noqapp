package com.noqapp.view.form.marketplace;

import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.catgeory.RentalTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 1/10/21 11:52 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public class MarketplaceForm implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceForm.class);

    private String ip;
    private String countryCode;
    private String city;
    /* Format Longitude and then Latitude. */
    private double[] coordinate;

    private BusinessTypeEnum businessType;
    private boolean postingAllowed;

    private MarketplaceEntity marketplace;

    private String validateByQid;

    @Transient
    private List<BusinessTypeEnum> marketPlaces = BusinessTypeEnum.marketPlaces();

    public String getIp() {
        return ip;
    }

    public MarketplaceForm setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public MarketplaceForm setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public MarketplaceForm setCity(String city) {
        this.city = city;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public MarketplaceForm setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public MarketplaceForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public boolean isPostingAllowed() {
        return postingAllowed;
    }

    public MarketplaceForm setPostingAllowed(boolean postingAllowed) {
        this.postingAllowed = postingAllowed;
        return this;
    }

    public MarketplaceEntity getMarketplace() {
        return marketplace;
    }

    public MarketplaceForm setMarketplace(MarketplaceEntity marketplace) {
        this.marketplace = marketplace;
        return this;
    }

    public String getValidateByQid() {
        return validateByQid;
    }

    public MarketplaceForm setValidateByQid(String validateByQid) {
        this.validateByQid = validateByQid;
        return this;
    }

    public List<BusinessTypeEnum> getMarketPlaces() {
        return marketPlaces;
    }

    public MarketplaceForm setMarketPlaces(List<BusinessTypeEnum> marketPlaces) {
        this.marketPlaces = marketPlaces;
        return this;
    }
}
