package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Validate;
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
import com.noqapp.domain.json.JsonTokenAndQueue;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    private StoreProductService storeProductService;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseProductOrderManager purchaseProductOrderManager;
    private UserAddressService userAddressService;

    @Autowired
    public PurchaseOrderService(
            BizService bizService,
            TokenQueueService tokenQueueService,
            StoreHourManager storeHourManager,
            StoreProductService storeProductService,
            PurchaseOrderManager purchaseOrderManager,
            PurchaseProductOrderManager purchaseProductOrderManager,
            UserAddressService userAddressService
    ) {
        this.bizService = bizService;
        this.tokenQueueService = tokenQueueService;
        this.storeHourManager = storeHourManager;
        this.storeProductService = storeProductService;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseProductOrderManager = purchaseProductOrderManager;
        this.userAddressService = userAddressService;
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
    @Mobile
    public void createOrder(JsonPurchaseOrder jsonPurchaseOrder, String qid, String did, TokenServiceEnum tokenService) {
        BizStoreEntity bizStore = bizService.getByStoreId(jsonPurchaseOrder.getBizStoreId());
        JsonToken jsonToken = getNextOrder(bizStore.getCodeQR(), did, qid, bizStore.getAverageServiceTime(), tokenService);

        Date expectedServiceBegin = null;
        try {
            if (jsonToken.getExpectedServiceBegin() != null) {
                expectedServiceBegin = simpleDateFormat.parse(jsonToken.getExpectedServiceBegin());
            }
        } catch (ParseException e) {
            LOG.error("Failed to parse date, reason={}", e.getLocalizedMessage(), e);
        }

        PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(
                        qid,
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
                .setTokenService(tokenService)
                .setTransactionId(CommonUtil.generateTransactionId(jsonPurchaseOrder.getBizStoreId(), jsonToken.getToken()));
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
                    .setQueueUserId(qid)
                    .setBizStoreId(jsonPurchaseOrder.getBizStoreId())
                    .setBizNameId(bizStore.getBizName().getId())
                    .setCodeQR(bizStore.getCodeQR())
                    .setBusinessType(bizStore.getBusinessType())
                    .setPurchaseOrderId(purchaseOrder.getId());
            purchaseProductOrderManager.save(purchaseOrderProduct);
        }

        purchaseOrder
                .addOrderState(PurchaseOrderStateEnum.VB)
                .addOrderState(PurchaseOrderStateEnum.PO);
        purchaseOrderManager.save(purchaseOrder);
        userAddressService.addressLastUsed(jsonPurchaseOrder.getDeliveryAddress(), qid);

        jsonPurchaseOrder.setServingNumber(jsonToken.getServingNumber())
                .setToken(purchaseOrder.getTokenNumber())
                .setExpectedServiceBegin(jsonPurchaseOrder.getExpectedServiceBegin())
                .setTransactionId(purchaseOrder.getTransactionId())
                .setPurchaseOrderState(purchaseOrder.getOrderStates().get(purchaseOrder.getOrderStates().size() - 1));
    }

    private List<PurchaseOrderEntity> findAllOpenOrder(String qid) {
        return purchaseOrderManager.findAllOpenOrder(qid);
    }

    @Mobile
    public List<JsonTokenAndQueue> findAllOpenOrderAsJson(String qid) {
        Validate.isValidQid(qid);

        List<JsonTokenAndQueue> jsonTokenAndQueues = new ArrayList<>();
        List<PurchaseOrderEntity> purchaseOrders = findAllOpenOrder(qid);
        for(PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            BizStoreEntity bizStore = bizService.findByCodeQR(purchaseOrder.getCodeQR());
            bizStore.setStoreHours(storeHourManager.findAll(bizStore.getId()));

            JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue(purchaseOrder, bizStore);
            jsonTokenAndQueues.add(jsonTokenAndQueue);
        }

        return jsonTokenAndQueues;
    }
}
