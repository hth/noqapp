package com.noqapp.domain;

import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.beans.Transient;
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
        @CompoundIndex(name = "store_product_idx", def = "{'BZ': 1}", unique = false)
})
public class StoreProductEntity extends BaseEntity {

    @Field("BZ")
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

    @Field ("UM")
    private UnitOfMeasurementEnum unitOfMeasurement;

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

    public UnitOfMeasurementEnum getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public StoreProductEntity setUnitOfMeasurement(UnitOfMeasurementEnum unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
        return this;
    }

    public String getProductReference() {
        return productReference;
    }

    public StoreProductEntity setProductReference(String productReference) {
        this.productReference = productReference;
        return this;
    }

    @Transient
    public String getDisplayPrice() {
        return new BigDecimal(productPrice).divide(new BigDecimal(100), MathContext.DECIMAL64).toString();
    }

    @Transient
    public String getDisplayDiscount() {
        return new BigDecimal(productDiscount).divide(new BigDecimal(100), MathContext.DECIMAL64).toString();
    }
}
