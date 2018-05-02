package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonPurchaseOrderProduct;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseProductOrderManager;
import com.noqapp.repository.StoreHourManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;

/**
 * hitender
 * 4/1/18 12:35 AM
 */
@Service
public class PurchaseOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderService.class);

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO8601_FMT);

    private BizService bizService;
    private TokenQueueService tokenQueueService;
    private StoreHourManager storeHourManager;
    private AccountService accountService;
    private StoreProductService storeProductService;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseProductOrderManager purchaseProductOrderManager;

    @Autowired
    public PurchaseOrderService(
            BizService bizService,
            TokenQueueService tokenQueueService,
            StoreHourManager storeHourManager,
            AccountService accountService,
            StoreProductService storeProductService,
            PurchaseOrderManager purchaseOrderManager,
            PurchaseProductOrderManager purchaseProductOrderManager
    ) {
        this.bizService = bizService;
        this.tokenQueueService = tokenQueueService;
        this.storeHourManager = storeHourManager;
        this.accountService = accountService;
        this.storeProductService = storeProductService;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseProductOrderManager = purchaseProductOrderManager;
    }

    @Mobile
    public JsonToken getNextOrder(
            String codeQR,
            String did,
            String qid,
            long averageServiceTime,
            TokenServiceEnum tokenService
    ) {
        try {
            TokenQueueEntity tokenQueue = tokenQueueService.getNextToken(codeQR);
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            ZoneId zoneId = TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId();
            DayOfWeek dayOfWeek = ZonedDateTime.now(zoneId).getDayOfWeek();
            StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), dayOfWeek);
            Date expectedServiceBegin = tokenQueueService.computeExpectedServiceBeginTime(averageServiceTime, zoneId, storeHour, tokenQueue);

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                    .setToken(tokenQueue.getLastNumber())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setQueueStatus(tokenQueue.getQueueStatus())
                    .setExpectedServiceBegin(expectedServiceBegin);
        } catch (Exception e) {
            LOG.error("Failed getting token reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed getting token");
        }
    }

    //TODO add multiple logic to validate and more complicated response on failure of order submission for letting user know.
    public void createOrder(JsonPurchaseOrder jsonPurchaseOrder, String did, TokenServiceEnum tokenService) {
        BizStoreEntity bizStore = bizService.getByStoreId(jsonPurchaseOrder.getBizStoreId());
        JsonToken jsonToken = getNextOrder(bizStore.getCodeQR(), did, jsonPurchaseOrder.getQueueUserId(), bizStore.getAverageServiceTime(), tokenService);

        Date expectedServiceBegin = null;
        try {
            expectedServiceBegin = simpleDateFormat.parse(jsonToken.getExpectedServiceBegin());
        } catch (ParseException e) {
            LOG.error("Failed to parse date, reason={}", e.getLocalizedMessage(), e);
        }

        PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(
                        jsonPurchaseOrder.getQueueUserId(),
                        jsonPurchaseOrder.getBizStoreId(),
                        bizStore.getBizName().getId(),
                        bizStore.getCodeQR())
                .setDid(did)
                .setCustomerName(jsonPurchaseOrder.getCustomerName())
                .setDeliveryAddress(jsonPurchaseOrder.getDeliveryAddress())
                .setCustomerPhone(jsonPurchaseOrder.getCustomerPhone())
                .setStoreDiscount(bizStore.getDiscount())
                .setOrderPrice(jsonPurchaseOrder.getOrderPrice())
                .setDeliveryType(jsonPurchaseOrder.getDeliveryType())
                .setPaymentType(jsonPurchaseOrder.getPaymentType())
                .setBusinessType(bizStore.getBusinessType())
                .setTokenNumber(jsonToken.getToken())
                .setExpectedServiceBegin(expectedServiceBegin)
                .setTokenService(tokenService);
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

        jsonPurchaseOrder.setServingNumber(jsonToken.getServingNumber())
                .setToken(purchaseOrder.getTokenNumber())
                .setExpectedServiceBegin(jsonPurchaseOrder.getExpectedServiceBegin())
                .setTransactionId(UUID.randomUUID().toString())
                .setPurchaseOrderState(PurchaseOrderStateEnum.PO);
    }
}
