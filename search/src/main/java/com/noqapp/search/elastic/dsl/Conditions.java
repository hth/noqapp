package com.noqapp.search.elastic.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;

/**
 * Conditions are like bool,
 *
 * hitender
 * 11/23/17 1:16 AM
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
public class Conditions extends AbstractDomain {

    @JsonProperty("filter")
    private Filter filter;

    @JsonProperty("must")
    private Options options;

    public Filter getFilter() {
        return filter;
    }

    public Conditions setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public Options getOptions() {
        return options;
    }

    public Conditions setOptions(Options options) {
        this.options = options;
        return this;
    }
}
