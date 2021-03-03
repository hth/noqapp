package com.noqapp.search.elastic.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 3/2/21 11:40 PM
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
public class MarketplaceQueryString extends QueryString {

    /**
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html
     * best_fields -- default
     * most_fields
     * cross_fields
     * phrase
     * phrase_prefix
     * bool_prefix
     */
    @JsonProperty("type")
    private String type = "phrase_prefix";

    /* Query on fields below when querying. */
    @JsonProperty("fields")
    private String[] fields = new String[]
        {
            "BT",  //BusinessType
            "PP",  //Price
            "TI",  //Title
            "DS",  //Description
            "TG", //Easy to search on
            "TS", //Easy to search on
        };

    @Override
    public String getType() {
        return type;
    }

    public MarketplaceQueryString setType(String type) {
        this.type = type;
        return this;
    }

    public String[] getFields() {
        return fields;
    }

    public QueryString setFields(String[] fields) {
        this.fields = fields;
        return this;
    }
}
