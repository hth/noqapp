package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 10/5/18 9:31 PM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonPurchaseOrderProductHistorical extends AbstractDomain {

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

    @JsonProperty("po")
    private String purchaseOrderId;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    public JsonPurchaseOrderProductHistorical() {
        //Required default constructor
    }

    public JsonPurchaseOrderProductHistorical(PurchaseOrderProductEntity purchaseOrderProduct) {
        this.productId = purchaseOrderProduct.getProductId();
        this.productName = purchaseOrderProduct.getProductName();
        this.productPrice = purchaseOrderProduct.getProductPrice();
        this.productDiscount = purchaseOrderProduct.getProductDiscount();
        this.productQuantity = purchaseOrderProduct.getProductQuantity();
        this.purchaseOrderId = purchaseOrderProduct.getPurchaseOrderId();
        this.queueUserId = purchaseOrderProduct.getQueueUserId();
        this.bizStoreId = purchaseOrderProduct.getBizStoreId();
        this.bizNameId = purchaseOrderProduct.getBizNameId();
        this.codeQR = purchaseOrderProduct.getCodeQR();
        this.businessType = purchaseOrderProduct.getBusinessType();
    }
}
