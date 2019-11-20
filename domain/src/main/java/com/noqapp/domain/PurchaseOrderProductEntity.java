package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.StringJoiner;

/**
 * hitender
 * 3/29/18 3:43 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "PURCHASE_ORDER_PRODUCT")
@CompoundIndexes(value = {
        @CompoundIndex(name = "por_qid_bs_idx", def = "{'QID' : 1, 'BS' : 1}", unique = false),
        @CompoundIndex(name = "por_bn_idx", def = "{'BN' : 1}", unique = false),
        @CompoundIndex(name = "por_qr_idx", def = "{'QR' : 1}", unique = false),
        @CompoundIndex(name = "por_po_idx", def = "{'PO' : 1}", unique = false),
})
public class PurchaseOrderProductEntity extends BaseEntity {

    @Field("PI")
    private String productId;

    @Field("PN")
    private String productName;

    @Field("PP")
    private int productPrice;

    @Field("PD")
    private int productDiscount;

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

    @Field("PQ")
    private int productQuantity;

    @Field("PO")
    private String purchaseOrderId;

    @Field("QID")
    private String queueUserId;

    @Field("BS")
    private String bizStoreId;

    @Field("BN")
    private String bizNameId;

    @Field("QR")
    private String codeQR;

    @Field ("BT")
    private BusinessTypeEnum businessType;

    public String getProductId() {
        return productId;
    }

    public PurchaseOrderProductEntity setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public PurchaseOrderProductEntity setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public PurchaseOrderProductEntity setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public PurchaseOrderProductEntity setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
        return this;
    }

    public ProductTypeEnum getProductType() {
        return productType;
    }

    public PurchaseOrderProductEntity setProductType(ProductTypeEnum productType) {
        this.productType = productType;
        return this;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public PurchaseOrderProductEntity setUnitValue(int unitValue) {
        this.unitValue = unitValue;
        return this;
    }

    public UnitOfMeasurementEnum getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public PurchaseOrderProductEntity setUnitOfMeasurement(UnitOfMeasurementEnum unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
        return this;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public PurchaseOrderProductEntity setPackageSize(int packageSize) {
        this.packageSize = packageSize;
        return this;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public PurchaseOrderProductEntity setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
        return this;
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public PurchaseOrderProductEntity setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public PurchaseOrderProductEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public PurchaseOrderProductEntity setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public PurchaseOrderProductEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public PurchaseOrderProductEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public PurchaseOrderProductEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    @Transient
    public int computeCost() {
        return productQuantity * (productPrice - productDiscount);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PurchaseOrderProductEntity.class.getSimpleName() + "[", "]")
            .add("productId=\"" + productId + "\"")
            .add("productName=\"" + productName + "\"")
            .add("productPrice=" + productPrice)
            .add("productDiscount=" + productDiscount)
            .add("productType=" + productType)
            .add("unitValue=" + unitValue)
            .add("unitOfMeasurement=" + unitOfMeasurement)
            .add("packageSize=" + packageSize)
            .add("productQuantity=" + productQuantity)
            .add("purchaseOrderId=\"" + purchaseOrderId + "\"")
            .add("queueUserId=\"" + queueUserId + "\"")
            .add("bizStoreId=\"" + bizStoreId + "\"")
            .add("bizNameId=\"" + bizNameId + "\"")
            .add("codeQR=\"" + codeQR + "\"")
            .add("businessType=" + businessType)
            .toString();
    }
}
