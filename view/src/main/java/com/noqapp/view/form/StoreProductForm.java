package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;

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
    private ScrubbedInput productDiscount;
    private ScrubbedInput productInfo;
    private ScrubbedInput storeCategoryId;
    private ScrubbedInput unitOfMeasurement;
    private ScrubbedInput unitValue;
    private ScrubbedInput packageSize;
    private ScrubbedInput inventoryCount;
    private ScrubbedInput productType;

    private Map<String, String> categories;
    private List<StoreProductEntity> storeProducts;
    private ProductTypeEnum[] productTypes;
    private UnitOfMeasurementEnum[] unitOfMeasurements;
    private BusinessTypeEnum businessType;

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

    public ScrubbedInput getProductDiscount() {
        return productDiscount;
    }

    public StoreProductForm setProductDiscount(ScrubbedInput productDiscount) {
        this.productDiscount = productDiscount;
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

    public ScrubbedInput getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public StoreProductForm setUnitOfMeasurement(ScrubbedInput unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
        return this;
    }

    public ScrubbedInput getUnitValue() {
        return unitValue;
    }

    public StoreProductForm setUnitValue(ScrubbedInput unitValue) {
        this.unitValue = unitValue;
        return this;
    }

    public ScrubbedInput getPackageSize() {
        return packageSize;
    }

    public StoreProductForm setPackageSize(ScrubbedInput packageSize) {
        this.packageSize = packageSize;
        return this;
    }

    public ScrubbedInput getInventoryCount() {
        return inventoryCount;
    }

    public StoreProductForm setInventoryCount(ScrubbedInput inventoryCount) {
        this.inventoryCount = inventoryCount;
        return this;
    }

    public ScrubbedInput getProductType() {
        return productType;
    }

    public StoreProductForm setProductType(ScrubbedInput productType) {
        this.productType = productType;
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

    public ProductTypeEnum[] getProductTypes() {
        return productTypes;
    }

    public StoreProductForm setProductTypes(ProductTypeEnum[] productTypes) {
        this.productTypes = productTypes;
        return this;
    }

    public UnitOfMeasurementEnum[] getUnitOfMeasurements() {
        return unitOfMeasurements;
    }

    public StoreProductForm setUnitOfMeasurements(UnitOfMeasurementEnum[] unitOfMeasurements) {
        this.unitOfMeasurements = unitOfMeasurements;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public StoreProductForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }
}
