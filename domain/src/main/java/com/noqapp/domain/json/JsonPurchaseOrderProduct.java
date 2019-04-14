package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.PurchaseOrderProductEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 3/31/18 4:15 PM
 */
@SuppressWarnings({
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
//@JsonInclude(JsonInclude.Include.NON_NULL) /* Intentionally commented. */
public class JsonPurchaseOrderProduct extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonPurchaseOrderProduct.class);

    @JsonProperty("pi")
    private String productId;

    @JsonProperty("pn")
    private String productName;

    @JsonProperty("pp")
    private int productPrice;

    @JsonProperty("pd")
    private int productDiscount;

    @JsonProperty("pq")
    private int productQuantity;

    public String getProductId() {
        return productId;
    }

    public JsonPurchaseOrderProduct setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public JsonPurchaseOrderProduct setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public JsonPurchaseOrderProduct setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public JsonPurchaseOrderProduct setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
        return this;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public JsonPurchaseOrderProduct setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
        return this;
    }

    public static JsonPurchaseOrderProduct populate(PurchaseOrderProductEntity purchaseOrderProduct) {
        return new JsonPurchaseOrderProduct()
            .setProductId(purchaseOrderProduct.getProductId())
            .setProductName(purchaseOrderProduct.getProductName())
            .setProductPrice(purchaseOrderProduct.getProductPrice())
            .setProductDiscount(purchaseOrderProduct.getProductDiscount())
            .setProductQuantity(purchaseOrderProduct.getProductQuantity());
    }

    @Override
    public String toString() {
        return "JsonPurchaseOrderProduct{" +
            "productId='" + productId + '\'' +
            ", productName='" + productName + '\'' +
            ", productPrice=" + productPrice +
            ", productDiscount=" + productDiscount +
            ", productQuantity=" + productQuantity +
            '}';
    }
}
