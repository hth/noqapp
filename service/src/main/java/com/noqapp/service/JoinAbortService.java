package com.noqapp.service;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apiguardian.api.API.Status.DEPRECATED;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateFormatter;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.common.ComposeMessagesForFCM;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonPurchaseOrderProduct;
import com.noqapp.domain.json.JsonResponse;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.payment.cashfree.JsonResponseWithCFToken;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.TransactionViaEnum;
import com.noqapp.repository.QueueManager;
import com.noqapp.service.exceptions.AlreadyServicedTodayException;
import com.noqapp.service.exceptions.BeforeStartOfStoreException;
import com.noqapp.service.exceptions.ExpectedServiceBeyondStoreClosingHour;
import com.noqapp.service.exceptions.LimitedPeriodException;
import com.noqapp.service.exceptions.PurchaseOrderCancelException;
import com.noqapp.service.exceptions.PurchaseOrderRefundExternalException;
import com.noqapp.service.exceptions.PurchaseOrderRefundPartialException;
import com.noqapp.service.exceptions.QueueAbortPaidPastDurationException;
import com.noqapp.service.exceptions.StoreDayClosedException;
import com.noqapp.service.exceptions.TokenAvailableLimitReachedException;
import com.noqapp.service.exceptions.WaitUntilServiceBegunException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.apiguardian.api.API;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

/**
 * User: hitender
 * Date: 2019-07-13 07:41
 */
@Service
public class JoinAbortService {
    private static final Logger LOG = LoggerFactory.getLogger(JoinAbortService.class);

    private int preventPaidAbortBeforeHours;

    private DeviceService deviceService;
    private TokenQueueService tokenQueueService;
    private PurchaseOrderService purchaseOrderService;
    private QueueManager queueManager;
    private PurchaseOrderProductService purchaseOrderProductService;
    private BizService bizService;
    private BusinessCustomerService businessCustomerService;
    private FirebaseMessageService firebaseMessageService;
    private StoreHourService storeHourService;

    private ExecutorService executorService;

    @Autowired
    public JoinAbortService(
        @Value("${preventPaidAbortBeforeHours:2}")
        int preventPaidAbortBeforeHours,

        DeviceService deviceService,
        TokenQueueService tokenQueueService,
        PurchaseOrderService purchaseOrderService,
        QueueManager queueManager,
        PurchaseOrderProductService purchaseOrderProductService,
        BizService bizService,
        BusinessCustomerService businessCustomerService,
        FirebaseMessageService firebaseMessageService,
        StoreHourService storeHourService
    ) {
        this.preventPaidAbortBeforeHours = preventPaidAbortBeforeHours;

        this.deviceService = deviceService;
        this.tokenQueueService = tokenQueueService;
        this.purchaseOrderService = purchaseOrderService;
        this.queueManager = queueManager;
        this.purchaseOrderProductService = purchaseOrderProductService;
        this.bizService = bizService;
        this.businessCustomerService = businessCustomerService;
        this.firebaseMessageService = firebaseMessageService;
        this.storeHourService = storeHourService;

        /* For executing in order of sequence. */
        this.executorService = newSingleThreadExecutor();
    }

    @API(status = DEPRECATED, since = "1.3.121")
    @Mobile
    public JsonToken joinQueueObsolete(String did, BizStoreEntity bizStore, TokenServiceEnum tokenService) {
        return joinQueue(did, null, null, bizStore, tokenService);
    }

    @Mobile
    public JsonToken joinQueue(String did, String qid, String guardianQid, BizStoreEntity bizStore, TokenServiceEnum tokenService) {
        LOG.info("joinQueue codeQR={} did={} qid={} guardianQid={}", bizStore.getCodeQR(), did, qid, guardianQid);
        JsonToken jsonToken = tokenQueueService.getNextToken(did, qid, guardianQid, bizStore, tokenService);
        checkLimitationEncountered(bizStore.getCodeQR(), jsonToken);

        return jsonToken;
    }

