package com.noqapp.service;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;
import static com.noqapp.domain.BizStoreEntity.UNDER_SCORE;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonPurchaseOrderList;
import com.noqapp.domain.json.JsonPurchaseOrderProduct;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.JsonTokenAndQueue;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.json.fcm.data.JsonTopicQueueData;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FCMTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseProductOrderManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.StoreHourManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.junit.jupiter.api.Assertions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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

    private BizService bizService;
    private TokenQueueService tokenQueueService;
    private StoreHourManager storeHourManager;
    private StoreProductService storeProductService;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseProductOrderManager purchaseProductOrderManager;
    private UserAddressService userAddressService;
    private FirebaseMessageService firebaseMessageService;
    private RegisteredDeviceManager registeredDeviceManager;
    private AccountService accountService;

    private ExecutorService executorService;

    @Autowired
    public PurchaseOrderService(
        BizService bizService,
        TokenQueueService tokenQueueService,
        StoreHourManager storeHourManager,
        StoreProductService storeProductService,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseProductOrderManager purchaseProductOrderManager,
        UserAddressService userAddressService,
        FirebaseMessageService firebaseMessageService,
        RegisteredDeviceManager registeredDeviceManager,
        AccountService accountService
    ) {
        this.bizService = bizService;
        this.tokenQueueService = tokenQueueService;
        this.storeHourManager = storeHourManager;
        this.storeProductService = storeProductService;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseProductOrderManager = purchaseProductOrderManager;
        this.userAddressService = userAddressService;
        this.firebaseMessageService = firebaseMessageService;
        this.registeredDeviceManager = registeredDeviceManager;
        this.accountService = accountService;

        this.executorService = newCachedThreadPool();
    }

    private JsonToken getNextOrder(String codeQR, long averageServiceTime) {
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
        JsonToken jsonToken = getNextOrder(bizStore.getCodeQR(), bizStore.getAverageServiceTime());

        Date expectedServiceBegin = null;
        try {
            if (jsonToken.getExpectedServiceBegin() != null) {
                expectedServiceBegin = simpleDateFormat.parse(jsonToken.getExpectedServiceBegin());
            }
        } catch (ParseException e) {
            LOG.error("Failed to parse date, reason={}", e.getLocalizedMessage(), e);
        }

        PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(qid, bizStore.getId(), bizStore.getBizName().getId(), bizStore.getCodeQR())
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
        executorService.submit(() -> updatePurchaseOrderWithUserDetail(purchaseOrder));
        userAddressService.addressLastUsed(jsonPurchaseOrder.getDeliveryAddress(), qid);

        doActionBasedOnQueueStatus(bizStore.getCodeQR(), purchaseOrder, tokenQueueService.findByCodeQR(bizStore.getCodeQR()));
        jsonPurchaseOrder.setServingNumber(jsonToken.getServingNumber())
            .setToken(purchaseOrder.getTokenNumber())
            .setExpectedServiceBegin(jsonPurchaseOrder.getExpectedServiceBegin())
            .setTransactionId(purchaseOrder.getTransactionId())
            .setPurchaseOrderState(purchaseOrder.getOrderStates().get(purchaseOrder.getOrderStates().size() - 1));
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

    private void doActionBasedOnQueueStatus(String codeQR, PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue) {
        switch (purchaseOrder.getPresentOrderState()) {
            case IN:
                break;
            case PC:
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, null, purchaseOrder.getTokenNumber());
                break;
            case VB:
            case IB:
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, null, purchaseOrder.getTokenNumber());
                break;
            case PO:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, null);
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, null, purchaseOrder.getTokenNumber());
                break;
            case FO:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, null);
                //Notify Merchant
                break;
            default:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, null);
                break;
        }
    }

    /** Send FCM message to Topic asynchronously. */
    private void sendMessageToTopic(String codeQR, PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue, String goTo) {
        switch (tokenQueue.getBusinessType().getQueueOrderType()) {
            case O:
                executorService.submit(() -> invokeThreadSendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo));
                break;
            default:
                LOG.error("Reached unreachable condition {}", tokenQueue.getBusinessType().getQueueOrderType());
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }

    /** Send FCM message to person with specific token number asynchronously. */
    private void sendMessageToSelectedTokenUser(String codeQR, PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue, String goTo, int tokenNumber) {
        switch (tokenQueue.getBusinessType().getQueueOrderType()) {
            case O:
                executorService.submit(() -> invokeThreadSendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, tokenNumber));
                break;
            default:
                LOG.error("Reached unreachable condition {}", tokenQueue.getBusinessType().getQueueOrderType());
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }

    private List<PurchaseOrderEntity> findAllOpenOrder(String qid) {
        return purchaseOrderManager.findAllOpenOrder(qid);
    }

    @Mobile
    public List<JsonTokenAndQueue> findAllOpenOrderAsJson(String qid) {
        Validate.isValidQid(qid);

        List<JsonTokenAndQueue> jsonTokenAndQueues = new ArrayList<>();
        List<PurchaseOrderEntity> purchaseOrders = findAllOpenOrder(qid);
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            BizStoreEntity bizStore = bizService.findByCodeQR(purchaseOrder.getCodeQR());
            bizStore.setStoreHours(storeHourManager.findAll(bizStore.getId()));

            JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue(purchaseOrder, bizStore);
            jsonTokenAndQueues.add(jsonTokenAndQueue);
        }

        return jsonTokenAndQueues;
    }

    public List<PurchaseOrderEntity> findAllOpenOrderByCodeQR(String codeQR) {
        return purchaseOrderManager.findAllOpenOrderByCodeQR(codeQR);
    }

    @Mobile
    public String findAllOpenOrderByCodeAsJson(String codeQR) {
        List<JsonPurchaseOrder> jsonPurchaseOrders = new ArrayList<>();
        List<PurchaseOrderEntity> purchaseOrders = findAllOpenOrderByCodeQR(codeQR);

        List<JsonPurchaseOrderProduct> jsonPurchaseOrderProducts = new LinkedList<>();
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            List<PurchaseOrderProductEntity> products = purchaseProductOrderManager.getAllByPurchaseOrderId(purchaseOrder.getId());
            for (PurchaseOrderProductEntity purchaseOrderProduct : products) {
                JsonPurchaseOrderProduct jsonPurchaseOrderProduct = new JsonPurchaseOrderProduct()
                    .setProductId(purchaseOrderProduct.getId())
                    .setProductName(purchaseOrderProduct.getProductName())
                    .setProductPrice(purchaseOrderProduct.getProductPrice())
                    .setProductDiscount(purchaseOrderProduct.getProductDiscount())
                    .setProductQuantity(purchaseOrderProduct.getProductQuantity());

                jsonPurchaseOrderProducts.add(jsonPurchaseOrderProduct);
            }

            JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                .setBizStoreId(purchaseOrder.getBizStoreId())
                .setCustomerPhone(purchaseOrder.getCustomerPhone())
                .setDeliveryAddress(purchaseOrder.getDeliveryAddress())
                .setStoreDiscount(purchaseOrder.getStoreDiscount())
                .setOrderPrice(purchaseOrder.getOrderPrice())
                .setDeliveryType(purchaseOrder.getDeliveryType())
                .setPaymentType(purchaseOrder.getPaymentType())
                .setBusinessType(purchaseOrder.getBusinessType())
                .setPurchaseOrderProducts(jsonPurchaseOrderProducts)
                //Serving Number not set for Merchant
                .setToken(purchaseOrder.getTokenNumber())
                .setCustomerName(purchaseOrder.getCustomerName())
                //ExpectedServiceBegin not set for Merchant
                .setTransactionId(purchaseOrder.getTransactionId())
                .setPurchaseOrderState(purchaseOrder.getPresentOrderState())
                .setCreated(DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC")));

            jsonPurchaseOrders.add(jsonPurchaseOrder);
        }

        return new JsonPurchaseOrderList().setPurchaseOrders(jsonPurchaseOrders).asJson();
    }

    /**
     * Formulates and send messages to FCM.
     */
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
            JsonData jsonData = new JsonTopicData(purchaseOrder.getBusinessType().getQueueOrderType(), tokenQueue.getFirebaseMessageType())
                .getJsonTopicOrderData()
                    .setLastNumber(tokenQueue.getLastNumber())
                    .setCurrentlyServing(tokenQueue.getCurrentlyServing())
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
                            LOG.error("Failed adding delay reason={}", e.getLocalizedMessage());
                        }
                    }

                    /*
                     * This message has to go as the merchant with the opened queue
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
                            .setBody("Now Serving " + tokenQueue.getCurrentlyServing())
                            .setLocKey("serving")
                            .setLocArgs(new String[]{String.valueOf(tokenQueue.getCurrentlyServing())})
                            .setTitle(tokenQueue.getDisplayName());
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now Serving " + tokenQueue.getCurrentlyServing())
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
            JsonData jsonData = new JsonTopicQueueData(FirebaseMessageTypeEnum.P, FCMTypeEnum.O)
                .setLastNumber(tokenQueue.getLastNumber())
                .setCurrentlyServing(tokenNumber)
                .setCodeQR(codeQR)
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setGoTo(goTo)
                .setBusinessType(tokenQueue.getBusinessType());

            /*
             * Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
             * As this is a personal message when server is planning to serve a spacific token.
             */
            switch (tokenQueue.getQueueStatus()) {
                case S:
                case R:
                case D:
                    LOG.warn("Skipped sending personal message as queue status is not 'Next' but queueStatus={}", tokenQueue.getQueueStatus());
                    break;
                case P:
                case C:
                    LOG.error("Cannot reach this state codeQR={}, queueStatus={}", codeQR, tokenQueue.getQueueStatus());
                    return;
                case N:
                default:
                    LOG.debug("Personal device is of type={} did={} token={}",
                        registeredDevice.getDeviceType(),
                        registeredDevice.getDeviceId(),
                        registeredDevice.getToken());

                    if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
                        jsonMessage.getNotification()
                            .setBody("Now Serving " + tokenNumber)
                            .setLocKey("serving")
                            .setLocArgs(new String[]{String.valueOf(tokenNumber)})
                            .setTitle(tokenQueue.getDisplayName());
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now Serving " + tokenNumber)
                            .setTitle(tokenQueue.getDisplayName());
                    }
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
     * Merchant when starting or re-starting to serve token when QueueState has been either Start or Re-Start.
     *
     * @param codeQR
     * @param goTo   counter name
     * @param sid    server device id
     * @return
     */
    @Mobile
    public JsonToken getNextInQueue(
        String codeQR,
        String goTo,
        String sid
    ) {
        LOG.info("Getting next in queue for codeQR={} goTo={} sid={}", codeQR, goTo, sid);

        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.getNext(codeQR, goTo, sid);
        if (null != purchaseOrder) {
            LOG.info("Found queue codeQR={} token={}", codeQR, purchaseOrder.getTokenNumber());

            JsonToken jsonToken = tokenQueueService.updateServing(
                codeQR,
                QueueStatusEnum.N,
                purchaseOrder.getTokenNumber(),
                goTo);
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
                .setServingNumber(tokenQueue.getLastNumber())
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(QueueStatusEnum.D);
        }

        return null;
    }

    @Mobile
    public JsonToken getThisAsNextInQueue(
        String codeQR,
        String goTo,
        String sid,
        int token
    ) {
        LOG.info("Getting specific token next in queue for codeQR={} goTo={} sid={} token={}",
            codeQR,
            goTo,
            sid,
            token);

        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.getThisAsNext(codeQR, goTo, sid, token);
        if(null != purchaseOrder) {
            LOG.info("Found queue codeQR={} token={}", codeQR, purchaseOrder.getTokenNumber());

            JsonToken jsonToken = updateThisServing(
                codeQR,
                QueueStatusEnum.N,
                purchaseOrder,
                goTo);
            //TODO(hth) call can be put in thread
            tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.N);
            return jsonToken;
        }

        return null;
    }

    /**
     * This acquires the record of the person being served by server. No one gets informed when the record is
     * acquired other than the person who's record is acquired to be served next.
     */
    @Mobile
    public JsonToken updateThisServing(String codeQR, QueueStatusEnum queueStatus, PurchaseOrderEntity purchaseOrder, String goTo) {
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
        /*
         * Do not inform anyone other than the person with the
         * token who is being served. This is personal message.
         * of being served out of order/sequence.
         */
        sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, purchaseOrder.getTokenNumber());

        LOG.info("After sending message to merchant and personal message to user of token");
        if (purchaseOrder.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue qid={} did={}", purchaseOrder.getQueueUserId(), purchaseOrder.getDid());

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(purchaseOrder.getTokenNumber())
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber())
                .setCustomerName(purchaseOrder.getCustomerName());
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setServingNumber(purchaseOrder.getTokenNumber())
            .setDisplayName(tokenQueue.getDisplayName())
            .setToken(tokenQueue.getLastNumber());
    }

}
