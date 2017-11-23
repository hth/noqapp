package com.noqapp.search.elastic.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;

/**
 * Options are like must,....
 *
 * hitender
 * 11/23/17 1:17 AM
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
public class Options  extends AbstractDomain {

    @JsonProperty("multi_match")
    private QueryString queryStringMultiMatch;

    public QueryString getQueryStringMultiMatch() {
        return queryStringMultiMatch;
    }

    public Options setQueryStringMultiMatch(QueryString queryStringMultiMatch) {
        this.queryStringMultiMatch = queryStringMultiMatch;
        return this;
    }
}
