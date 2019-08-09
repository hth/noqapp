package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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

    @JsonProperty("id")
    private String productId;

    @JsonProperty("n")
    private String productName;

    @JsonProperty("p")
    private int productPrice;

    @JsonProperty("d")
    private int productDiscount;

    @JsonProperty("i")
    private String productInfo;

    @JsonProperty("im")
    private String productImage;

    @JsonProperty("ci")
    private String storeCategoryId;

    @JsonProperty("t")
    private ProductTypeEnum productType;

    @JsonProperty("uv")
    private int unitValue;

    @JsonProperty("ps")
    private int packageSize;

    @JsonProperty("um")
    private UnitOfMeasurementEnum unitOfMeasurement;

    //TODO product info references to html location for more detail like for Medicine.
    @JsonProperty("pr")
    private String productReference;

    @JsonProperty("a")
    private boolean active;

    public String getProductId() {
        return productId;
    }

    public JsonStoreProduct setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public JsonStoreProduct setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public JsonStoreProduct setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public JsonStoreProduct setProductDiscount(int productDiscount) {
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

    public String getProductImage() {
        return productImage;
    }

    public JsonStoreProduct setProductImage(String productImage) {
        this.productImage = productImage;
        return this;
    }

    public String getStoreCategoryId() {
        return storeCategoryId;
    }

    public JsonStoreProduct setStoreCategoryId(String storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
        return this;
    }

    public ProductTypeEnum getProductType() {
        return productType;
    }

    public JsonStoreProduct setProductType(ProductTypeEnum productType) {
        this.productType = productType;
        return this;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public JsonStoreProduct setUnitValue(int unitValue) {
        this.unitValue = unitValue;
        return this;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public JsonStoreProduct setPackageSize(int packageSize) {
        this.packageSize = packageSize;
        return this;
    }

    public UnitOfMeasurementEnum getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public JsonStoreProduct setUnitOfMeasurement(UnitOfMeasurementEnum unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
        return this;
    }

    public String getProductReference() {
        return productReference;
    }

    public JsonStoreProduct setProductReference(String productReference) {
        this.productReference = productReference;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public JsonStoreProduct setActive(boolean active) {
        this.active = active;
        return this;
    }
}
