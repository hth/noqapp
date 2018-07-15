package com.noqapp.search.elastic.dsl;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class Search extends AbstractDomain {

    @JsonProperty("query")
    private Query query;

    /* Used for pagination. */
    @JsonProperty("from")
    private int from;

    /* Limit search to size. */
    @JsonProperty("size")
    private int size;

    @JsonProperty("sort")
    private String sort;

    public Query getQuery() {
        return query;
    }

    public Search setQuery(Query query) {
        this.query = query;
        return this;
    }

    public int getFrom() {
        return from;
    }

    public Search setFrom(int from) {
        this.from = from;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Search setSize(int size) {
        this.size = size;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public Search setSort(String sort) {
        this.sort = sort;
        return this;
    }
}
