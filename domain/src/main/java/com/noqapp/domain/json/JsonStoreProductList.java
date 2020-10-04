package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 10/3/20 9:28 PM
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
public class JsonStoreProductList extends AbstractDomain {

    @JsonProperty("products")
    private List<JsonStoreProduct> jsonStoreProducts = new LinkedList<>();

    public List<JsonStoreProduct> getJsonStoreProducts() {
        return jsonStoreProducts;
    }

    public JsonStoreProductList addJsonStoreProduct(JsonStoreProduct jsonStoreProduct) {
        this.jsonStoreProducts.add(jsonStoreProduct);
        return this;
    }
}
