package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.StoreProductEntity;

import java.util.List;
import java.util.Map;

/**
 * hitender
 * 3/21/18 5:28 PM
 */
public class StoreProductForm {
    private ScrubbedInput displayName;

    private ScrubbedInput bizStoreId;
    private ScrubbedInput storeProductId;
    private ScrubbedInput productName;
    private ScrubbedInput productPrice;
    private ScrubbedInput productInfo;
    private ScrubbedInput storeCategoryId;
    private boolean productFresh;

    private Map<String, String> categories;
    private List<StoreProductEntity> storeProducts;

    public ScrubbedInput getDisplayName() {
        return displayName;
    }

    public StoreProductForm setDisplayName(ScrubbedInput displayName) {
        this.displayName = displayName;
        return this;
    }

    public ScrubbedInput getBizStoreId() {
        return bizStoreId;
    }

    public StoreProductForm setBizStoreId(ScrubbedInput bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public ScrubbedInput getStoreProductId() {
        return storeProductId;
    }

    public StoreProductForm setStoreProductId(ScrubbedInput storeProductId) {
        this.storeProductId = storeProductId;
        return this;
    }

    public ScrubbedInput getProductName() {
        return productName;
    }

    public StoreProductForm setProductName(ScrubbedInput productName) {
        this.productName = productName;
        return this;
    }

    public ScrubbedInput getProductPrice() {
        return productPrice;
    }

    public StoreProductForm setProductPrice(ScrubbedInput productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public ScrubbedInput getProductInfo() {
        return productInfo;
    }

    public StoreProductForm setProductInfo(ScrubbedInput productInfo) {
        this.productInfo = productInfo;
        return this;
    }

    public ScrubbedInput getStoreCategoryId() {
        return storeCategoryId;
    }

    public StoreProductForm setStoreCategoryId(ScrubbedInput storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
        return this;
    }

    public boolean isProductFresh() {
        return productFresh;
    }

    public StoreProductForm setProductFresh(boolean productFresh) {
        this.productFresh = productFresh;
        return this;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public StoreProductForm setCategories(Map<String, String> categories) {
        this.categories = categories;
        return this;
    }

    public List<StoreProductEntity> getStoreProducts() {
        return storeProducts;
    }

    public StoreProductForm setStoreProducts(List<StoreProductEntity> storeProducts) {
        this.storeProducts = storeProducts;
        return this;
    }
}
