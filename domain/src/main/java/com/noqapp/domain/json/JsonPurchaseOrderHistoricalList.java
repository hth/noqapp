package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 10/5/18 11:08 AM
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
public class JsonPurchaseOrderHistoricalList extends AbstractDomain {

    @JsonProperty("pos")
    private List<JsonPurchaseOrderHistorical> jsonPurchaseOrderHistoricals = new ArrayList<>();

    public List<JsonPurchaseOrderHistorical> getJsonPurchaseOrderHistoricals() {
        return jsonPurchaseOrderHistoricals;
    }

    public JsonPurchaseOrderHistoricalList setJsonPurchaseOrderHistoricals(List<JsonPurchaseOrderHistorical> jsonPurchaseOrderHistoricals) {
        this.jsonPurchaseOrderHistoricals = jsonPurchaseOrderHistoricals;
        return this;
    }

    public JsonPurchaseOrderHistoricalList addJsonPurchaseOrderHistorical(JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical) {
        this.jsonPurchaseOrderHistoricals.add(jsonPurchaseOrderHistorical);
        return this;
    }
}
