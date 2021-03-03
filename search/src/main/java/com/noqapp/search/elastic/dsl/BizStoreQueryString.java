package com.noqapp.search.elastic.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 3/2/21 11:30 PM
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
public class BizStoreQueryString extends QueryString {

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
    private String type = "best_fields";

    /* Query on fields below when querying. */
    @JsonProperty("fields")
    private String[] fields = new String[]
        {
            "SA", //Store Address
            "DN", //Display Name
            "BT", //Business Type
            "BC", //Business Category defined by businesses
            "DT", //District
            "N",  //BusinessName
            "ST", //State
            "AR", //Area is smaller than Town
            "TO", //Town
            "TG"  //Easy to search on
        };

    @Override
    public String getType() {
        return type;
    }

    public BizStoreQueryString setType(String type) {
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
