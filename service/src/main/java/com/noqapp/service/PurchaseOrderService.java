package com.noqapp.service;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;
import static com.noqapp.common.utils.Constants.UNDER_SCORE;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.PointEarnedEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonPurchaseOrderHistorical;
import com.noqapp.domain.json.JsonPurchaseOrderHistoricalList;
import com.noqapp.domain.json.JsonPurchaseOrderList;
import com.noqapp.domain.json.JsonPurchaseOrderProduct;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.JsonTokenAndQueue;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.json.fcm.data.JsonTopicOrderData;
import com.noqapp.domain.json.payment.cashfree.JsonRequestPurchaseOrderCF;
import com.noqapp.domain.json.payment.cashfree.JsonResponseWithCFToken;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PointActivityEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.QueueJoinDeniedEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserManager;
import com.noqapp.repository.PointEarnedManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.PurchaseOrderProductManagerJDBC;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.exceptions.CouponCannotApplyException;
import com.noqapp.service.exceptions.CouponRemovalException;
import com.noqapp.service.exceptions.FailedTransactionException;
import com.noqapp.service.exceptions.OrderFailedReActivationException;
import com.noqapp.service.exceptions.PriceMismatchException;
import com.noqapp.service.exceptions.PurchaseOrderCancelException;
import com.noqapp.service.exceptions.PurchaseOrderFailException;
import com.noqapp.service.exceptions.PurchaseOrderNegativeException;
import com.noqapp.service.exceptions.PurchaseOrderProductNFException;
import com.noqapp.service.exceptions.PurchaseOrderRefundExternalException;
import com.noqapp.service.exceptions.PurchaseOrderRefundPartialException;
import com.noqapp.service.exceptions.QueueAbortPaidPastDurationException;
import com.noqapp.service.exceptions.StoreDayClosedException;
import com.noqapp.service.exceptions.StoreInActiveException;
import com.noqapp.service.exceptions.StorePreventJoiningException;
import com.noqapp.service.exceptions.StoreTempDayClosedException;
import com.noqapp.service.nlp.NLPService;
import com.noqapp.service.payment.CashfreeService;
import com.noqapp.service.transaction.TransactionService;
import com.noqapp.service.utils.ServiceUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * hitender
 * 4/1/18 12:35 AM
 */
