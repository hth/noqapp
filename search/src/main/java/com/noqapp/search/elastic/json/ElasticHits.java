package com.noqapp.search.elastic.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2/18/18 10:20 PM
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
public class ElasticHits {

    @JsonProperty("hits")
    private List<ElasticBizStoreSource> elasticSources = new LinkedList<>();

    public List<ElasticBizStoreSource> getElasticSources() {
        return elasticSources;
    }

    public ElasticHits setElasticSources(List<ElasticBizStoreSource> elasticSources) {
        this.elasticSources = elasticSources;
        return this;
    }
}
