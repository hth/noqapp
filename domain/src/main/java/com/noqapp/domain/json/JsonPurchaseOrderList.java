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
 * 7/27/18 3:33 PM
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
public class JsonPurchaseOrderList extends AbstractDomain {

    @JsonProperty("pos")
    private List<JsonPurchaseOrder> purchaseOrders = new ArrayList<>();

    public List<JsonPurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public JsonPurchaseOrderList setPurchaseOrders(List<JsonPurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
        return this;
    }

    @Override
    public String toString() {
        return "JsonPurchaseOrderList{" +
            "purchaseOrders=" + purchaseOrders +
            '}';
    }
}
