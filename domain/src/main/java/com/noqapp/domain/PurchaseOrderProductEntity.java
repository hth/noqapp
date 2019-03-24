package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
        return "PurchaseOrderProductEntity{" +
            "productId='" + productId + '\'' +
            ", productName='" + productName + '\'' +
            ", productPrice=" + productPrice +
            ", productDiscount=" + productDiscount +
            ", productQuantity=" + productQuantity +
            ", purchaseOrderId='" + purchaseOrderId + '\'' +
            ", queueUserId='" + queueUserId + '\'' +
            ", bizStoreId='" + bizStoreId + '\'' +
            ", bizNameId='" + bizNameId + '\'' +
            ", codeQR='" + codeQR + '\'' +
            ", businessType=" + businessType +
            '}';
    }
}
