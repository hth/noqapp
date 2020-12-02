package com.noqapp.search.elastic.dsl;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 11/22/17 4:39 PM
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
public class QueryString extends AbstractDomain {

    /* Searching for elements. */
    @JsonProperty("query")
    private String query;

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
            "PH"  //Phone
        };

    public String getQuery() {
        return query;
    }

    public QueryString setQuery(String query) {
        this.query = query;
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
