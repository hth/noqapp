package com.noqapp.search.elastic.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 11/22/17 11:46 AM
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
public class Query extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(Query.class);

    @JsonProperty("match_all")
    private QueryString queryStringMatchAll;

    @JsonProperty("bool")
    private Conditions conditions;

    public QueryString getQueryStringMatchAll() {
        return queryStringMatchAll;
    }

    public Query setQueryStringMatchAll(QueryString queryStringMatchAll) {
        if (StringUtils.isNotBlank(queryStringMatchAll.getQuery()) || null == queryStringMatchAll.getFields()) {
            LOG.error("Match All should be blank");
            throw new RuntimeException("Match All should be blank");
        }

        this.queryStringMatchAll = queryStringMatchAll;
        return this;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public Query setConditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }
}
