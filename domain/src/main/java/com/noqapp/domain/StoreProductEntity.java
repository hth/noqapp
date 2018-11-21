package com.noqapp.domain;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonStoreProduct;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * hitender
 * 3/21/18 4:52 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "STORE_PRODUCT")
@CompoundIndexes({
        @CompoundIndex(name = "store_product_idx", def = "{'BS': 1}", unique = false)
})
public class StoreProductEntity extends BaseEntity {

    @Field("BS")
    private String bizStoreId;

    @Field("PN")
    private String productName;

    @Field("PP")
    private int productPrice;

    @Field("PD")
    private int productDiscount;

    @Field("PI")
    private String productInfo;

    @Field("SC")
    private String storeCategoryId;

    @Field("PT")
    private ProductTypeEnum productType;

    /* Like 1 kg, 200 ml, 2 kg and so on. */
    @Field("UV")
    private int unitValue;

    @Field ("UM")
    private UnitOfMeasurementEnum unitOfMeasurement;

    /* Package size is the quantity of individual items in the unit. Like 1 strip contains 10 tablets. Defaults to 1. */
    @Field("PS")
    private int packageSize;

    //TODO product description references to html location.
    @Field("PR")
    private String productReference;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public StoreProductEntity setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public StoreProductEntity setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public StoreProductEntity setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public StoreProductEntity setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
        return this;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public StoreProductEntity setProductInfo(String productInfo) {
        this.productInfo = productInfo;
        return this;
    }

    public String getStoreCategoryId() {
        return storeCategoryId;
    }

    public StoreProductEntity setStoreCategoryId(String storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
        return this;
    }

    public ProductTypeEnum getProductType() {
        return productType;
    }

    public StoreProductEntity setProductType(ProductTypeEnum productType) {
        this.productType = productType;
        return this;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public StoreProductEntity setUnitValue(int unitValue) {
        this.unitValue = unitValue;
        return this;
    }

    public UnitOfMeasurementEnum getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public StoreProductEntity setUnitOfMeasurement(UnitOfMeasurementEnum unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
        return this;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public StoreProductEntity setPackageSize(int packageSize) {
        this.packageSize = packageSize;
        return this;
    }

    public String getProductReference() {
        return productReference;
    }

    public StoreProductEntity setProductReference(String productReference) {
        this.productReference = productReference;
        return this;
    }


    /* On Web. */
    @SuppressWarnings("unused")
    @Transient
    public String getDisplayPrice() {
        return new BigDecimal(productPrice).divide(new BigDecimal(100), MathContext.DECIMAL64).toString();
    }

    /* On Web. */
    @SuppressWarnings("unused")
    @Transient
    public String getDisplayDiscount() {
        return new BigDecimal(productDiscount).divide(new BigDecimal(100), MathContext.DECIMAL64).toString();
    }

    @Transient
    public String toCommaSeparatedString() {
        return id + "," + bizStoreId + ","
            + getDisplayPrice() + ","
            + productName + ","
            + (StringUtils.isBlank(productInfo) ? "" : productInfo) + ","
            + (StringUtils.isBlank(storeCategoryId) ? "" : storeCategoryId) + ","
            + (productType == null ? "" : productType.name()) + ","
            + unitValue + ","
            + (unitOfMeasurement == null ? "" : unitOfMeasurement.getName()) + ",";
    }

    @Transient
    @Mobile
    public static StoreProductEntity parseJsonStoreProduct(JsonStoreProduct jsonStoreProduct) {
        StoreProductEntity storeProduct = new StoreProductEntity()
            //BizStoreId
            .setProductName(jsonStoreProduct.getProductName())
            .setProductPrice(jsonStoreProduct.getProductPrice())
            .setProductDiscount(jsonStoreProduct.getProductDiscount())
            .setProductInfo(jsonStoreProduct.getProductInfo())
            .setStoreCategoryId(jsonStoreProduct.getStoreCategoryId())
            .setProductType(jsonStoreProduct.getProductType())
            .setUnitValue(jsonStoreProduct.getUnitValue())
            .setUnitOfMeasurement(jsonStoreProduct.getUnitOfMeasurement())
            .setPackageSize(jsonStoreProduct.getPackageSize());

        storeProduct.setId(jsonStoreProduct.getProductId());
        return storeProduct;
    }

    @Transient
    @Mobile
    public void populateWithExistingStoreProduct(StoreProductEntity found) {
        this
            .setBizStoreId(found.getBizStoreId())
            .setVersion(found.getVersion());
    }

    @Override
    public String toString() {
        return "StoreProductEntity{" +
            "bizStoreId='" + bizStoreId + '\'' +
            ", productName='" + productName + '\'' +
            ", productPrice=" + productPrice +
            ", productDiscount=" + productDiscount +
            ", productInfo='" + productInfo + '\'' +
            ", storeCategoryId='" + storeCategoryId + '\'' +
            ", productType=" + productType +
            ", unitValue=" + unitValue +
            ", unitOfMeasurement=" + unitOfMeasurement +
            ", packageSize=" + packageSize +
            ", productReference='" + productReference + '\'' +
            ", id='" + id + '\'' +
            '}';
    }
}
