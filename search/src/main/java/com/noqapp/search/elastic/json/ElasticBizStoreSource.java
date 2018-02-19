package com.noqapp.search.elastic.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.search.elastic.domain.BizStoreElastic;

/**
 * hitender
 * 2/18/18 10:36 PM
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
public class ElasticBizStoreSource implements ElasticSource {

    @JsonProperty("_source")
    private BizStoreElastic bizStoreElastic;

    public BizStoreElastic getBizStoreElastic() {
        return bizStoreElastic;
    }

    public ElasticBizStoreSource setBizStoreElastic(BizStoreElastic bizStoreElastic) {
        this.bizStoreElastic = bizStoreElastic;
        return this;
    }
}
