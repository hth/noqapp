package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.TaxEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;

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

    @JsonProperty("ta")
    private TaxEnum tax;

    @JsonProperty("pd")
    private int productDiscount;

    @JsonProperty("pt")
    private ProductTypeEnum productType;

    /* Like 1 kg, 200 ml, 2 kg and so on. */
    @JsonProperty("uv")
    private int unitValue;

    @JsonProperty ("um")
    private UnitOfMeasurementEnum unitOfMeasurement;

    /* Package size is the quantity of individual items in the unit. Like 1 strip contains 10 tablets. Defaults to 1. */
    @JsonProperty("ps")
    private int packageSize;

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
        this.tax = purchaseOrderProduct.getTax();
        this.productDiscount = purchaseOrderProduct.getProductDiscount();
        this.productType = purchaseOrderProduct.getProductType();
        this.unitValue = purchaseOrderProduct.getUnitValue();
        this.unitOfMeasurement = purchaseOrderProduct.getUnitOfMeasurement();
        this.packageSize = purchaseOrderProduct.getPackageSize();
        this.productQuantity = purchaseOrderProduct.getProductQuantity();
        this.purchaseOrderId = purchaseOrderProduct.getPurchaseOrderId();
        this.queueUserId = purchaseOrderProduct.getQueueUserId();
        this.bizStoreId = purchaseOrderProduct.getBizStoreId();
        this.bizNameId = purchaseOrderProduct.getBizNameId();
        this.codeQR = purchaseOrderProduct.getCodeQR();
        this.businessType = purchaseOrderProduct.getBusinessType();
    }
}
