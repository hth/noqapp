package com.noqapp.search.elastic.domain;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 3/7/21 8:23 PM
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
public class MarketplaceElasticList extends AbstractDomain {

    @JsonProperty("marketplaces")
    private List<MarketplaceElastic> marketplaceElastics = new LinkedList<>();

    public List<MarketplaceElastic> getMarketplaceElastics() {
        return marketplaceElastics;
    }

    public MarketplaceElasticList setMarketplaceElastics(List<MarketplaceElastic> marketplaceElastics) {
        this.marketplaceElastics = marketplaceElastics;
        return this;
    }

    public MarketplaceElasticList addMarketplaceElastic(MarketplaceElastic marketplaceElastic) {
        this.marketplaceElastics.add(marketplaceElastic);
        return this;
    }
}
