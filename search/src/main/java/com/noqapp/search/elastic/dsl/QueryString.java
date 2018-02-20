package com.noqapp.search.elastic.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;

/**
 * hitender
 * 11/22/17 4:39 PM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryString extends AbstractDomain {

    @JsonProperty("query")
    private String query;

    @JsonProperty("fields")
    private String[] fields = new String[]
            {
                    "AD", //Address
                    "DN", //Display Name
                    "BT", //Business Type
                    "CS", //Country Short Name
                    "DT", //District
                    "N",  //BusinessName
                    "SS", //State Short Name
                    "ST", //State
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