@Service
public class PurchaseOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderService.class);

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO8601_FMT);

    private int limitedToDays;

    private BizStoreManager bizStoreManager;
    private BusinessUserManager businessUserManager;
    private TokenQueueService tokenQueueService;
    private StoreHourManager storeHourManager;
    private StoreProductService storeProductService;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    private PurchaseOrderProductManager purchaseOrderProductManager;
    private PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;
    private QueueManager queueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private PointEarnedManager pointEarnedManager;
    private CouponService couponService;
    private UserAddressService userAddressService;
    private FirebaseMessageService firebaseMessageService;
    private RegisteredDeviceManager registeredDeviceManager;
    private TokenQueueManager tokenQueueManager;
    private AccountService accountService;
    private TransactionService transactionService;
    private NLPService nlpService;
    private MailService mailService;
    private CashfreeService cashfreeService;
    private PurchaseOrderProductService purchaseOrderProductService;
    private SubscribeTopicService subscribeTopicService;

    private ExecutorService executorService;

    @Autowired
    public PurchaseOrderService(
        @Value("${limitedToDays:5}")
        int limitedToDays,

        BizStoreManager bizStoreManager,
        BusinessUserManager businessUserManager,
        StoreHourManager storeHourManager,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC,
        PurchaseOrderProductManager purchaseOrderProductManager,
        PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC,
        QueueManager queueManager,
        QueueManagerJDBC queueManagerJDBC,
        PointEarnedManager pointEarnedManager,
        CouponService couponService,
        UserAddressService userAddressService,
        FirebaseMessageService firebaseMessageService,
        RegisteredDeviceManager registeredDeviceManager,
        TokenQueueManager tokenQueueManager,
        StoreProductService storeProductService,
        TokenQueueService tokenQueueService,
        AccountService accountService,
        TransactionService transactionService,
        NLPService nlpService,
        MailService mailService,
        CashfreeService cashfreeService,
        PurchaseOrderProductService purchaseOrderProductService,
        SubscribeTopicService subscribeTopicService
    ) {
        this.limitedToDays = limitedToDays;

        this.bizStoreManager = bizStoreManager;
        this.businessUserManager = businessUserManager;
        this.tokenQueueService = tokenQueueService;
        this.storeHourManager = storeHourManager;
        this.storeProductService = storeProductService;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
        this.purchaseOrderProductManager = purchaseOrderProductManager;
        this.purchaseOrderProductManagerJDBC = purchaseOrderProductManagerJDBC;
        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.pointEarnedManager = pointEarnedManager;
        this.couponService = couponService;
        this.userAddressService = userAddressService;
        this.firebaseMessageService = firebaseMessageService;
        this.registeredDeviceManager = registeredDeviceManager;
        this.tokenQueueManager = tokenQueueManager;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.nlpService = nlpService;
        this.mailService = mailService;
        this.cashfreeService = cashfreeService;
        this.purchaseOrderProductService = purchaseOrderProductService;
        this.subscribeTopicService = subscribeTopicService;

        this.executorService = newCachedThreadPool();
    }

    private JsonToken getNextOrder(BizStoreEntity bizStore) {
        ZoneId zoneId = TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId();
        DayOfWeek dayOfWeek = ZonedDateTime.now(zoneId).getDayOfWeek();
        StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), dayOfWeek);
        String codeQR = bizStore.getCodeQR();

        if (!bizStore.isActive() || storeHour.isDayClosed() || storeHour.isTempDayClosed() || storeHour.isPreventJoining()) {
            LOG.warn("When store closed or prevent joining, attempting to create new order");

            /* Always Check if store is active or not. */
            if (!bizStore.isActive()) {
                throw new StoreInActiveException("Store is offline bizStoreId " + bizStore.getId());
            }

            /* Skip for HS as the orders are placed through internally. */
            if (bizStore.getBusinessType() != BusinessTypeEnum.HS) {
                if (storeHour.isDayClosed()) {
                    throw new StoreDayClosedException("Store is closed today bizStoreId " + bizStore.getId());
                }
                if (storeHour.isTempDayClosed()) {
                    throw new StoreTempDayClosedException("Store is temporary closed bizStoreId " + bizStore.getId());
                }
                if (storeHour.isPreventJoining()) {
                    throw new StorePreventJoiningException("Store not accepting new orders bizStoreId " + bizStore.getId());
                }
            }
        }

        try {
            TokenQueueEntity tokenQueue = tokenQueueService.getNextToken(codeQR, bizStore.getAvailableTokenCount());
            if (tokenQueue == null && bizStore.getAvailableTokenCount() > 0) {
                return ServiceUtils.blankJsonToken(codeQR, QueueJoinDeniedEnum.L, bizStore);
            }
            LOG.info("Assigned order number with codeQR={} with new token={}", codeQR, tokenQueue.getLastNumber());
            ZonedDateTime expectedServiceBegin = ServiceUtils.computeExpectedServiceBeginTime(bizStore, storeHour, tokenQueue.getLastNumber());

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setToken(tokenQueue.getLastNumber())
                .setDisplayToken(tokenQueue.generateDisplayToken())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setExpectedServiceBegin(expectedServiceBegin);
        } catch (Exception e) {
            LOG.error("Failed getting token reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed getting token");
        }
    }

    @Mobile
    public JsonPurchaseOrder cancelOrderByClient(String qid, String transactionId) {
        try {
            Assert.isTrue(Validate.isValidQid(qid), "Should be a valid qid " + qid);

            PurchaseOrderEntity purchaseOrder = transactionService.cancelPurchaseInitiatedByClient(qid, transactionId);
            TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
            doActionBasedOnQueueStatus(purchaseOrder.getCodeQR(), purchaseOrder, tokenQueue, null);

            /* Increase inventory when purchase successful. */
            List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManager.getAllByPurchaseOrderId(purchaseOrder.getId());
            for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
                storeProductService.changeInventoryCount(purchaseOrderProduct.getProductId(), purchaseOrderProduct.getProductQuantity());
            }

            UserAddressEntity userAddress = userAddressService.findById(purchaseOrder.getUserAddressId());
            return JsonPurchaseOrder.populateForCancellingOrder(purchaseOrder, userAddress);
        } catch (
            PurchaseOrderRefundPartialException |
                PurchaseOrderRefundExternalException |
                PurchaseOrderCancelException |
                QueueAbortPaidPastDurationException e
        ) {
            LOG.warn("Failed cancel order reason={}", e.getLocalizedMessage());
            throw e;
        } catch (Exception e) {
            LOG.error("Failed cancellation of order reason={}", e.getLocalizedMessage(), e);
            return null;
        }
    }

    /* Activate old order by client. */
    @Mobile
    public JsonPurchaseOrder activateOrderByClient(String qid, String transactionId, String did) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderManagerJDBC.findOrderByTransactionId(qid, transactionId);
        if (DateUtil.getDaysBetween(purchaseOrder.getCreated(), DateUtil.nowDate()) > 30) {
            LOG.error("Order expired transactionId={}", transactionId);
            throw new OrderFailedReActivationException("Order cannot be activated after 30 days");
        }

        switch (purchaseOrder.getPresentOrderState()) {
            case IN:
            case PC:
            case IB:
            case FO:
            case OD:
            case CO:
                LOG.error("Cannot activate order in state {} transactionId={}", purchaseOrder.getPresentOrderState(), transactionId);
                throw new OrderFailedReActivationException("Cannot activate this order");
            case VB:
            case PO:
                //Allow activate when in VB state or PO state
            default:
                List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManagerJDBC.getByPurchaseOrderId(purchaseOrder.getId());
                JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder(purchaseOrder, purchaseOrderProducts, userAddressService.findById(purchaseOrder.getUserAddressId()));
                createOrder(jsonPurchaseOrder, purchaseOrder.getQueueUserId(), did, TokenServiceEnum.C);
                purchaseOrderProductManagerJDBC.deleteByPurchaseOrderId(purchaseOrder.getId());
                purchaseOrderManagerJDBC.deleteById(purchaseOrder.getId());
                return jsonPurchaseOrder;
        }
    }

    @Mobile
    public PurchaseOrderEntity findHistoricalPurchaseOrder(String qid, String transactionId) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderManagerJDBC.findOrderByTransactionId(qid, transactionId);

        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(purchaseOrder.getQueueUserId());
        purchaseOrder
            .setCustomerName(userProfile.getName())
            .setCustomerPhone(StringUtils.isBlank(userProfile.getGuardianPhone()) ? userProfile.getPhone() : userProfile.getGuardianPhone());

        return purchaseOrder;
    }

    public List<PurchaseOrderEntity> historicalByQidAndBizNameId(String qid, String bizNameId) {
        return purchaseOrderManagerJDBC.findByQidAndBizNameId(qid, bizNameId);
    }

    public List<PurchaseOrderEntity> currentByQidAndBizNameId(String qid, String bizNameId) {
        return purchaseOrderManager.findByQidAndBizNameId(qid, bizNameId);
    }

    @Mobile
    public boolean isOrderCancelled(String qid, String transactionId) {
        Assert.isTrue(Validate.isValidQid(qid), "Should be a valid qid");
        return purchaseOrderManager.isOrderCancelled(qid, transactionId);
    }

    @Mobile
    public JsonPurchaseOrder updateOnPaymentGatewayNotificationAsJsonPurchaseOrder(
        String transactionId,
        String transactionMessage,
        String transactionReferenceId,
        PaymentStatusEnum paymentStatus,
        PurchaseOrderStateEnum purchaseOrderState,
        PaymentModeEnum paymentMode
    ) {
        PurchaseOrderEntity purchaseOrder = updateOnPaymentGatewayNotification(transactionId, transactionMessage, transactionReferenceId, paymentStatus, purchaseOrderState, paymentMode);
        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder(purchaseOrder, userAddressService.findById(purchaseOrder.getUserAddressId()));

        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
        jsonPurchaseOrder
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayServingNumber(tokenQueue.generateDisplayServingNow());
        return jsonPurchaseOrder;
    }

    @Mobile
    public PurchaseOrderEntity updateOnPaymentGatewayNotification(
        String transactionId,
        String transactionMessage,
        String transactionReferenceId,
        PaymentStatusEnum paymentStatus,
        PurchaseOrderStateEnum purchaseOrderState,
        PaymentModeEnum paymentMode
    ) {
        Assert.hasText(transactionId, "No transaction id found");
        PurchaseOrderEntity purchaseOrderExisting = purchaseOrderManager.findByTransactionId(transactionId);
        String transactionMessageAppended = StringUtils.isBlank(purchaseOrderExisting.getTransactionMessage())
            ? transactionMessage
            : purchaseOrderExisting.getTransactionMessage() + ";" + transactionMessage;
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.updateOnPaymentGatewayNotification(
            transactionId,
            transactionMessageAppended,
            transactionReferenceId,
            paymentStatus,
            purchaseOrderState,
            paymentMode
        );

        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
        doActionBasedOnQueueStatus(purchaseOrder.getCodeQR(), purchaseOrder, tokenQueue, null);
        return purchaseOrder;
    }

    @Mobile
    public JsonPurchaseOrder updateOnCashPayment(
        String transactionId,
        String transactionMessage,
        PaymentStatusEnum paymentStatus,
        PurchaseOrderStateEnum purchaseOrderState,
        PaymentModeEnum paymentMode
    ) {
        Assert.hasText(transactionId, "No transaction id found");
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.updateOnCashPayment(
            transactionId,
            transactionMessage,
            paymentStatus,
            purchaseOrderState,
            paymentMode
        );

        /* Notification not required as Business has the update payment and Client always fetches fresh. */
        //TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
        //doActionBasedOnQueueStatus(purchaseOrder.getCodeQR(), purchaseOrder, tokenQueue, null);

        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder(purchaseOrder, userAddressService.findById(purchaseOrder.getUserAddressId()));
        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
        jsonPurchaseOrder
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayServingNumber(tokenQueue.generateDisplayServingNow());
        return jsonPurchaseOrder;
    }

    @Mobile
    public JsonPurchaseOrderList cancelOrderByMerchant(String qid, String transactionId) {
        Assert.isTrue(Validate.isValidQid(qid), "Should be a valid qid " + qid);
        LOG.info("Business is cancelling order for {} {}", qid, transactionId);

        PurchaseOrderEntity purchaseOrder = transactionService.cancelPurchaseInitiatedByMerchant(qid, transactionId);
        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
        doActionBasedOnQueueStatus(purchaseOrder.getCodeQR(), purchaseOrder, tokenQueue, null);

        /* Increase inventory when purchase successful. */
        List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManager.getAllByPurchaseOrderId(purchaseOrder.getId());
        for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
            storeProductService.changeInventoryCount(purchaseOrderProduct.getProductId(), purchaseOrderProduct.getProductQuantity());
        }

        UserAddressEntity userAddress = userAddressService.findById(purchaseOrder.getUserAddressId());
        return new JsonPurchaseOrderList().addPurchaseOrder(JsonPurchaseOrder.populateForCancellingOrder(purchaseOrder, userAddress));
    }

    @Mobile
    public JsonPurchaseOrder modifyOrder(JsonPurchaseOrder jsonPurchaseOrder, String did, TokenServiceEnum tokenService) {
        Assert.hasText(jsonPurchaseOrder.getQueueUserId(), "QID cannot be empty");
        LOG.info("JsonPurchaseOrder={} did={} tokenService={}", jsonPurchaseOrder, did, tokenService);
        List<JsonPurchaseOrderProduct> jsonPurchaseOrderProducts = jsonPurchaseOrder.getJsonPurchaseOrderProducts();

        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findByTransactionId(jsonPurchaseOrder.getTransactionId());
        List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManager.getAllByPurchaseOrderIdWhenPriceZero(purchaseOrder.getId());

        BigDecimal orderPrice = new BigDecimal(purchaseOrder.getOrderPrice());
        for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrderProducts.stream()
                .filter(a -> a.getProductName().contentEquals(purchaseOrderProduct.getProductName()))
                .findFirst()
                .orElse(null);

            if (jsonPurchaseOrderProduct == null) {
                LOG.error("Not found product {}", purchaseOrderProduct.getId());
                throw new PurchaseOrderProductNFException("Product Not Found");
            }
            purchaseOrderProduct
                .setProductPrice(jsonPurchaseOrderProduct.getProductPrice())
                .setTax(jsonPurchaseOrderProduct.getTax());
            purchaseOrderProductManager.save(purchaseOrderProduct);
            orderPrice = orderPrice.add(new BigDecimal(purchaseOrderProduct.getProductPrice()));
        }
        purchaseOrder.setOrderPrice(orderPrice.toString());
        purchaseOrderManager.save(purchaseOrder);
        return new JsonPurchaseOrder(purchaseOrder, purchaseOrderProducts, userAddressService.findById(purchaseOrder.getUserAddressId()));
    }

    /**
     * Orders initiated by business is by default set to PO.
     *
     * @param jsonPurchaseOrder
     * @param did               Device Id of guardian or purchaser. Helps in notifying user of changes through FCM.
     * @param tokenService
     */
    @Mobile
    public void createOrderInitiatedByBusiness(JsonPurchaseOrder jsonPurchaseOrder, String did, TokenServiceEnum tokenService) {
        Assert.hasText(jsonPurchaseOrder.getQueueUserId(), "QID cannot be empty");
        LOG.info("JsonPurchaseOrder={}", jsonPurchaseOrder);
        createOrder(jsonPurchaseOrder, jsonPurchaseOrder.getQueueUserId(), did, tokenService);
        changeItToPurchaseOrderState(jsonPurchaseOrder.getTransactionId(), jsonPurchaseOrder.getBizStoreId());
        jsonPurchaseOrder.setPresentOrderState(PurchaseOrderStateEnum.PO);
    }

    /** Order initiated by business should be set PO as business knows best. Do not change payment here as its always payment pending. */
    private void changeItToPurchaseOrderState(String transactionId, String bizStoreId) {
        purchaseOrderManager.changeItToPurchaseOrderState(transactionId, bizStoreId);
    }

    @Mobile
    public void createOrderWithCFToken(JsonPurchaseOrder jsonPurchaseOrder, String qid, String did, TokenServiceEnum tokenService) {
        createOrder(jsonPurchaseOrder, qid, did, tokenService);
        jsonPurchaseOrder.setJsonResponseWithCFToken(
            createTokenForPurchaseOrder(
                PurchaseOrderEntity.correctPriceForTransaction(jsonPurchaseOrder.getGrandTotal()),
                jsonPurchaseOrder.getTransactionId()));
    }

    /**
     * @param jsonPurchaseOrder
     * @param qid               purchaserQid
     * @param did               Device Id of guardian or purchaser. Helps in notifying user of changes through FCM.
     * @param tokenService
     */
    @Mobile
    public void createOrder(JsonPurchaseOrder jsonPurchaseOrder, String qid, String did, TokenServiceEnum tokenService) {
        BizStoreEntity bizStore;
        if (null == jsonPurchaseOrder.getBizStoreId()) {
            bizStore = bizStoreManager.findByCodeQR(jsonPurchaseOrder.getCodeQR());
        } else {
            bizStore = bizStoreManager.getById(jsonPurchaseOrder.getBizStoreId());
        }

        if (null == bizStore) {
            LOG.error("Failed to find bizStore {} {}", qid, jsonPurchaseOrder);
            throw new RuntimeException("Store not found");
        }

        boolean discountIfAny = false;
        int discountedPrice = 0;
        if (jsonPurchaseOrder.getDeliveryMode() == DeliveryModeEnum.QS) {
            /* Find person being served, check if its the person that would get discounted service. */
            QueueEntity queue = queueManager.findOne(jsonPurchaseOrder.getCodeQR(), jsonPurchaseOrder.getToken());
            Long lastVisited = daysBetweenService(queue.getQueueUserId(), bizStore);
            if (null != lastVisited) {
                if (lastVisited <= bizStore.getFreeFollowupDays()) {
                    discountIfAny = true;
                    /* When its a free service set the order price as 0. */
                    jsonPurchaseOrder
                        .setOrderPrice(String.valueOf(discountedPrice))
                        .setCustomized(true);

                    /* Add discount as line item. */
                    jsonPurchaseOrder.getJsonPurchaseOrderProducts().add(
                        new JsonPurchaseOrderProduct()
                            .setProductName("Follow-up discount before " + lastVisited + " days")
                            .setProductPrice(-bizStore.getProductPrice())
                            .setTax(bizStore.getTax())
                            .setProductQuantity(1)
                    );

                    jsonPurchaseOrder.setDiscountedPurchase(true);
                } else if (lastVisited <= bizStore.getDiscountedFollowupDays()) {
                    discountIfAny = true;
                    /* When it is between the specified days then set the order price. */
                    discountedPrice = bizStore.getProductPrice() - bizStore.getDiscountedFollowupProductPrice();
                    jsonPurchaseOrder
                        .setOrderPrice(String.valueOf(discountedPrice))
                        .setCustomized(true);

                    /* Add discount as line item. */
                    jsonPurchaseOrder.getJsonPurchaseOrderProducts().add(
                        new JsonPurchaseOrderProduct()
                            .setProductName("Follow-up discount within " + lastVisited + " days")
                            .setProductPrice(-bizStore.getDiscountedFollowupProductPrice())
                            .setTax(bizStore.getTax())
                            .setProductQuantity(1)
                    );

                    jsonPurchaseOrder.setDiscountedPurchase(true);
                }
            }
        }

        setCustomerPhoneAndAddress(qid, jsonPurchaseOrder);
        /* Check for coupon if present or apply store discount if any. */
        int storeDiscount = StringUtils.isNotBlank(jsonPurchaseOrder.getCouponId()) ? jsonPurchaseOrder.getStoreDiscount() : bizStore.getDiscount();

        int receivedOrderPrice = 0;
        if (null != jsonPurchaseOrder.getOrderPrice()) {
            receivedOrderPrice = Integer.parseInt(jsonPurchaseOrder.getOrderPrice()) - storeDiscount;
        }

        String grandTotal;
        if (null == jsonPurchaseOrder.getGrandTotal()) {
            grandTotal = String.valueOf(receivedOrderPrice + (StringUtils.isNotBlank(jsonPurchaseOrder.getTax())
                ? Integer.parseInt(jsonPurchaseOrder.getTax())
                : 0));
        } else {
            grandTotal = jsonPurchaseOrder.getGrandTotal();
        }
        PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(qid, bizStore.getId(), bizStore.getBizName().getId(), bizStore.getCodeQR())
            .setDid(did)
            .setCustomerName(jsonPurchaseOrder.getCustomerName())
            .setUserAddressId(jsonPurchaseOrder.getUserAddressId())
            .setCustomerPhone(jsonPurchaseOrder.getCustomerPhone())
            .setCouponId(StringUtils.isNotBlank(jsonPurchaseOrder.getCouponId()) ? jsonPurchaseOrder.getCouponId() : null)
            .setDiscountedPurchase(jsonPurchaseOrder.isDiscountedPurchase())
            .setStoreDiscount(storeDiscount)
            .setPartialPayment(jsonPurchaseOrder.getPartialPayment())
            .setOrderPrice(String.valueOf(receivedOrderPrice))
            .setTax(StringUtils.isNotBlank(jsonPurchaseOrder.getTax()) ? String.valueOf(jsonPurchaseOrder.getTax()) : "0")
            .setGrandTotal(grandTotal)
            .setDeliveryMode(jsonPurchaseOrder.getDeliveryMode())
            //.setPaymentMode(jsonPurchaseOrder.getPaymentMode())
            .setBusinessType(bizStore.getBusinessType())
            .setTokenService(tokenService)
            .setDisplayName(bizStore.getDisplayName())
            .setAdditionalNote(StringUtils.isBlank(jsonPurchaseOrder.getAdditionalNote()) ? null : jsonPurchaseOrder.getAdditionalNote());
        purchaseOrder.setId(CommonUtil.generateHexFromObjectId());

        //Check when did this person visit last to this store
        if (purchaseOrderManagerJDBC.hasClientVisitedThisStoreAndServiced(bizStore.getCodeQR(), qid)) {
            purchaseOrder.setClientVisitedThisStore(true);
            //Fails with Failed getting token reason=Incorrect result size: expected 1, actual 0 when users has not visited the store
            purchaseOrder.setClientVisitedThisStoreDate(purchaseOrderManagerJDBC.clientVisitedStoreAndServicedDate(bizStore.getCodeQR(), qid));
        }

        List<PurchaseOrderProductEntity> purchaseOrderProducts = new LinkedList<>();
        int computedOrderPrice = 0, computedTax = 0;
        Map<String, Integer> productPurchases = new HashMap<>();
        for (JsonPurchaseOrderProduct jsonPurchaseOrderProduct : jsonPurchaseOrder.getJsonPurchaseOrderProducts()) {
            StoreProductEntity storeProduct = null;
            if (StringUtils.isNotBlank(jsonPurchaseOrderProduct.getProductId())) {
                storeProduct = storeProductService.findOne(jsonPurchaseOrderProduct.getProductId());
            }

            PurchaseOrderProductEntity purchaseOrderProduct = new PurchaseOrderProductEntity();
            if (null != storeProduct) {
                purchaseOrderProduct.setProductId(jsonPurchaseOrderProduct.getProductId())
                    .setProductName(storeProduct.getProductName())
                    .setProductPrice(storeProduct.getProductPrice())
                    .setTax(storeProduct.getTax())
                    .setProductDiscount(storeProduct.getProductDiscount())
                    .setProductType(storeProduct.getProductType())
                    .setUnitValue(storeProduct.getUnitValue())
                    .setUnitOfMeasurement(storeProduct.getUnitOfMeasurement())
                    .setPackageSize(storeProduct.getPackageSize());

                switch (bizStore.getBusinessType()) {
                    case RS:
                    case FT:
                        if (storeProduct.getInventoryCurrent() > 0) {
                            productPurchases.put(storeProduct.getId(), -jsonPurchaseOrderProduct.getProductQuantity());
                        } else {
                            LOG.error("Out of product {} {} {} {} {}",
                                storeProduct.getId(),
                                storeProduct.getProductName(),
                                bizStore.getId(),
                                bizStore.getDisplayName(), qid);
                        }
                        break;
                }
            } else {
                purchaseOrderProduct
                    .setProductName(jsonPurchaseOrderProduct.getProductName())
                    .setProductPrice(jsonPurchaseOrderProduct.getProductPrice())
                    .setTax(jsonPurchaseOrderProduct.getTax())
                    .setProductDiscount(discountIfAny ? jsonPurchaseOrderProduct.getProductPrice() : discountedPrice);
            }

            purchaseOrderProduct.setProductQuantity(jsonPurchaseOrderProduct.getProductQuantity())
                .setQueueUserId(qid)
                .setBizStoreId(bizStore.getId())
                .setBizNameId(bizStore.getBizName().getId())
                .setCodeQR(bizStore.getCodeQR())
                .setBusinessType(bizStore.getBusinessType())
                .setPurchaseOrderId(purchaseOrder.getId());
            purchaseOrderProducts.add(purchaseOrderProduct);
            computedOrderPrice = computedOrderPrice + purchaseOrderProduct.computeCost();
            computedTax = computedTax + purchaseOrderProduct.computeTax();
        }

        if (StringUtils.isBlank(purchaseOrder.getOrderPrice())) {
            LOG.warn("Purchase price NOT set for order={}", purchaseOrder.getId());
            purchaseOrder.setOrderPrice("0");
        }

        /* Check if computed grand total and submitted is same. */
        int computedGrandTotal = computedOrderPrice + computedTax - storeDiscount;
        if (computedGrandTotal != Integer.parseInt(purchaseOrder.getGrandTotal()) && !jsonPurchaseOrder.isCustomized()) {
            LOG.error("Computed grand total {} and submitted grand total {}", computedGrandTotal, purchaseOrder.getGrandTotal());
            throw new PriceMismatchException("Price sent and computed does not match");
        }

        /* Check only when order is not placed by the system or via merchant. Skip price check when placed by merchant. */
        if (TokenServiceEnum.M != tokenService) {
            /* Can be narrowed down to pharmacy, lab or other Medical Services like. */
            if (purchaseOrder.isTransactionNotSupported()) {
                LOG.error("Computed order price is less than 1 {} and submitted order price {}", computedOrderPrice, purchaseOrder.getOrderPrice());
                throw new PurchaseOrderNegativeException("Order price cannot be negative");
            }
        }

        JsonToken jsonToken;
        try {
            if (DeliveryModeEnum.QS != jsonPurchaseOrder.getDeliveryMode()) {
                jsonToken = getNextOrder(bizStore);
            } else {
                jsonToken = new JsonToken(jsonPurchaseOrder.getCodeQR(), jsonPurchaseOrder.getBusinessType());
                jsonToken.setToken(jsonPurchaseOrder.getToken())
                    .setDisplayToken(jsonPurchaseOrder.getDisplayToken())
                    .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
            }
            /* Transaction Id is required key and is indexed set to unique. Without this, session and transaction fails. */
            purchaseOrder.setTransactionId(CommonUtil.generateTransactionId(bizStore.getId(), jsonToken.getToken()));
            transactionService.completePurchase(purchaseOrder, purchaseOrderProducts);
            Date expectedServiceBegin = null;
            try {
                if (null != jsonToken.getExpectedServiceBegin()) {
                    expectedServiceBegin = simpleDateFormat.parse(jsonToken.getExpectedServiceBegin());
                }
            } catch (ParseException e) {
                LOG.error("Failed to parse date, reason={}", e.getLocalizedMessage(), e);
            }

            if (null != jsonPurchaseOrder.getPresentOrderState()) {
                /* Set the old state before resetting. */
                purchaseOrder.addOrderState(jsonPurchaseOrder.getPresentOrderState());
            }

            /* Success in transaction. Change status to VB to initialize. */
            purchaseOrder
                .addOrderState(PurchaseOrderStateEnum.VB)
                .setTokenNumber(jsonToken.getToken())
                .setDisplayToken(jsonToken.getDisplayToken())
                .setExpectedServiceBegin(expectedServiceBegin);
            purchaseOrderManager.save(purchaseOrder);
            executorService.submit(() -> updatePurchaseOrderWithUserDetail(purchaseOrder));
            userAddressService.updateLastUsedAddress(jsonPurchaseOrder.getUserAddressId(), qid);

            /* Decrease inventory when purchase successful. */
            for (String productId : productPurchases.keySet()) {
                storeProductService.changeInventoryCount(productId, productPurchases.get(productId));
            }

            doActionBasedOnQueueStatus(bizStore.getCodeQR(), purchaseOrder, tokenQueueService.findByCodeQR(bizStore.getCodeQR()), null);
            jsonPurchaseOrder
                .setToken(purchaseOrder.getTokenNumber())
                .setDisplayToken(purchaseOrder.getDisplayToken())
                .setServingNumber(jsonToken.getServingNumber())
                .setDisplayServingNumber(jsonToken.getDisplayServingNumber())
                .setExpectedServiceBegin(jsonPurchaseOrder.getExpectedServiceBegin())
                .setTransactionId(purchaseOrder.getTransactionId())
                .setPresentOrderState(purchaseOrder.getOrderStates().get(purchaseOrder.getOrderStates().size() - 1))
                .setCreated(DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
                .setBizStoreId(bizStore.getId())
                .setBusinessType(bizStore.getBusinessType())
                .setPaymentStatus(purchaseOrder.getPaymentStatus());
            subscribeTopicService.addSubscribedTopic(qid, bizStore);
            LOG.debug("JsonPurchaseOrder={}", jsonPurchaseOrder);
        } catch (FailedTransactionException e) {
            LOG.error("Failed transaction on creating order reason={}", e.getLocalizedMessage(), e);
            purchaseOrder.addOrderState(PurchaseOrderStateEnum.FO);
            purchaseOrderManager.save(purchaseOrder);
            throw new PurchaseOrderFailException("Failed creating order");
        } catch (StoreInActiveException e) {
            LOG.warn("Store is offline bizStoreId={} reason={}", jsonPurchaseOrder.getBizStoreId(), e.getLocalizedMessage());
            throw e;
        } catch (Exception e) {
            LOG.error("Failed creating order reason={}", e.getLocalizedMessage(), e);
            throw new PurchaseOrderFailException("Failed creating order");
        }
    }

    private void setCustomerPhoneAndAddress(String qid, JsonPurchaseOrder jsonPurchaseOrder) {
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
        if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
            UserProfileEntity guardianUserProfile = accountService.checkUserExistsByPhone(userProfile.getGuardianPhone());
            jsonPurchaseOrder.setCustomerPhone(guardianUserProfile.getPhone());

            UserAddressEntity userAddress = userAddressService.findOneUserAddress(guardianUserProfile.getQueueUserId());
            jsonPurchaseOrder.setUserAddressId(null == userAddress ? null : userAddress.getId());
        } else {
            jsonPurchaseOrder.setCustomerPhone(userProfile.getPhone());
        }
    }

    /** Days between now and previous visit. */
    private Long daysBetweenService(String qid, BizStoreEntity bizStore) {
        Date lastVisited = purchaseOrderManagerJDBC.clientVisitedStoreAndServicedDate(bizStore.getCodeQR(), qid);
        if (null == lastVisited) {
            return null;
        }
        return DateUtil.getDaysBetween(lastVisited, DateUtil.nowDate());
    }

    @Mobile
    public PurchaseOrderEntity applyCoupon(String qid, String transactionId, String couponId) {
        try {
            PurchaseOrderEntity purchaseOrder = removeCoupon(qid, transactionId);
            switch (purchaseOrder.getPaymentStatus()) {
                case MP:
                case PA:
                case FP:
                case PC:
                case PR:
                    LOG.error("Cannot apply coupon {} {}", purchaseOrder.getTransactionId(), purchaseOrder.getPaymentStatus());
                    throw new CouponCannotApplyException("Cannot apply coupon");
            }

            if (purchaseOrder.isDiscountedPurchase()) {
                LOG.error("Cannot apply coupon when already discounted {} {}", purchaseOrder.getTransactionId(), purchaseOrder.getPaymentStatus());
                throw new CouponCannotApplyException("Cannot apply coupon when already discounted");
            }

            CouponEntity coupon = couponService.findById(couponId);
            if (StringUtils.isNotBlank(coupon.getQid()) && !coupon.getQid().equalsIgnoreCase(qid)) {
                UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
                if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
                    UserProfileEntity guardianProfile = accountService.checkUserExistsByPhone(userProfile.getGuardianPhone());
                    if (!coupon.getQid().equalsIgnoreCase(guardianProfile.getQueueUserId())) {
                        throw new CouponCannotApplyException("Cannot apply coupon " + coupon.getCouponCode());
                    }
                }
                purchaseOrder.setCouponAddedByQid(qid);
            }

            switch (coupon.getDiscountType()) {
                case F:
                    purchaseOrder.setStoreDiscount(coupon.getDiscountAmount());
                    break;
                case P:
                    int discountToApply = Integer.parseInt(purchaseOrder.getOrderPrice()) * coupon.getDiscountAmount() / 100;
                    purchaseOrder.setStoreDiscount(discountToApply);
                    break;
            }

            int afterDiscount = Integer.parseInt(purchaseOrder.getOrderPrice()) - purchaseOrder.getStoreDiscount();
            purchaseOrder
                .setOrderPrice(String.valueOf(afterDiscount))
                .setCouponId(couponId);
            purchaseOrderManager.save(purchaseOrder);

            /* Mark coupon inactive when not set for multi use. */
            if (!coupon.isMultiUse()) {
                coupon.inActive();
                couponService.save(coupon);
            }
            return purchaseOrder;
        } catch (Exception e) {
            LOG.error("Failed applying coupon {} {} {}", qid, transactionId, couponId);
            throw new RuntimeException("Failed applying coupon");
        }
    }

    @Mobile
    public PurchaseOrderEntity applyCouponByMerchant(String qid, String transactionId, String couponId, String couponAddedByQid) {
        Assert.hasText(couponAddedByQid, "Coupon being added should not be null");

        PurchaseOrderEntity purchaseOrder = removeCoupon(qid, transactionId);
        switch (purchaseOrder.getPaymentStatus()) {
            case MP:
            case PA:
            case FP:
            case PC:
            case PR:
                LOG.error("Cannot apply coupon {} {}", purchaseOrder.getTransactionId(), purchaseOrder.getPaymentStatus());
                throw new CouponCannotApplyException("Cannot apply coupon");
        }

        CouponEntity coupon = couponService.findById(couponId);
        switch (coupon.getDiscountType()) {
            case F:
                purchaseOrder.setStoreDiscount(coupon.getDiscountAmount());
                break;
            case P:
                int discountToApply = Integer.parseInt(purchaseOrder.getOrderPrice()) * coupon.getDiscountAmount() / 100;
                purchaseOrder.setStoreDiscount(discountToApply);
                break;
        }

        int afterDiscount = Integer.parseInt(purchaseOrder.getOrderPrice()) - purchaseOrder.getStoreDiscount();
        purchaseOrder
            .setOrderPrice(String.valueOf(afterDiscount))
            .setCouponId(couponId)
            .setCouponAddedByQid(couponAddedByQid);
        purchaseOrderManager.save(purchaseOrder);

        /* Mark coupon inactive when not set for multi use. */
        if (!coupon.isMultiUse()) {
            coupon.inActive();
            couponService.save(coupon);
        }
        return purchaseOrder;
    }

    @Mobile
    public PurchaseOrderEntity removeCoupon(String qid, String transactionId) {
        PurchaseOrderEntity purchaseOrder = findByQidAndTransactionId(qid, transactionId);
        switch (purchaseOrder.getPaymentStatus()) {
            case MP:
            case PA:
            case FP:
            case PC:
            case PR:
                LOG.error("Cannot remove coupon {} {}", purchaseOrder.getTransactionId(), purchaseOrder.getPaymentStatus());
                throw new CouponRemovalException("Cannot remove coupon");
        }

        if (purchaseOrder.getQueueUserId().equalsIgnoreCase(qid) && StringUtils.isNotBlank(purchaseOrder.getCouponId())) {
            purchaseOrder
                .setOrderPrice(String.valueOf(Integer.parseInt(purchaseOrder.getOrderPrice()) + purchaseOrder.getStoreDiscount()))
                .setCouponId(null)
                .setCouponAddedByQid(null)
                .setStoreDiscount(0);

            purchaseOrderManager.save(purchaseOrder);
        }
        return purchaseOrder;
    }

    @Mobile
    public void updatePurchaseOrderWithToken(int token, String displayToken, String expectedServiceBeginStr, String transactionId) {
        Date expectedServiceBegin = new Date();
        if (StringUtils.isNotBlank(expectedServiceBeginStr)) {
            try {
                expectedServiceBegin = simpleDateFormat.parse(expectedServiceBeginStr);
            } catch (ParseException e) {
                LOG.error("Parse date={} error reason={}", expectedServiceBeginStr, e.getLocalizedMessage(), e);
            }
        }
        purchaseOrderManager.updatePurchaseOrderWithToken(token, displayToken, expectedServiceBegin, transactionId);
    }

    @Mobile
    public void deleteReferenceToTransactionId(String transactionId) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findByTransactionId(transactionId);
        //Process all steps below for DeliveryMode QS only
        revertAppliedCouponIfAny(purchaseOrder);
        purchaseOrderProductManager.removePurchaseOrderProduct(purchaseOrder.getId());
        purchaseOrderManager.removePurchaseOrderForService(transactionId);
    }

    /**
     * Revert coupon used.
     * Refer coupon apply {@link #applyCoupon}
     */
    private void revertAppliedCouponIfAny(PurchaseOrderEntity purchaseOrder) {
        if (StringUtils.isNotBlank(purchaseOrder.getCouponId())) {
            CouponEntity coupon = couponService.findById(purchaseOrder.getCouponId());
            if (coupon == null) {
                /*
                 * Added condition when there are two cancel request comes in milli-seconds apart.
                 * Though should be managed by Client, Server needs to manage this error if it occurs.
                 */
                LOG.warn("No coupon exists couponId={} purchaseOrder={}", purchaseOrder.getCouponId(), purchaseOrder.getId());
            } else if (!coupon.isMultiUse() && !coupon.isActive() && purchaseOrder.getQueueUserId().equalsIgnoreCase(coupon.getQid())) {
                coupon.active();
                couponService.save(coupon);
            }
        }
    }

    @Mobile
    public void cancelOrderWhenBackedAwayFromGatewayForOrder(String transactionId) {
        purchaseOrderManager.cancelOrderWhenBackedAwayFromGateway(transactionId);
    }

    @Mobile
    public void populateWithCFToken(JsonPurchaseOrder jsonPurchaseOrder, PurchaseOrderEntity purchaseOrder) {
        jsonPurchaseOrder.setJsonResponseWithCFToken(createTokenForPurchaseOrder(purchaseOrder.orderPriceForTransaction(), purchaseOrder.getTransactionId()));
    }

    @Mobile
    public JsonResponseWithCFToken createTokenForPurchaseOrder(String orderAmount, String transactionId) {
        JsonRequestPurchaseOrderCF jsonRequestPurchaseOrderCF = new JsonRequestPurchaseOrderCF()
            .setOrderAmount(orderAmount)
            .setOrderId(transactionId);
        return cashfreeService.createTokenForPurchaseOrder(jsonRequestPurchaseOrderCF);
    }

    @Mobile
    public JsonPurchaseOrder partialCounterPayment(JsonPurchaseOrder jsonPurchaseOrder, String qid) {
        LOG.info("Partial payment for transactionId={} partialPayment={} by qid={}",
            jsonPurchaseOrder.getTransactionId(),
            jsonPurchaseOrder.getPartialPayment(),
            qid);

        PurchaseOrderEntity purchaseOrderOriginal = findByTransactionIdAndBizStore(jsonPurchaseOrder.getTransactionId(), jsonPurchaseOrder.getBizStoreId());

        /* When partial amount is equal to full amount, mark it as counter payment. */
        if (purchaseOrderOriginal.getOrderPrice().equalsIgnoreCase(jsonPurchaseOrder.getPartialPayment())) {
            return counterPayment(jsonPurchaseOrder, qid);
        }

        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.updateWithPartialCounterPayment(
            jsonPurchaseOrder.getPartialPayment(),
            jsonPurchaseOrder.getTransactionId(),
            jsonPurchaseOrder.getBizStoreId(),
            "Counter Partial via " + jsonPurchaseOrder.getPaymentMode().getDescription(),
            jsonPurchaseOrder.getPaymentMode(),
            qid);

        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
        doActionBasedOnQueueStatus(purchaseOrder.getCodeQR(), purchaseOrder, tokenQueue, null);
        return couponService.addCouponInformationIfAny(new JsonPurchaseOrder(purchaseOrder, userAddressService.findById(purchaseOrder.getUserAddressId())));
    }

    @Mobile
    public JsonPurchaseOrder counterPayment(JsonPurchaseOrder jpo, String qid) {
        LOG.info("Counter payment for transactionId={} partialPayment={} by qid={}", jpo.getTransactionId(), jpo.getPartialPayment(), qid);

        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.updateWithCounterPayment(
            jpo.getTransactionId(),
            jpo.getBizStoreId(),
            "Counter via " + jpo.getPaymentMode().getDescription(),
            jpo.getPaymentMode(),
            qid);

        return couponService.addCouponInformationIfAny(new JsonPurchaseOrder(purchaseOrder, userAddressService.findById(purchaseOrder.getUserAddressId())));
    }

    private void updatePurchaseOrderWithUserDetail(PurchaseOrderEntity purchaseOrder) {
        Assertions.assertNotNull(purchaseOrder.getId(), "PurchaseOrder should have been persisted before executing the code");
        if (StringUtils.isNotBlank(purchaseOrder.getQueueUserId())) {
            UserProfileEntity userProfile = accountService.findProfileByQueueUserId(purchaseOrder.getQueueUserId());
            purchaseOrder.setCustomerName(userProfile.getName());
            LOG.debug("Updated customer name purchaseOrder={}", purchaseOrder);
            purchaseOrderManager.save(purchaseOrder);
        }
    }

    /* Invokes a refresh when some event or activity happens that needs to be notified. */
    public void forceRefreshOnSomeActivity(String codeQR, String transactionId) {
        PurchaseOrderEntity purchaseOrder = findByTransactionId(transactionId);
        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
        doActionBasedOnQueueStatus(codeQR, purchaseOrder, tokenQueue, null);
    }

    private void doActionBasedOnQueueStatus(String codeQR, PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue, String goTo) {
        switch (purchaseOrder.getPresentOrderState()) {
            case IN:
                break;
            case PC:
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, purchaseOrder.getTokenNumber());
                break;
            case VB:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
                break;
            case IB:
                break;
            case FO:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, purchaseOrder.getTokenNumber());
                break;
            case PO:
                switch (tokenQueue.getQueueStatus()) {
                    case D:
                        tokenQueue.setQueueStatus(QueueStatusEnum.R);
                        tokenQueueManager.changeQueueStatus(codeQR, QueueStatusEnum.R);
                        break;
                    case S:
                        tokenQueue.setQueueStatus(QueueStatusEnum.S);
                        break;
                    case R:
                        tokenQueue.setQueueStatus(QueueStatusEnum.R);
                        break;
                    default:
                        tokenQueue.setQueueStatus(QueueStatusEnum.N);
                        break;
                }
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
                break;
            case PR:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, purchaseOrder.getTokenNumber());
                break;
            case RP:
            case RD:
            case OW:
            case LO:
            case FD:
            case OD:
            case DA:
            case CO:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, purchaseOrder.getTokenNumber());
                break;
            default:
                LOG.error("Reached un-supported condition {}", purchaseOrder.getPresentOrderState());
                throw new UnsupportedOperationException("Reached un-supported condition");
        }
    }

    /** Send FCM message to Topic asynchronously. */
    private void sendMessageToTopic(String codeQR, PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue, String goTo) {
        switch (tokenQueue.getBusinessType().getMessageOrigin()) {
            case O:
                executorService.submit(() -> invokeThreadSendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo));
                break;
            case Q:
                QueueStatusEnum queueStatus;
                switch (tokenQueue.getQueueStatus()) {
                    case D:
                    case R:
                        queueStatus = QueueStatusEnum.R;
                        break;
                    case S:
                        queueStatus = QueueStatusEnum.S;
                        break;
                    default:
                        queueStatus = QueueStatusEnum.N;
                        break;
                }
                executorService.submit(() -> tokenQueueService.invokeThreadSendMessageToTopic(codeQR, queueStatus, tokenQueue, goTo, purchaseOrder.getDisplayToken()));
                break;
            default:
                LOG.error("Reached unreachable condition {}", tokenQueue.getBusinessType().getMessageOrigin());
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }

    /** Send FCM message to person with specific token number asynchronously. */
    private void sendMessageToSelectedTokenUser(String codeQR, PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue, String goTo, int tokenNumber) {
        switch (tokenQueue.getBusinessType().getMessageOrigin()) {
            case O:
                executorService.submit(() -> invokeThreadSendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, tokenNumber));
                break;
            case Q:
                //Do Nothing
                break;
            default:
                LOG.error("Reached unreachable condition {}", tokenQueue.getBusinessType().getMessageOrigin());
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }

    private List<PurchaseOrderEntity> findAllOpenOrder(String qid) {
        return purchaseOrderManager.findAllOpenOrder(qid);
    }

    @Mobile
    public List<JsonTokenAndQueue> findAllOpenOrderAsJson(String qid) {
        Assert.isTrue(Validate.isValidQid(qid), "Should be a valid qid " + qid);

        List<PurchaseOrderEntity> purchaseOrders = findAllOpenOrder(qid);
        List<UserProfileEntity> dependentUserProfiles = accountService.findDependentProfiles(qid);
        for (UserProfileEntity userProfile : dependentUserProfiles) {
            purchaseOrders.addAll(findAllOpenOrder(userProfile.getQueueUserId()));
        }

        List<JsonTokenAndQueue> jsonTokenAndQueues = new ArrayList<>();
        populateJsonTokenAndQueueWithOrderDetail(purchaseOrders, jsonTokenAndQueues);

        return jsonTokenAndQueues;
    }

    private void populateJsonTokenAndQueueWithOrderDetail(List<PurchaseOrderEntity> purchaseOrders, List<JsonTokenAndQueue> jsonTokenAndQueues) {
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(purchaseOrder.getCodeQR());
            bizStore.setStoreHours(storeHourManager.findAll(bizStore.getId()));
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(purchaseOrder.getCodeQR());

            JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue(purchaseOrder, tokenQueue, bizStore);
            jsonTokenAndQueues.add(jsonTokenAndQueue);
        }
    }

    @Mobile
    public List<JsonTokenAndQueue> findPendingPurchaseOrderAsJson(String qid) {
        Assert.isTrue(Validate.isValidQid(qid), "Should be a valid qid " + qid);

        List<PurchaseOrderEntity> purchaseOrders = purchaseOrderManagerJDBC.findAllOrderWithState(qid, PurchaseOrderStateEnum.PO);
        List<UserProfileEntity> dependentUserProfiles = accountService.findDependentProfiles(qid);
        for (UserProfileEntity userProfile : dependentUserProfiles) {
            purchaseOrders.addAll(purchaseOrderManagerJDBC.findAllOrderWithState(userProfile.getQueueUserId(), PurchaseOrderStateEnum.PO));
        }

        List<JsonTokenAndQueue> jsonTokenAndQueues = new ArrayList<>();
        populateJsonTokenAndQueueWithOrderDetail(purchaseOrders, jsonTokenAndQueues);

        return jsonTokenAndQueues;
    }

    private List<PurchaseOrderEntity> findAllOpenOrderByCodeQR(String codeQR) {
        return purchaseOrderManager.findAllOpenOrderByCodeQR(codeQR);
    }

    public List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR) {
        return purchaseOrderManager.findAllOrderByCodeQR(codeQR);
    }

    /** Finds historical orders. */
    public List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR, int durationInDays) {
        return purchaseOrderManagerJDBC.findAllOrderByCodeQR(codeQR, durationInDays);
    }

    private List<PurchaseOrderEntity> findAllPastDeliveredOrCancelledOrders(String qid, BusinessTypeEnum ignoreBusinessType) {
        return purchaseOrderManager.findAllPastDeliveredOrCancelledOrders(qid, ignoreBusinessType);
    }

    @Mobile
    public String findAllOpenOrderByCodeAsJson(String codeQR) {
        List<JsonPurchaseOrder> jsonPurchaseOrders = new ArrayList<>();
        List<PurchaseOrderEntity> purchaseOrders = findAllOpenOrderByCodeQR(codeQR);
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            populateRelatedToPurchaseOrder(jsonPurchaseOrders, purchaseOrder);
        }

        return new JsonPurchaseOrderList().setPurchaseOrders(jsonPurchaseOrders).asJson();
    }

    @Mobile
    public String findAllOrderByCodeAsJson(String codeQR) {
        List<JsonPurchaseOrder> jsonPurchaseOrders = new ArrayList<>();
        List<PurchaseOrderEntity> purchaseOrders = findAllOrderByCodeQR(codeQR);
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            populateRelatedToPurchaseOrder(jsonPurchaseOrders, purchaseOrder);
        }

        return new JsonPurchaseOrderList().setPurchaseOrders(jsonPurchaseOrders).asJson();
    }

    @Mobile
    public String findAllOrderByCodeHistoricalAsJson(String codeQR) {
        List<JsonPurchaseOrder> jsonPurchaseOrders = new ArrayList<>();
        List<PurchaseOrderEntity> purchaseOrders = findAllOrderByCodeQR(codeQR, limitedToDays);
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            UserProfileEntity userProfile = accountService.findProfileByQueueUserId(purchaseOrder.getQueueUserId());
            purchaseOrder
                .setCustomerName(userProfile.getName())
                .setCustomerPhone(StringUtils.isBlank(userProfile.getGuardianPhone()) ? userProfile.getPhone() : userProfile.getGuardianPhone());

            populateHistoricalJsonPurchaseOrder(jsonPurchaseOrders, purchaseOrder);
        }

        return new JsonPurchaseOrderList().setPurchaseOrders(jsonPurchaseOrders).asJson();
    }

    /** This is for historical orders placed today, other past orders have moved in archive. */
    @Mobile
    public JsonPurchaseOrderHistoricalList findAllPastDeliveredOrCancelledOrdersAsJson(String qid) {
        JsonPurchaseOrderHistoricalList jsonPurchaseOrderHistoricalList = new JsonPurchaseOrderHistoricalList();

        List<PurchaseOrderEntity> purchaseOrders = findAllPastDeliveredOrCancelledOrders(qid, BusinessTypeEnum.DO);
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManager.getAllByPurchaseOrderId(purchaseOrder.getId());
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(purchaseOrder.getCodeQR());
            UserAddressEntity userAddress = userAddressService.findById(purchaseOrder.getUserAddressId());
            jsonPurchaseOrderHistoricalList.addJsonPurchaseOrderHistorical(new JsonPurchaseOrderHistorical(purchaseOrder, purchaseOrderProducts, userAddress, bizStore));
        }
        purchaseOrders = purchaseOrderManagerJDBC.getByQid(qid, BusinessTypeEnum.DO);
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManagerJDBC.getByPurchaseOrderId(purchaseOrder.getId());
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(purchaseOrder.getCodeQR());
            UserAddressEntity userAddress = userAddressService.findById(purchaseOrder.getUserAddressId());
            jsonPurchaseOrderHistoricalList.addJsonPurchaseOrderHistorical(new JsonPurchaseOrderHistorical(purchaseOrder, purchaseOrderProducts, userAddress, bizStore));
        }
        LOG.info("Order history size {}", jsonPurchaseOrderHistoricalList.getJsonPurchaseOrderHistoricals().size());
        return jsonPurchaseOrderHistoricalList;
    }

    private void populateRelatedToPurchaseOrder(List<JsonPurchaseOrder> jsonPurchaseOrders, PurchaseOrderEntity purchaseOrder) {
        jsonPurchaseOrders.add(purchaseOrderProductService.populateJsonPurchaseOrder(purchaseOrder));
    }

    private void populateHistoricalJsonPurchaseOrder(List<JsonPurchaseOrder> jsonPurchaseOrders, PurchaseOrderEntity purchaseOrder) {
        jsonPurchaseOrders.add(purchaseOrderProductService.populateHistoricalJsonPurchaseOrder(purchaseOrder));
    }

    /** Formulates and send messages to FCM. */
    private void invokeThreadSendMessageToTopic(
        String codeQR,
        PurchaseOrderEntity purchaseOrder,
        TokenQueueEntity tokenQueue,
        String goTo
    ) {
        LOG.debug("Sending message codeQR={} goTo={}", codeQR, goTo);

        int timeout = 2;
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            LOG.debug("Topic being sent to {}", tokenQueue.getCorrectTopic(purchaseOrder.getPresentOrderState()) + UNDER_SCORE + deviceType.name());
            JsonMessage jsonMessage = new JsonMessage(tokenQueue.getCorrectTopic(purchaseOrder.getPresentOrderState()) + UNDER_SCORE + deviceType.name());
            JsonData jsonData = new JsonTopicData(purchaseOrder.getBusinessType().getMessageOrigin(), tokenQueue.getFirebaseMessageType()).getJsonTopicOrderData()
                .setLastNumber(tokenQueue.getLastNumber())
                .setCurrentlyServing(tokenQueue.getCurrentlyServing())
                .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
                .setCodeQR(codeQR)
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setGoTo(goTo)
                .setBusinessType(tokenQueue.getBusinessType())
                .setPurchaseOrderState(purchaseOrder.getPresentOrderState());

            /*
             * Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
             * Otherwise we will have to manage more number of topic.
             */
            switch (tokenQueue.getQueueStatus()) {
                case S:
                case R:
                case D:
                    //TODO remove me, added as messages go out fast, before records are propagated to other replica set
                    if (0 != timeout) {
                        try {
                            TimeUnit.SECONDS.sleep(timeout);
                            timeout = 0;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            LOG.error("Failed adding delay reason={}", e.getLocalizedMessage());
                        }
                    }

                    /*
                     * This message has to go as the business with the opened queue
                     * will not get any update if some one joins. FCM makes sure the message is dispersed.
                     */
                    long confirmedWaiting = purchaseOrderManager.countAllPlacedOrder(codeQR);
                    if (DeviceTypeEnum.I == deviceType) {
                        jsonMessage.getNotification()
                            .setBody("Now has " + tokenQueue.totalWaiting() + " order. Confirmed order " + confirmedWaiting)
                            .setTitle(tokenQueue.getDisplayName() + " Queue");
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now has " + tokenQueue.totalWaiting() + " order. Confirmed order " + confirmedWaiting)
                            .setTitle(tokenQueue.getDisplayName() + " Queue");
                    }
                    break;
                default:
                    if (DeviceTypeEnum.I == deviceType) {
                        jsonMessage.getNotification()
                            .setBody("Now Serving " + purchaseOrder.getDisplayToken())
                            .setLocKey("serving")
                            .setLocArgs(new String[]{String.valueOf(tokenQueue.getCurrentlyServing())})
                            .setTitle(tokenQueue.getDisplayName());
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now Serving " + purchaseOrder.getDisplayToken())
                            .setTitle(tokenQueue.getDisplayName());
                    }
            }

            jsonMessage.setData(jsonData);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.debug("Sent topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
            }
        }
    }

    /**
     * When servicing token that's out of order or sequence. Send message as the selected token is being served
     * and mark it Personal.
     */
