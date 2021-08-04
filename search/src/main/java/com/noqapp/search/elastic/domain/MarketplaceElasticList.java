package com.noqapp.search.elastic.domain;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.BusinessTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.annotation.Transient;

import java.util.Collection;
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

    @Transient
    @JsonProperty("si")
    private String scrollId;

    @Transient
    @JsonProperty("from")
    private int from;

    @Transient
    @JsonProperty("size")
    private int size;

    @JsonProperty("cityName")
    private String cityName;

    @JsonProperty("result")
    private Collection<MarketplaceElastic> marketplaceElastics = new LinkedList<>();

    @JsonProperty("bt")
    private BusinessTypeEnum searchedOnBusinessType;

    public String getScrollId() {
        return scrollId;
    }

    public MarketplaceElasticList setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

    public int getFrom() {
        return from;
    }

    public MarketplaceElasticList setFrom(int from) {
        this.from = from;
        return this;
    }

    public int getSize() {
        return size;
    }

    public MarketplaceElasticList setSize(int size) {
        this.size = size;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public MarketplaceElasticList setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public Collection<MarketplaceElastic> getMarketplaceElastics() {
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

    public BusinessTypeEnum getSearchedOnBusinessType() {
        return searchedOnBusinessType;
    }

    public MarketplaceElasticList setSearchedOnBusinessType(BusinessTypeEnum searchedOnBusinessType) {
        this.searchedOnBusinessType = searchedOnBusinessType;
        return this;
    }
}
