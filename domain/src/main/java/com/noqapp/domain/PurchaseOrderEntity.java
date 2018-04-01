package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryTypeEnum;
import com.noqapp.domain.types.PaymentTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 3/29/18 3:36 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "PURCHASE_ORDER")
@CompoundIndexes(value = {
        @CompoundIndex(name = "po_qid_bz_idx", def = "{'QID' : 1, 'BZ' : 1}", unique = false),
        @CompoundIndex(name = "po_bn_idx", def = "{'BN' : 1}", unique = false),
        @CompoundIndex(name = "po_cqr_idx", def = "{'CQR' : 1}", unique = false),
})
public class PurchaseOrderEntity extends BaseEntity {

    @Field("QID")
    private String queueUserId;

    @Field("BZ")
    private String bizStoreId;

    @Field("BN")
    private String bizNameId;

    @Field("CQR")
    private String codeQR;

    @Field("CN")
    private String customerName;

    @Field("DA")
    private String deliveryAddress;

    @Field("CP")
    private String customerPhone;

    @Field("SD")
    private int storeDiscount;

    @Field("OP")
    private String orderPrice;

    @Field("DM")
    private DeliveryTypeEnum deliveryType;

    @Field("PT")
    private PaymentTypeEnum paymentType;

    @Field("OS")
    private List<PurchaseOrderStateEnum> orderStates = new LinkedList<PurchaseOrderStateEnum>() {{add(PurchaseOrderStateEnum.IN);}};

    @Field ("BT")
    private BusinessTypeEnum businessType;

    @SuppressWarnings("unused")
    private PurchaseOrderEntity() {
        //Default constructor, required to keep bean happy
    }

    public PurchaseOrderEntity(String queueUserId, String bizStoreId, String bizNameId, String codeQR) {
        this.queueUserId = queueUserId;
        this.bizStoreId = bizStoreId;
        this.bizNameId = bizNameId;
        this.codeQR = codeQR;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public String getCustomerName() {
        return customerName;
    }

    public PurchaseOrderEntity setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public PurchaseOrderEntity setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public PurchaseOrderEntity setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public int getStoreDiscount() {
        return storeDiscount;
    }

    public PurchaseOrderEntity setStoreDiscount(int storeDiscount) {
        this.storeDiscount = storeDiscount;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public PurchaseOrderEntity setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public DeliveryTypeEnum getDeliveryType() {
        return deliveryType;
    }

    public PurchaseOrderEntity setDeliveryType(DeliveryTypeEnum deliveryType) {
        this.deliveryType = deliveryType;
        return this;
    }

    public PaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public PurchaseOrderEntity setPaymentType(PaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public List<PurchaseOrderStateEnum> getOrderStates() {
        return orderStates;
    }

    public PurchaseOrderEntity addOrderState(PurchaseOrderStateEnum orderState) {
        this.orderStates.add(orderState);
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public PurchaseOrderEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }
}