    /** Invoke by client and hence has a token service as Client. */
    @Mobile
    public JsonToken payBeforeJoinQueue(String codeQR, String did, String qid, String guardianQid, BizStoreEntity bizStore, TokenServiceEnum tokenService) {
        JsonToken jsonToken = tokenQueueService.getPaidNextToken(codeQR, did, qid, guardianQid, bizStore.getAverageServiceTime(), tokenService);
        checkLimitationEncountered(codeQR, jsonToken);

        JsonPurchaseOrder jsonPurchaseOrder;
        PurchaseOrderEntity purchaseOrder = purchaseOrderService.findByTransactionId(jsonToken.getTransactionId());
        if (null == purchaseOrder) {
            jsonPurchaseOrder = createNewJsonPurchaseOrder(qid, jsonToken, bizStore);
            LOG.info("joinQueue codeQR={} did={} qid={} guardianQid={}", codeQR, did, qid, guardianQid);
            purchaseOrderService.createOrder(jsonPurchaseOrder, qid, did, TokenServiceEnum.C);
            queueManager.updateWithTransactionId(codeQR, qid, jsonToken.getToken(), jsonPurchaseOrder.getTransactionId());
        } else {
            LOG.info("Found exists purchaseOrder with transactionId={}", purchaseOrder.getTransactionId());
            jsonPurchaseOrder = purchaseOrderProductService.populateJsonPurchaseOrder(purchaseOrder);
        }
        jsonToken.setJsonPurchaseOrder(jsonPurchaseOrder);
        return jsonToken;
    }

    public void checkLimitationEncountered(String codeQR, JsonToken jsonToken) {
        if (null != jsonToken.getQueueJoinDenied()) {
            switch (jsonToken.getQueueJoinDenied()) {
                case A:
                    throw new ExpectedServiceBeyondStoreClosingHour("Your service time exceeds store closing hour " + codeQR);
                case B:
                    throw new BeforeStartOfStoreException("Please correct your system time to match your timezone " + codeQR);
                case C:
                    throw new StoreDayClosedException("Store is closed today codeQR " + codeQR);
                case T:
                    throw new AlreadyServicedTodayException("You have been serviced today");
                case W:
                    throw new WaitUntilServiceBegunException("Cancelled service. Please wait until service has begun to reclaim your spot if available.");
                case X:
                    throw new LimitedPeriodException("Please wait until set number of days before using this service");
                case L:
                    throw new TokenAvailableLimitReachedException("Token limit reached");
                default:
                    LOG.error("Reached unsupported condition={}", jsonToken.getQueueJoinDenied());
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
        }
    }

    /**
     * Invoke by client and hence has a token service as Client.
     * Note: When client skips, the state is VB (Valid before purchase). PO when Paid. After skip, if client make a Paid API request, server
     * sends VB then client is trying to pay when its should skip. Hence send SKIP CFToken.
     */
    @Mobile
    public JsonToken skipPayBeforeJoinQueue(String codeQR, String did, String qid, String guardianQid, BizStoreEntity bizStore, TokenServiceEnum tokenService) {
        JsonToken jsonToken = payBeforeJoinQueue(codeQR, did, qid, guardianQid, bizStore, tokenService);

        if (!purchaseOrderService.existsTransactionId(jsonToken.getTransactionId())) {
            JsonToken jsonTokenUpdatedWithPayment = updateWhenPaymentSuccessful(codeQR, jsonToken.getJsonPurchaseOrder().getTransactionId());
            jsonTokenUpdatedWithPayment.setJsonPurchaseOrder(jsonToken.getJsonPurchaseOrder());
            return jsonTokenUpdatedWithPayment;
        }
        return jsonToken;
    }

    @Mobile
    public JsonResponse abortQueue(String codeQR, String did, String qid, int requesterTime) {
        LOG.info("abortQueue codeQR={} did={} qid={}", codeQR, did, qid);
        QueueEntity queue = queueManager.findToAbort(codeQR, qid);
        if (queue == null) {
            LOG.warn("Not joined to queue qid={}, ignore abort", qid);
            return new JsonResponse(false);
        }

        try {
            if (StringUtils.isNotBlank(queue.getTransactionId())) {
                BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
                ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());
                StoreHourEntity storeHour = storeHourService.findStoreHour(bizStore.getId(), zonedDateTime.getDayOfWeek());
                LocalTime localTime = DateFormatter.addHours(DateFormatter.getLocalTime(requesterTime), preventPaidAbortBeforeHours);
                if (DateFormatter.getTimeIn24HourFormat(localTime) > storeHour.getStartHour()) {
                    LOG.warn("Failed as aborting paid transaction within {} hrs duration", preventPaidAbortBeforeHours);
                    throw new QueueAbortPaidPastDurationException("Cannot cancel as its within " + preventPaidAbortBeforeHours + " hrs");
                } else {
                    LOG.info("Cancelled and refund initiated by {} {} {}", queue.getQueueUserId(), qid, queue.getTransactionId());
                    JsonPurchaseOrder jsonPurchaseOrder = purchaseOrderService.cancelOrderByClient(queue.getQueueUserId(), queue.getTransactionId());
                    sendMessageToSelf(jsonPurchaseOrder);
                }
            }
            abort(queue.getId(), codeQR);
            return new JsonResponse(true);
        } catch (PurchaseOrderRefundPartialException |
            PurchaseOrderRefundExternalException |
            PurchaseOrderCancelException |
            QueueAbortPaidPastDurationException e
        ) {
            LOG.warn("Failed to abort reason={}", e.getLocalizedMessage(), e);
            throw e;
        } catch (Exception e) {
            LOG.error("Abort failed reason={}", e.getLocalizedMessage(), e);
            return new JsonResponse(false);
        }
    }

