package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private String productPrice;

    @Field("PD")
    private String productDescription;

    @Field("SC")
    private String storeCategoryId;

    @Field("PF")
    private boolean productFresh;

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

    public String getProductPrice() {
        return productPrice;
    }

    public StoreProductEntity setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public StoreProductEntity setProductDescription(String productDescription) {
        this.productDescription = productDescription;
        return this;
    }

    public String getStoreCategoryId() {
        return storeCategoryId;
    }

    public StoreProductEntity setStoreCategoryId(String storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
        return this;
    }

    public boolean isProductFresh() {
        return productFresh;
    }

    public StoreProductEntity setProductFresh(boolean productFresh) {
        this.productFresh = productFresh;
        return this;
    }

    public String getProductReference() {
        return productReference;
    }

    public StoreProductEntity setProductReference(String productReference) {
        this.productReference = productReference;
        return this;
    }
}
