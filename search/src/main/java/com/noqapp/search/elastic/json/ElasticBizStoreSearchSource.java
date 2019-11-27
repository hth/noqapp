package com.noqapp.search.elastic.json;

import com.noqapp.search.elastic.domain.BizStoreSearchElastic;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2019-01-24 18:31
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
public class ElasticBizStoreSearchSource implements ElasticSource {

    @JsonProperty("_source")
    private BizStoreSearchElastic bizStoreSearchElastic;

    public BizStoreSearchElastic getBizStoreSearchElastic() {
        return bizStoreSearchElastic;
    }

    public ElasticBizStoreSearchSource setBizStoreSearchElastic(BizStoreSearchElastic bizStoreSearchElastic) {
        this.bizStoreSearchElastic = bizStoreSearchElastic;
        return this;
    }
}