    @Mobile
    public void abort(String id, String codeQR) {
        queueManager.abort(id);
        /* This is dynamic update of queue when someone aborts their position in queue. */
        //tokenQueueService.updateServingTimeForAllWhenAborted(id);
        /* Irrespective of Queue with order or without order, notify business of abort by just sending a refresh notification. */
        tokenQueueService.forceRefreshOnSomeActivity(codeQR);
    }

    @Mobile
    public JsonResponseWithCFToken createTokenForPaymentGateway(String purchaserQid, String codeQR, String transactionId) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderService.findByQidAndTransactionId(purchaserQid, transactionId);
        if (null != purchaseOrder) {
            return purchaseOrderService.createTokenForPurchaseOrder(purchaseOrder.orderPriceForTransaction(), purchaseOrder.getTransactionId());
        }

        LOG.error("Purchase Order qid mis-match for {} {} {}", purchaserQid, codeQR, transactionId);
        return null;
    }

    @Mobile
    public JsonToken updateWhenPaymentSuccessful(String codeQR, String transactionId) {
        JsonToken jsonToken = tokenQueueService.updateJsonToken(codeQR, transactionId);
        purchaseOrderService.updatePurchaseOrderWithToken(jsonToken.getToken(), jsonToken.getDisplayToken(), jsonToken.getExpectedServiceBegin(), transactionId);
        return jsonToken;
    }

    private JsonPurchaseOrder createNewJsonPurchaseOrder(String purchaserQid, JsonToken jsonToken, BizStoreEntity bizStore) {
        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
            .setBizStoreId(bizStore.getId())
            .setCodeQR(bizStore.getCodeQR())
            .setBusinessType(bizStore.getBusinessType())
            .setOrderPrice(String.valueOf(bizStore.getProductPrice()))
            .setTax(String.valueOf(bizStore.getTax().getValue().intValue()))
            .setGrandTotal(bizStore.computeGrandTotal())
            .setQueueUserId(purchaserQid)
            .setExpectedServiceBegin(jsonToken.getExpectedServiceBegin())
            .setToken(jsonToken.getToken())
            .setDisplayToken(jsonToken.getDisplayToken())
            .setDeliveryMode(DeliveryModeEnum.QS);

        jsonPurchaseOrder.addJsonPurchaseOrderProduct(new JsonPurchaseOrderProduct()
            .setProductId(bizStore.getId())
            .setProductPrice(bizStore.getProductPrice())
            .setTax(bizStore.getTax())
            .setProductQuantity(1)
            .setProductName(bizStore.getDisplayName()));

        return jsonPurchaseOrder;
    }

    private void sendMessageToSelf(JsonPurchaseOrder jsonPurchaseOrder) {
        BizStoreEntity bizStore = bizService.findByCodeQR(jsonPurchaseOrder.getCodeQR());
        String title, body;
        if (new BigDecimal(jsonPurchaseOrder.getOrderPriceForDisplay()).intValue() > 0) {
            title = "Refund initiated by you";
            body = "You have been refunded net total of " + CommonUtil.displayWithCurrencyCode(jsonPurchaseOrder.getOrderPriceForDisplay(), bizStore.getCountryShortName())
                + (jsonPurchaseOrder.getTransactionVia() == TransactionViaEnum.I
                ? " via " + jsonPurchaseOrder.getPaymentMode().getDescription() + ".\n\n" + "Note: It takes 7 to 10 business days for this amount to show up in your account."
                : " at counter");
        } else {
            title = "Cancelled order by you";
            body = "Your order at " + bizStore.getDisplayName() + " was cancelled by you.";
        }

        executorService.execute(() -> notifyClient(
            deviceService.findRegisteredDeviceByQid(jsonPurchaseOrder.getQueueUserId()), title, body, jsonPurchaseOrder.getCodeQR())
        );
    }

    /** Sends personal message with all the current queue and orders. */
    private void notifyClient(RegisteredDeviceEntity registeredDevice, String title, String body, String codeQR) {
        if (null != registeredDevice) {
            JsonMessage jsonMessage = ComposeMessagesForFCM.composeMessageForClientDisplay(registeredDevice, body, title, codeQR);
            firebaseMessageService.messageToTopic(jsonMessage);
        }
    }

    @Mobile
    public void authenticateMessageToClient(String qid, String title, String body, String codeQR) {
        executorService.execute(() -> composeMessageForClientAuth(deviceService.findRegisteredDeviceByQid(qid), title, body, codeQR));
    }

    /** Sends personal message with all the current queue and orders. */
    private void composeMessageForClientAuth(RegisteredDeviceEntity registeredDevice, String title, String body, String codeQR) {
        if (null != registeredDevice) {
            JsonMessage jsonMessage = ComposeMessagesForFCM.composeMessageForClientAuth(registeredDevice, body, title, codeQR);
            firebaseMessageService.messageToTopic(jsonMessage);
        }
    }

    @Mobile
    public void deleteReferenceToTransactionId(String codeQR, String transactionId) {
        queueManager.deleteReferenceToTransactionId(codeQR, transactionId);
        purchaseOrderService.deleteReferenceToTransactionId(transactionId);
    }

    /** Prevent for now. */
    @Mobile
    @Deprecated
    public void checkCustomerApprovedForTheQueue(String qid, BizStoreEntity bizStore) {
//        if (bizStore.getBizName().getPriorityAccess() == OnOffEnum.O) {
//            switch (bizStore.getBusinessType()) {
//                case CD:
//                case CDQ:
//                    BusinessCustomerEntity businessCustomer = businessCustomerService.findOneByQidAndAttribute(qid, bizStore.getBizName().getId(), CommonHelper.findBusinessCustomerAttribute(bizStore));
//                    if (null == businessCustomer) {
//                        throw new JoiningQueuePreApprovedRequiredException("Store has to pre-approve. Please register before joining the queue.");
//                    }
//
//                    if (StringUtils.isNotBlank(businessCustomer.getLimitBusinessCategory()) && !businessCustomer.getLimitBusinessCategory().equalsIgnoreCase(bizStore.getBizCategoryId())) {
//                        throw new JoiningNonApprovedQueueException("This queue is not approved. Select correct pre-approved queue.");
//                    }
//
//                    if (businessCustomer.getBusinessCustomerAttributes().contains(BusinessCustomerAttributeEnum.RJ)) {
//                        throw new JoiningQueuePermissionDeniedException("Business has prevented you from joining this queue. Please contact store.");
//                    }
//                    break;
//                default:
//                    businessCustomer = businessCustomerService.findOneByQid(qid, bizStore.getBizName().getId());
//                    if (null == businessCustomer) {
//                        throw new JoiningQueuePreApprovedRequiredException("Store has to authorize for joining the queue. Contact store for access.");
//                    }
//            }
//        }
    }
}