//    private void invokeThreadSendMessageToSelectedTokenUser(
//        String codeQR,
//        PurchaseOrderEntity purchaseOrder,
//        TokenQueueEntity tokenQueue,
//        String goTo,
//        int tokenNumber
//    ) {
//        LOG.debug("Sending personal message codeQR={} goTo={} tokenNumber={}", codeQR, goTo, tokenNumber);
//
//        List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.findAll(purchaseOrder.getQueueUserId(), purchaseOrder.getDid());
//        for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
//            LOG.debug("Personal message of being served is sent to qid={} deviceId={} deviceType={} with tokenNumber={}",
//                registeredDevice.getQueueUserId(),
//                registeredDevice.getDeviceId(),
//                registeredDevice.getDeviceType(),
//                tokenNumber);
//
//            JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
//            JsonData jsonData = new JsonTopicQueueData(FirebaseMessageTypeEnum.P, MessageOriginEnum.O)
//                .setLastNumber(tokenQueue.getLastNumber())
//                .setCurrentlyServing(tokenNumber)
//                .setCodeQR(codeQR)
//                .setQueueStatus(tokenQueue.getQueueStatus())
//                .setGoTo(goTo)
//                .setBusinessType(tokenQueue.getBusinessType());
//
//            /*
//             * Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
//             * As this is a personal message when server is planning to serve a specific token.
//             */
//            switch (tokenQueue.getQueueStatus()) {
//                case S:
//                case R:
//                case D:
//                    LOG.warn("Skipped sending personal message as queue status is not 'Next' but queueStatus={}", tokenQueue.getQueueStatus());
//                    break;
//                case P:
//                case C:
//                    LOG.error("Cannot reach this state codeQR={}, queueStatus={}", codeQR, tokenQueue.getQueueStatus());
//                    return;
//                case N:
//                default:
//                    LOG.debug("Personal device is of type={} did={} token={}",
//                        registeredDevice.getDeviceType(),
//                        registeredDevice.getDeviceId(),
//                        registeredDevice.getToken());
//
//                    if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
//                        jsonMessage.getNotification()
//                            .setBody("Now Serving " + tokenNumber)
//                            .setLocKey("serving")
//                            .setLocArgs(new String[]{String.valueOf(tokenNumber)})
//                            .setTitle(tokenQueue.getDisplayName());
//                    } else {
//                        jsonMessage.setNotification(null);
//                        jsonData.setBody("Now Serving " + tokenNumber)
//                            .setTitle(tokenQueue.getDisplayName());
//                    }
//            }
//
//            jsonMessage.setData(jsonData);
//
//            LOG.debug("Personal FCM message to be sent={}", jsonMessage);
//            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
//            if (!fcmMessageBroadcast) {
//                LOG.warn("Personal broadcast failed message={}", jsonMessage.asJson());
//            } else {
//                LOG.debug("Sent Personal topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
//            }
//        }
//    }
    private void invokeThreadSendMessageToSelectedTokenUser(
        String codeQR,
        PurchaseOrderEntity purchaseOrder,
        TokenQueueEntity tokenQueue,
        String goTo,
        int tokenNumber
    ) {
        LOG.debug("Sending personal message codeQR={} goTo={} tokenNumber={}", codeQR, goTo, tokenNumber);

        List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.findAll(purchaseOrder.getQueueUserId(), purchaseOrder.getDid());
        for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
            LOG.debug("Personal message of being served is sent to qid={} deviceId={} deviceType={} with tokenNumber={}",
                registeredDevice.getQueueUserId(),
                registeredDevice.getDeviceId(),
                registeredDevice.getDeviceType(),
                tokenNumber);

            JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
            JsonData jsonData = new JsonTopicOrderData(FirebaseMessageTypeEnum.P, MessageOriginEnum.O)
                .setLastNumber(tokenQueue.getLastNumber())
                .setCurrentlyServing(tokenNumber)
                .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
                .setCodeQR(codeQR)
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setPurchaseOrderState(purchaseOrder.getPresentOrderState())
                .setGoTo(goTo)
                .setBusinessType(tokenQueue.getBusinessType());

            /*
             * Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
             * As this is a personal message when server is planning to serve a specific token.
             */
            String message;
            switch (purchaseOrder.getPresentOrderState()) {
                case PC:
                    message = "Price has changed. Please re-order.";
                    //Rarely will be sent. No message sent until PO
                    break;
                case FO:
                    message = "Apologies as we have failed to place order.";
                    break;
                case OP:
                    message = "Your order " + purchaseOrder.getDisplayToken() + " is being processed.";
                    break;
                case RP:
                    message = "Your order is ready for pickup.";
                    break;
                case RD:
                    message = "Your order is ready for delivery.";
                    break;
                case OW:
                    message = "Your order is on the way.";
                    break;
                case LO:
                    message = "Apologies we have lost your order. We are working on it to find your order.";
                    break;
                case FD:
                    message = "We failed to deliver your order.";
                    break;
                case OD:
                    message = "Your order has been successfully delivered.";
                    break;
                case DA:
                    message = "We are re-attempting to deliver your order.";
                    break;
                case CO:
                    message = "Your order was cancelled.";
                    break;
                default:
                    LOG.error("Failed condition=\"{}\" device is of type={} did={} token={}",
                        purchaseOrder.getPresentOrderState(),
                        registeredDevice.getDeviceType(),
                        registeredDevice.getDeviceId(),
                        registeredDevice.getToken());
                    throw new RuntimeException("Reached unsupported condition " + purchaseOrder.getPresentOrderState());
            }

            if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
                jsonMessage.getNotification()
                    .setBody(message)
                    .setLocKey("serving")
                    .setLocArgs(new String[]{String.valueOf(tokenNumber)})
                    .setTitle(tokenQueue.getDisplayName());
            } else {
                jsonMessage.setNotification(null);
                jsonData.setBody(message)
                    .setTitle(tokenQueue.getDisplayName());
            }

            jsonMessage.setData(jsonData);

            LOG.debug("Personal FCM message to be sent={}", jsonMessage);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Personal broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.debug("Sent Personal topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
            }
        }
    }

    private PurchaseOrderEntity findOne(String codeQR, int tokenNumber) {
        return purchaseOrderManager.findOne(codeQR, tokenNumber);
    }

    /**
     * When business has served a specific token.
     *
     * @param codeQR
     * @param servedNumber
     * @param purchaseOrderState
     * @param goTo               - counter name
     * @param sid                - server device id
     * @param tokenService       - Invoked via Web or Device
     * @return
     */
    public JsonToken updateAndGetNextInQueue(
        String codeQR,
        int servedNumber,
        PurchaseOrderStateEnum purchaseOrderState,
        String goTo,
        String sid,
        TokenServiceEnum tokenService
    ) {
        LOG.info("Update and getting next in queue codeQR={} servedNumber={} purchaseOrderState={} goTo={} sid={}", codeQR, servedNumber, purchaseOrderState, goTo, sid);
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.updateAndGetNextInQueue(codeQR, servedNumber, purchaseOrderState, goTo, sid, tokenService);
        if (null != purchaseOrder) {
            LOG.info("Found queue codeQR={} servedNumber={} purchaseOrderState={} nextToken={}", codeQR, servedNumber, purchaseOrderState, purchaseOrder.getTokenNumber());
            return updateServing(codeQR, QueueStatusEnum.N, purchaseOrder.getId(), goTo);
        }

        LOG.info("Reached condition of not having any more to serve");
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.D);
        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            /* Better to show last number than served number. This is to maintain consistent state. */
            .setToken(tokenQueue.getCurrentlyServing())
            .setDisplayToken(tokenQueue.generateDisplayToken())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
            .setDisplayName(tokenQueue.getDisplayName())
            .setQueueStatus(QueueStatusEnum.D);
    }

    /**
     * Business when starting or re-starting to serve token when QueueState has been either Start or Re-Start.
     *
     * @param codeQR
     * @param goTo   counter name
     * @param sid    server device id
     * @return
     */
    @Mobile
    public JsonToken getNextInQueue(String codeQR, String goTo, String sid) {
        LOG.info("Getting next in queue for codeQR={} goTo={} sid={}", codeQR, goTo, sid);

        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.getNext(codeQR, goTo, sid);
        if (null != purchaseOrder) {
            LOG.info("Found queue codeQR={} token={}", codeQR, purchaseOrder.getTokenNumber());
            JsonToken jsonToken = updateServing(codeQR, QueueStatusEnum.N, purchaseOrder.getId(), goTo);
            //TODO(hth) call can be put in thread
            tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.N);
            return jsonToken;
        }

        /* When nothing is found, return DONE status for the queue. */
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        if (null != tokenQueue) {
            LOG.info("On next, found no one in queue, returning with DONE status");
            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setToken(tokenQueue.getLastNumber())
                .setDisplayToken(tokenQueue.generateDisplayToken())
                .setServingNumber(tokenQueue.getLastNumber())
                .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(QueueStatusEnum.D);
        }

        return null;
    }

    @Mobile
    public JsonToken getThisAsNextInQueue(String codeQR, String goTo, String sid, int token) {
        LOG.info("Getting specific token next in queue for codeQR={} goTo={} sid={} token={}", codeQR, goTo, sid, token);
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.getThisAsNext(codeQR, goTo, sid, token);
        if (null != purchaseOrder) {
            LOG.info("Found queue codeQR={} token={}", codeQR, purchaseOrder.getTokenNumber());

            JsonToken jsonToken = updateThisServing(
                codeQR,
                QueueStatusEnum.N,
                purchaseOrder.getId(),
                goTo);
            //TODO(hth) call can be put in thread
            tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.N);
            return jsonToken;
        }

        return null;
    }

    @Mobile
    public JsonToken updateServing(String codeQR, QueueStatusEnum queueStatus, String id, String goTo) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findById(id);
        TokenQueueEntity tokenQueue = tokenQueueManager.updateServing(codeQR, purchaseOrder.getTokenNumber(), queueStatus);
        sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);

        LOG.info("After sending message to merchant");
        if (purchaseOrder.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue qid={} did={}", purchaseOrder.getQueueUserId(), purchaseOrder.getDid());

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber())
                .setDisplayToken(tokenQueue.generateDisplayToken())
                .setCustomerName(purchaseOrder.getCustomerName())
                .setTransactionId(purchaseOrder.getTransactionId());
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
            .setDisplayName(tokenQueue.getDisplayName())
            .setToken(tokenQueue.getLastNumber())
            .setDisplayToken(tokenQueue.generateDisplayToken())
            .setTransactionId(purchaseOrder.getTransactionId());
    }

    /**
     * This acquires the record of the person being served by server. No one gets informed when the record is
     * acquired other than the person who's record is acquired to be served next.
     */
    @Mobile
    public JsonToken updateThisServing(String codeQR, QueueStatusEnum queueStatus, String id, String goTo) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findById(id);
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
        /*
         * Do not inform anyone other than the person with the
         * token who is being served. This is personal message.
         * of being served out of order/sequence.
         */
        sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, purchaseOrder.getTokenNumber());

        LOG.info("After sending message to business and personal message to user of token");
        if (purchaseOrder.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue qid={} did={}", purchaseOrder.getQueueUserId(), purchaseOrder.getDid());

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(purchaseOrder.getTokenNumber())
                .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber())
                .setDisplayToken(tokenQueue.generateDisplayToken())
                .setCustomerName(purchaseOrder.getCustomerName())
                .setTransactionId(purchaseOrder.getTransactionId());
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setServingNumber(purchaseOrder.getTokenNumber())
            .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
            .setDisplayName(tokenQueue.getDisplayName())
            .setToken(tokenQueue.getLastNumber())
            .setDisplayToken(tokenQueue.generateDisplayToken())
            .setTransactionId(purchaseOrder.getTransactionId());
    }

    @Mobile
    public JsonPurchaseOrderList processedOrderService(
        String codeQR,
        int servedNumber,
        PurchaseOrderStateEnum purchaseOrderState,
        String goTo,
        String sid,
        TokenServiceEnum tokenService
    ) {
        LOG.info("Getting specific token next in queue for codeQR={} goTo={} sid={} token={}", codeQR, goTo, sid, servedNumber);
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findOne(codeQR, servedNumber);
        if (null != purchaseOrder) {
            LOG.info("Found queue codeQR={} token={}", codeQR, purchaseOrder.getTokenNumber());
            switch (purchaseOrder.getPresentOrderState()) {
                case OP:
                    switch (purchaseOrder.getDeliveryMode()) {
                        case HD:
                            purchaseOrder
                                .addOrderState(PurchaseOrderStateEnum.PR)
                                .addOrderState(PurchaseOrderStateEnum.RD);
                            break;
                        case TO:
                            purchaseOrder
                                .addOrderState(PurchaseOrderStateEnum.PR)
                                .addOrderState(PurchaseOrderStateEnum.RP);
                            break;
                        default:
                            LOG.error("Reached unreachable condition, deliveryMode={}", purchaseOrder.getDeliveryMode());
                            throw new UnsupportedOperationException("Reached un-reachable condition for processing order");
                    }
                    break;
                case RP:
                case RD:
                    purchaseOrder
                        .addOrderState(PurchaseOrderStateEnum.OD)
                        .setServiceEndTime(new Date());
                    break;
                default:
                    //Skipped as not supported.
                    break;
            }
            purchaseOrderManager.save(purchaseOrder);
            return markOrderProcessed(codeQR, purchaseOrder, goTo);
        }

        return null;
    }

    private JsonPurchaseOrderList markOrderProcessed(String codeQR, PurchaseOrderEntity purchaseOrder, String goTo) {
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        doActionBasedOnQueueStatus(codeQR, purchaseOrder, tokenQueue, goTo);

//For broadcast message
//        new JsonTopicOrderData(FirebaseMessageTypeEnum.M, MessageOriginEnum.O)
//            .setMessage("Order ready for pickup/delivery")
//            .setLastNumber(tokenQueue.getLastNumber())
//            .setCurrentlyServing(purchaseOrder.getTokenNumber())
//            .setCodeQR(purchaseOrder.getCodeQR())
//            .setQueueStatus(tokenQueue.getQueueStatus())
//            .setGoTo(goTo)
//            .setBusinessType(purchaseOrder.getBusinessType())
//            .setPurchaseOrderState(purchaseOrder.getPresentOrderState());

        List<JsonPurchaseOrder> jsonPurchaseOrders = new ArrayList<>();
        populateRelatedToPurchaseOrder(jsonPurchaseOrders, purchaseOrder);
        return new JsonPurchaseOrderList().setPurchaseOrders(jsonPurchaseOrders);
    }

    @Mobile
    public JsonPurchaseOrder findBy(String qid, String codeQR, int tokenNumber) {
        Assert.isTrue(Validate.isValidQid(qid), "Should be a valid qid " + qid);

        Set<String> qidSet = accountService.findDependentQIDByPhone(qid);
        qidSet.add(qid);
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findBy(qidSet, codeQR, tokenNumber);
        if (null == purchaseOrder) {
            return null;
        }

        List<JsonPurchaseOrder> jsonPurchaseOrders = new ArrayList<>();
        populateRelatedToPurchaseOrder(jsonPurchaseOrders, purchaseOrder);
        return jsonPurchaseOrders.isEmpty() ? null : jsonPurchaseOrders.get(0);
    }

    /** Since review can be done in background. Moved logic to thread. */
    @Mobile
    public boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, String review) {
        executorService.submit(() -> reviewingService(codeQR, token, did, qid, ratingCount, review));
        return true;
    }

    /** Submitting review. */
    private void reviewingService(String codeQR, int token, String did, String qid, int ratingCount, String review) {
        SentimentTypeEnum sentimentType = nlpService.computeSentiment(review);
        boolean reviewSubmitStatus = purchaseOrderManager.reviewService(codeQR, token, qid, ratingCount, review, sentimentType);
        if (!reviewSubmitStatus) {
            //TODO(hth) make sure for Guardian this is taken care. Right now its ignore "GQ" add to MySQL Table
            reviewSubmitStatus = reviewHistoricalService(codeQR, token, qid, ratingCount, review, sentimentType);
        }

        /* Add points on review submission. */
        if (reviewSubmitStatus && StringUtils.isNotBlank(review)) {
            pointEarnedManager.save(new PointEarnedEntity(qid, PointActivityEnum.REV));
        }

        sendMailWhenSentimentIsNegative(codeQR, token, ratingCount, review, sentimentType);

        LOG.info("Review update status={} codeQR={} token={} ratingCount={} did={} qid={} review={}",
            reviewSubmitStatus,
            codeQR,
            token,
            ratingCount,
            did,
            qid,
            review);
    }

    private boolean reviewHistoricalService(
        String codeQR,
        int token,
        String qid,
        int ratingCount,
        String review,
        SentimentTypeEnum sentimentType
    ) {
        return purchaseOrderManagerJDBC.reviewService(codeQR, token, qid, ratingCount, review, sentimentType);
    }

    @Mobile
    public PurchaseOrderEntity findByQidAndTransactionId(String qid, String transactionId) {
        if (StringUtils.isBlank(transactionId)) {
            return null;
        }
        return purchaseOrderManager.findByQidAndTransactionId(qid, transactionId);
    }

    public PurchaseOrderEntity findByTransactionId(String transactionId) {
        if (StringUtils.isBlank(transactionId)) {
            return null;
        }
        return purchaseOrderManager.findByTransactionId(transactionId);
    }

    @Mobile
    public boolean existsTransactionId(String transactionId) {
        if (StringUtils.isBlank(transactionId)) {
            return false;
        }
        return purchaseOrderManager.existsTransactionId(transactionId);
    }

    public PurchaseOrderEntity findByTransactionIdAndBizStore(String transactionId, String bizStoreId) {
        return purchaseOrderManager.findByTransactionIdAndBizStore(transactionId, bizStoreId);
    }

    public PurchaseOrderEntity findHistoricalByTransactionIdAndBizStore(String transactionId, String bizStoreId) {
        return purchaseOrderManagerJDBC.findByTransactionIdAndBizStore(transactionId, bizStoreId);
    }

    public void changePatient(String transactionId, UserProfileEntity userProfile) {
        UserAddressEntity userAddress = userAddressService.findOneUserAddress(userProfile.getQueueUserId());
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.changePatient(transactionId, userProfile, userAddress);
        purchaseOrderProductManager.changePatient(purchaseOrder.getId(), userProfile.getQueueUserId());
    }

    private void sendMailWhenSentimentIsNegative(String codeQR, int token, int ratingCount, String review, SentimentTypeEnum sentimentType) {
        if (SentimentTypeEnum.N == sentimentType) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
            List<BusinessUserEntity> businessUsers = businessUserManager.getAllForBusiness(bizStore.getBizName().getId(), UserLevelEnum.M_ADMIN);

            PurchaseOrderEntity purchaseOrder = findOne(codeQR, token);
            for (BusinessUserEntity businessUser : businessUsers) {
                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put("storeName", purchaseOrder.getDisplayName());
                rootMap.put("reviewerName", purchaseOrder.getCustomerName());
                rootMap.put("reviewerPhone", purchaseOrder.getCustomerPhone());
                rootMap.put("ratingCount", ratingCount);
                rootMap.put("review", review);
                rootMap.put("sentiment", sentimentType.getDescription());

                mailService.sendAnyMail(
                    accountService.findProfileByQueueUserId(businessUser.getQueueUserId()).getEmail(),
                    "Customer Sentiment Watcher",
                    "Review for: " + purchaseOrder.getDisplayName(),
                    rootMap,
                    "mail/reviewSentiment.ftl");
            }
        }
    }

    public boolean isPaid(String transactionId) {
        return purchaseOrderManager.isPaid(transactionId);
    }
}
