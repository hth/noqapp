package com.noqapp.search.elastic.json;

import com.noqapp.search.elastic.domain.MarketplaceElastic;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 3/2/21 10:15 PM
 */
@SuppressWarnings ({
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
public class ElasticMarketplaceSearchSource implements ElasticSource {

    @JsonProperty("_source")
    private MarketplaceElastic marketplaceElastic;

    public MarketplaceElastic getMarketplaceElastic() {
        return marketplaceElastic;
    }

    public ElasticMarketplaceSearchSource setMarketplaceElastic(MarketplaceElastic marketplaceElastic) {
        this.marketplaceElastic = marketplaceElastic;
        return this;
    }
}
