package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonPurchaseOrderProduct;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseProductOrderManager;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 4/1/18 12:35 AM
 */
@Service
public class PurchaseOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderService.class);

    private BizService bizService;
    private AccountService accountService;
    private StoreProductService storeProductService;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseProductOrderManager purchaseProductOrderManager;

    @Autowired
    public PurchaseOrderService(
            BizService bizService,
            AccountService accountService,
            StoreProductService storeProductService,
            PurchaseOrderManager purchaseOrderManager,
            PurchaseProductOrderManager purchaseProductOrderManager
    ) {
        this.bizService = bizService;
        this.accountService = accountService;
        this.storeProductService = storeProductService;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseProductOrderManager = purchaseProductOrderManager;
    }

    //TODO add multiple logic to validate and more complicated response on failure of order submission for letting user know.
    public boolean createOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        BizStoreEntity bizStore = bizService.getByStoreId(jsonPurchaseOrder.getBizStoreId());

        PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(
                        jsonPurchaseOrder.getQueueUserId(),
                        jsonPurchaseOrder.getBizStoreId(),
                        bizStore.getBizName().getId(),
                        bizStore.getCodeQR())
                .setCustomerName(jsonPurchaseOrder.getCustomerName())
                .setDeliveryAddress(jsonPurchaseOrder.getDeliveryAddress())
                .setCustomerPhone(jsonPurchaseOrder.getCustomerPhone())
                .setStoreDiscount(bizStore.getDiscount())
                .setOrderPrice(jsonPurchaseOrder.getOrderPrice())
                .setDeliveryType(jsonPurchaseOrder.getDeliveryType())
                .setPaymentType(jsonPurchaseOrder.getPaymentType())
                .setBusinessType(bizStore.getBusinessType());
        purchaseOrder.setId(CommonUtil.generateHexFromObjectId());
        purchaseOrderManager.save(purchaseOrder);

        for (JsonPurchaseOrderProduct jsonPurchaseOrderProduct : jsonPurchaseOrder.getPurchaseOrderProducts()) {
            StoreProductEntity storeProduct = storeProductService.findOne(jsonPurchaseOrderProduct.getProductId());
            PurchaseOrderProductEntity purchaseOrderProduct = new PurchaseOrderProductEntity()
                    .setProductId(jsonPurchaseOrderProduct.getProductId())
                    .setProductName(storeProduct.getProductName())
                    .setProductPrice(storeProduct.getProductPrice())
                    .setProductDiscount(storeProduct.getProductDiscount())
                    .setProductQuantity(jsonPurchaseOrderProduct.getProductQuantity())
                    .setQueueUserId(jsonPurchaseOrder.getQueueUserId())
                    .setBizStoreId(jsonPurchaseOrder.getBizStoreId())
                    .setBizNameId(bizStore.getBizName().getId())
                    .setCodeQR(bizStore.getCodeQR())
                    .setBusinessType(bizStore.getBusinessType())
                    .setPurchaseOrderId(purchaseOrder.getId());
            purchaseProductOrderManager.save(purchaseOrderProduct);
        }

        return true;
    }
}
