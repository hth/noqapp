package com.noqapp.search.elastic.dsl;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class Options extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(Options.class);

    @JsonProperty("multi_match")
    private QueryString queryStringMultiMatch;

    @JsonProperty("match_all")
    private QueryString queryStringMatchAll;

    public QueryString getQueryStringMultiMatch() {
        return queryStringMultiMatch;
    }

    public Options setQueryStringMultiMatch(QueryString queryStringMultiMatch) {
        this.queryStringMultiMatch = queryStringMultiMatch;
        return this;
    }

    public QueryString getQueryStringMatchAll() {
        return queryStringMatchAll;
    }

    public Options setQueryStringMatchAll(QueryString queryStringMatchAll) {
        if (StringUtils.isNotBlank(queryStringMatchAll.getQuery()) && null != queryStringMatchAll.getFields()) {
            LOG.error("Match All should be blank");
            throw new RuntimeException("Match All should be blank");
        }

        this.queryStringMatchAll = queryStringMatchAll;
        return this;
    }
}
