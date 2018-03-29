package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 3/23/18 1:54 AM
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
public class JsonStoreProduct extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonStoreProduct.class);

    @JsonProperty("n")
    private String productName;

    @JsonProperty("p")
    private String productPrice;

    @JsonProperty("d")
    private String productDiscount;

    @JsonProperty("i")
    private String productInfo;

    @JsonProperty("ci")
    private String storeCategoryId;

    @JsonProperty("f")
    private boolean productFresh;

    //TODO product description references to html location.
    @JsonProperty("r")
    private String productReference;

    public String getProductName() {
        return productName;
    }

    public JsonStoreProduct setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public JsonStoreProduct setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public JsonStoreProduct setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
        return this;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public JsonStoreProduct setProductInfo(String productInfo) {
        this.productInfo = productInfo;
        return this;
    }

    public String getStoreCategoryId() {
        return storeCategoryId;
    }

    public JsonStoreProduct setStoreCategoryId(String storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
        return this;
    }

    public boolean isProductFresh() {
        return productFresh;
    }

    public JsonStoreProduct setProductFresh(boolean productFresh) {
        this.productFresh = productFresh;
        return this;
    }

    public String getProductReference() {
        return productReference;
    }

    public JsonStoreProduct setProductReference(String productReference) {
        this.productReference = productReference;
        return this;
    }
}
