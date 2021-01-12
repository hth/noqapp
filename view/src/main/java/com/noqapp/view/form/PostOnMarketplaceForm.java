package com.noqapp.view.form;

import com.noqapp.domain.market.MarketplaceEntity;
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
public class PostOnMarketplaceForm implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PostOnMarketplaceForm.class);

    private String ip;
    private String countryCode;
    private String city;
    /* Format Longitude and then Latitude. */
    private double[] coordinate;

    private BusinessTypeEnum businessType;

    private MarketplaceEntity marketplace;

    private String validateByQid;

    @Transient
    private List<BusinessTypeEnum> marketPlaces = BusinessTypeEnum.marketPlaces();

    @Transient
    private List<RentalTypeEnum> rentalTypes = new ArrayList<>(RentalTypeEnum.rentalTypes);

    public String getIp() {
        return ip;
    }

    public PostOnMarketplaceForm setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public PostOnMarketplaceForm setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public PostOnMarketplaceForm setCity(String city) {
        this.city = city;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public PostOnMarketplaceForm setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public PostOnMarketplaceForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public MarketplaceEntity getMarketplace() {
        return marketplace;
    }

    public PostOnMarketplaceForm setMarketplace(MarketplaceEntity marketplace) {
        this.marketplace = marketplace;
        return this;
    }

    public String getValidateByQid() {
        return validateByQid;
    }

    public PostOnMarketplaceForm setValidateByQid(String validateByQid) {
        this.validateByQid = validateByQid;
        return this;
    }

    public List<BusinessTypeEnum> getMarketPlaces() {
        return marketPlaces;
    }

    public PostOnMarketplaceForm setMarketPlaces(List<BusinessTypeEnum> marketPlaces) {
        this.marketPlaces = marketPlaces;
        return this;
    }

    public List<RentalTypeEnum> getRentalTypes() {
        return rentalTypes;
    }

    public PostOnMarketplaceForm setRentalTypes(List<RentalTypeEnum> rentalTypes) {
        this.rentalTypes = rentalTypes;
        return this;
    }
}
