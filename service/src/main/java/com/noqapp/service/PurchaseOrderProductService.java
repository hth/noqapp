package com.noqapp.service;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.PurchaseOrderProductManagerJDBC;
import com.noqapp.repository.UserAddressManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-04-01 12:25
 */
@Service
public class PurchaseOrderProductService {

    private CouponService couponService;
    private PurchaseOrderProductManager purchaseOrderProductManager;
    private PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;
    private UserAddressService userAddressService;

    @Autowired
    public PurchaseOrderProductService(
        CouponService couponService,
        PurchaseOrderProductManager purchaseOrderProductManager,
        PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC,
        UserAddressService userAddressService
    ) {
        this.couponService = couponService;
        this.purchaseOrderProductManager = purchaseOrderProductManager;
        this.purchaseOrderProductManagerJDBC = purchaseOrderProductManagerJDBC;
        this.userAddressService = userAddressService;
    }

    @Mobile
    public JsonPurchaseOrder populateJsonPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        List<PurchaseOrderProductEntity> products = purchaseOrderProductManager.getAllByPurchaseOrderId(purchaseOrder.getId());
        UserAddressEntity userAddress = userAddressService.findById(purchaseOrder.getUserAddressId());
        return couponService.addCouponInformationIfAny(new JsonPurchaseOrder(purchaseOrder, products, userAddress));
    }

    @Mobile
    public JsonPurchaseOrder populateHistoricalJsonPurchaseOrder(PurchaseOrderEntity purchaseOrder) {
        List<PurchaseOrderProductEntity> products = purchaseOrderProductManagerJDBC.getByPurchaseOrderId(purchaseOrder.getId());
        UserAddressEntity userAddress = userAddressService.findById(purchaseOrder.getUserAddressId());
        return couponService.addCouponInformationIfAny(new JsonPurchaseOrder(purchaseOrder, products, userAddress));
    }
}
