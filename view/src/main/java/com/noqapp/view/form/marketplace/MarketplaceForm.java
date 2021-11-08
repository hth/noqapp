package com.noqapp.view.form.marketplace;

import com.noqapp.common.utils.MathUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.catgeory.MarketplaceRejectReasonEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.math.BigDecimal;
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

    /** For validating marketplace post. */
    private ScrubbedInput marketplaceId;
    private ActionTypeEnum actionType;
    private MarketplaceRejectReasonEnum marketplaceRejectReason;
    /** For validating marketplace post ends. */

    private String ip;
    private String countryCode;
    private String city;
    /* Format Longitude and then Latitude. */
    private double[] coordinate;

    private String listPrice;
    private BusinessTypeEnum businessType;
    private boolean postingAllowed;

    private MarketplaceEntity marketplace;

    private String validateByQid;

    @Transient
    private List<BusinessTypeEnum> marketPlaces = BusinessTypeEnum.marketPlaces();

    @Transient
    private List<MarketplaceRejectReasonEnum> marketplaceRejectReasons = new ArrayList<>(MarketplaceRejectReasonEnum.marketplaceRejectReasons);

    public ScrubbedInput getMarketplaceId() {
        return marketplaceId;
    }

    public MarketplaceForm setMarketplaceId(ScrubbedInput marketplaceId) {
        this.marketplaceId = marketplaceId;
        return this;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public MarketplaceForm setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
        return this;
    }

    public MarketplaceRejectReasonEnum getMarketplaceRejectReason() {
        return marketplaceRejectReason;
    }

    public MarketplaceForm setMarketplaceRejectReason(MarketplaceRejectReasonEnum marketplaceRejectReason) {
        this.marketplaceRejectReason = marketplaceRejectReason;
        return this;
    }

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

    public String getListPrice() {
        return listPrice;
    }

    public MarketplaceForm setListPrice(String listPrice) {
        this.listPrice = listPrice;
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

    public List<MarketplaceRejectReasonEnum> getMarketplaceRejectReasons() {
        return marketplaceRejectReasons;
    }

    public MarketplaceForm setMarketplaceRejectReasons(List<MarketplaceRejectReasonEnum> marketplaceRejectReasons) {
        this.marketplaceRejectReasons = marketplaceRejectReasons;
        return this;
    }

    @Transient
    public String getListPriceForDB() {
        return MathUtil.correctPriceForDB(listPrice);
    }

    @Transient
    public boolean isListPriceValid() {
        return new BigDecimal(listPrice).intValue() < 1;
    }
}
