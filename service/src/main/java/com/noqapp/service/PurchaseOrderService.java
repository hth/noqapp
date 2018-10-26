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
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.PurchaseOrderProductManagerJDBC;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.exceptions.StoreDayClosedException;
import com.noqapp.service.exceptions.StorePreventJoiningException;
import com.noqapp.service.exceptions.StoreTempDayClosedException;

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

    private BizStoreManager bizStoreManager;
    private TokenQueueService tokenQueueService;
    private StoreHourManager storeHourManager;
    private StoreProductService storeProductService;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    private PurchaseOrderProductManager purchaseOrderProductManager;
    private PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;
    private UserAddressService userAddressService;
    private FirebaseMessageService firebaseMessageService;
    private RegisteredDeviceManager registeredDeviceManager;
    private TokenQueueManager tokenQueueManager;
    private AccountService accountService;

    private ExecutorService executorService;

    @Autowired
    public PurchaseOrderService(
        BizStoreManager bizStoreManager,
        TokenQueueService tokenQueueService,
        StoreHourManager storeHourManager,
        StoreProductService storeProductService,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC,
        PurchaseOrderProductManager purchaseOrderProductManager,
        PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC,
        UserAddressService userAddressService,
        FirebaseMessageService firebaseMessageService,
        RegisteredDeviceManager registeredDeviceManager,
        TokenQueueManager tokenQueueManager,
        AccountService accountService
    ) {
        this.bizStoreManager = bizStoreManager;
        this.tokenQueueService = tokenQueueService;
        this.storeHourManager = storeHourManager;
        this.storeProductService = storeProductService;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
        this.purchaseOrderProductManager = purchaseOrderProductManager;
        this.purchaseOrderProductManagerJDBC = purchaseOrderProductManagerJDBC;
        this.userAddressService = userAddressService;
        this.firebaseMessageService = firebaseMessageService;
        this.registeredDeviceManager = registeredDeviceManager;
        this.tokenQueueManager = tokenQueueManager;
        this.accountService = accountService;

        this.executorService = newCachedThreadPool();
    }

    private JsonToken getNextOrder(String codeQR, long averageServiceTime) {
        try {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
            ZoneId zoneId = TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId();
            DayOfWeek dayOfWeek = ZonedDateTime.now(zoneId).getDayOfWeek();
            StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), dayOfWeek);

            if (storeHour.isDayClosed() || storeHour.isTempDayClosed() || storeHour.isPreventJoining()) {
                LOG.warn("When store closed or prevent joining, attempting to create new order");

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

            TokenQueueEntity tokenQueue = tokenQueueService.getNextToken(codeQR);
            LOG.info("Assigned order number with codeQR={} with new token={}", codeQR, tokenQueue.getLastNumber());
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

    @Mobile
    public JsonPurchaseOrder cancelOrderByClient(String qid, String transactionId) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.cancelOrderByClient(qid, transactionId);
        return JsonPurchaseOrder.populateForCancellingOrder(purchaseOrder);
    }

    @Mobile
    public boolean isOrderCancelled(String qid, String transactionId) {
        return purchaseOrderManager.isOrderCancelled(qid, transactionId);
    }

    @Mobile
    public JsonPurchaseOrderList cancelOrderByMerchant(String codeQR, int tokenNumber) {
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.cancelOrderByMerchant(codeQR, tokenNumber);
        return new JsonPurchaseOrderList().addPurchaseOrder(JsonPurchaseOrder.populateForCancellingOrder(purchaseOrder));
    }

    @Mobile
    public boolean isOrderCancelled(String codeQR, int tokenNumber) {
        return purchaseOrderManager.isOrderCancelled(codeQR, tokenNumber);
    }

    //TODO add multiple logic to validate and more complicated response on failure of order submission for letting user know.
    @Mobile
    public void createOrder(JsonPurchaseOrder jsonPurchaseOrder, String qid, String did, TokenServiceEnum tokenService) {
        BizStoreEntity bizStore = bizStoreManager.getById(jsonPurchaseOrder.getBizStoreId());
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
            .setTransactionId(CommonUtil.generateTransactionId(jsonPurchaseOrder.getBizStoreId(), jsonToken.getToken()))
            .setDisplayName(bizStore.getDisplayName())
            .setAdditionalNote(jsonPurchaseOrder.getAdditionalNote());

        purchaseOrder.setId(CommonUtil.generateHexFromObjectId());
        if (StringUtils.isBlank(purchaseOrder.getOrderPrice())) {
            //TODO(hth) add condition to check for purchase price.
            LOG.warn("Purchase price NOT set for order={}", purchaseOrder.getId());
        }
        purchaseOrderManager.save(purchaseOrder);

        for (JsonPurchaseOrderProduct jsonPurchaseOrderProduct : jsonPurchaseOrder.getPurchaseOrderProducts()) {
            StoreProductEntity storeProduct = null;
            if (StringUtils.isNotBlank(jsonPurchaseOrderProduct.getProductId())) {
                storeProduct = storeProductService.findOne(jsonPurchaseOrderProduct.getProductId());
            }

            PurchaseOrderProductEntity purchaseOrderProduct = new PurchaseOrderProductEntity();
            if (storeProduct != null) {
                purchaseOrderProduct.setProductId(jsonPurchaseOrderProduct.getProductId())
                    .setProductName(storeProduct.getProductName())
                    .setProductPrice(storeProduct.getProductPrice())
                    .setProductDiscount(storeProduct.getProductDiscount());
            } else {
                purchaseOrderProduct.setProductName(jsonPurchaseOrderProduct.getProductName());
            }

            purchaseOrderProduct.setProductQuantity(jsonPurchaseOrderProduct.getProductQuantity())
                .setQueueUserId(qid)
                .setBizStoreId(jsonPurchaseOrder.getBizStoreId())
                .setBizNameId(bizStore.getBizName().getId())
                .setCodeQR(bizStore.getCodeQR())
                .setBusinessType(bizStore.getBusinessType())
                .setPurchaseOrderId(purchaseOrder.getId());
            purchaseOrderProductManager.save(purchaseOrderProduct);
        }

        purchaseOrder
            .addOrderState(PurchaseOrderStateEnum.VB)
            .addOrderState(PurchaseOrderStateEnum.PO);
        purchaseOrderManager.save(purchaseOrder);
        executorService.submit(() -> updatePurchaseOrderWithUserDetail(purchaseOrder));
        userAddressService.addressLastUsed(jsonPurchaseOrder.getDeliveryAddress(), qid);

        doActionBasedOnQueueStatus(bizStore.getCodeQR(), purchaseOrder, tokenQueueService.findByCodeQR(bizStore.getCodeQR()), null);
        jsonPurchaseOrder.setServingNumber(jsonToken.getServingNumber())
            .setToken(purchaseOrder.getTokenNumber())
            .setExpectedServiceBegin(jsonPurchaseOrder.getExpectedServiceBegin())
            .setTransactionId(purchaseOrder.getTransactionId())
            .setPresentOrderState(purchaseOrder.getOrderStates().get(purchaseOrder.getOrderStates().size() - 1))
            .setCreated(DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC")));
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

    private void doActionBasedOnQueueStatus(String codeQR, PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue, String goTo) {
        switch (purchaseOrder.getPresentOrderState()) {
            case IN:
                break;
            case PC:
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, purchaseOrder.getTokenNumber());
                break;
            case VB:
            case IB:
                break;
            case FO:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
                //Notify Merchant
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
                sendMessageToSelectedTokenUser(codeQR, purchaseOrder, tokenQueue, goTo, purchaseOrder.getTokenNumber());
                break;
            default:
                sendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo);
                break;
        }
    }

    /** Send FCM message to Topic asynchronously. */
    private void sendMessageToTopic(String codeQR, PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue, String goTo) {
        switch (tokenQueue.getBusinessType().getMessageOrigin()) {
            case O:
                executorService.submit(() -> invokeThreadSendMessageToTopic(codeQR, purchaseOrder, tokenQueue, goTo));
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
        Validate.isValidQid(qid);

        List<PurchaseOrderEntity> purchaseOrders = findAllOpenOrder(qid);
        List<UserProfileEntity> dependentUserProfiles = accountService.findDependentProfiles(qid);
        for (UserProfileEntity userProfile : dependentUserProfiles) {
            purchaseOrders.addAll(findAllOpenOrder(userProfile.getQueueUserId()));
        }

        List<JsonTokenAndQueue> jsonTokenAndQueues = new ArrayList<>();
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(purchaseOrder.getCodeQR());
            bizStore.setStoreHours(storeHourManager.findAll(bizStore.getId()));
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(purchaseOrder.getCodeQR());

            JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue(purchaseOrder, tokenQueue, bizStore);
            jsonTokenAndQueues.add(jsonTokenAndQueue);
        }

        return jsonTokenAndQueues;
    }

    private List<PurchaseOrderEntity> findAllDeliveredHistoricalOrder(String qid) {
        return purchaseOrderManager.findAllDeliveredHistoricalOrder(qid);
    }

    @Mobile
    public List<JsonTokenAndQueue> findAllDeliveredHistoricalOrderAsJson(String qid) {
        Validate.isValidQid(qid);

        List<PurchaseOrderEntity> purchaseOrders = findAllDeliveredHistoricalOrder(qid);
        LOG.info("Total purchase orders completed count={}", purchaseOrders.size());
        List<JsonTokenAndQueue> jsonTokenAndQueues = new ArrayList<>();
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(purchaseOrder.getCodeQR());
            bizStore.setStoreHours(storeHourManager.findAll(bizStore.getId()));

            JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue(purchaseOrder, bizStore);
            jsonTokenAndQueues.add(jsonTokenAndQueue);
        }

        return jsonTokenAndQueues;
    }

    private List<PurchaseOrderEntity> findAllOpenOrderByCodeQR(String codeQR) {
        return purchaseOrderManager.findAllOpenOrderByCodeQR(codeQR);
    }

    private List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR) {
        return purchaseOrderManager.findAllOrderByCodeQR(codeQR);
    }

    private List<PurchaseOrderEntity> findAllPastDeliveredOrCancelledOrders(String qid) {
        return purchaseOrderManager.findAllPastDeliveredOrCancelledOrders(qid);
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

    /** This is for historical orders placed today, other past orders have moved in archive. */
    @Mobile
    public JsonPurchaseOrderHistoricalList findAllPastDeliveredOrCancelledOrdersAsJson(String qid) {
        JsonPurchaseOrderHistoricalList jsonPurchaseOrderHistoricalList = new JsonPurchaseOrderHistoricalList();

        List<PurchaseOrderEntity> purchaseOrders = findAllPastDeliveredOrCancelledOrders(qid);
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManager.getAllByPurchaseOrderId(purchaseOrder.getId());
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(purchaseOrder.getCodeQR());
            jsonPurchaseOrderHistoricalList.addJsonPurchaseOrderHistorical(new JsonPurchaseOrderHistorical(purchaseOrder, purchaseOrderProducts, bizStore));
        }
        purchaseOrders = purchaseOrderManagerJDBC.getByQid(qid);
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManagerJDBC.getByPurchaseOrderId(purchaseOrder.getId());
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(purchaseOrder.getCodeQR());
            jsonPurchaseOrderHistoricalList.addJsonPurchaseOrderHistorical(new JsonPurchaseOrderHistorical(purchaseOrder, purchaseOrderProducts, bizStore));
        }
        return jsonPurchaseOrderHistoricalList;
    }

    private void populateRelatedToPurchaseOrder(List<JsonPurchaseOrder> jsonPurchaseOrders, PurchaseOrderEntity purchaseOrder) {
        List<JsonPurchaseOrderProduct> jsonPurchaseOrderProducts = new LinkedList<>();
        List<PurchaseOrderProductEntity> products = purchaseOrderProductManager.getAllByPurchaseOrderId(purchaseOrder.getId());
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
            .setPresentOrderState(purchaseOrder.getPresentOrderState())
            .setCreated(DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
            .setAdditionalNote(purchaseOrder.getAdditionalNote());

        jsonPurchaseOrders.add(jsonPurchaseOrder);
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
            JsonData jsonData = new JsonTopicData(purchaseOrder.getBusinessType().getMessageOrigin(), tokenQueue.getFirebaseMessageType())
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
                    message = "Apologies as we have failed to place order";
                    break;
                case OP:
                    message = "Your order " + tokenNumber + " is being processed";
                    break;
                case RP:
                    message = "Your order is ready for pickup";
                    break;
                case RD:
                    message = "Your order is ready for delivery";
                    break;
                case OW:
                    message = "Your order is on the way";
                    break;
                case LO:
                    message = "Apologies we have lost your order. We are working on it to find your order.";
                    break;
                case FD:
                    message = "We failed to deliver your order";
                    break;
                case OD:
                    message = "Your order has been successfully delivered";
                    break;
                case DA:
                    message = "We are re-attempting to deliver your order";
                    break;
                case CO:
                    message = "Your order was cancelled";
                    break;
                default:
                    LOG.error("Failed condition={} device is of type={} did={} token={}",
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
     * When merchant has served a specific token.
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
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayName(tokenQueue.getDisplayName())
            .setQueueStatus(QueueStatusEnum.D);
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
                .setServingNumber(tokenQueue.getLastNumber())
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
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber())
                .setCustomerName(purchaseOrder.getCustomerName());
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayName(tokenQueue.getDisplayName())
            .setToken(tokenQueue.getLastNumber());
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
                    switch (purchaseOrder.getDeliveryType()) {
                        case HD:
                            purchaseOrder
                                .addOrderState(PurchaseOrderStateEnum.PR)
                                .addOrderState(PurchaseOrderStateEnum.RP);
                            break;
                        case TO:
                            purchaseOrder
                                .addOrderState(PurchaseOrderStateEnum.PR)
                                .addOrderState(PurchaseOrderStateEnum.RD);
                            break;
                        default:
                            LOG.error("Reached unreachable condition, deliveryType={}", purchaseOrder.getDeliveryType());
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
        PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findBy(qid, codeQR, tokenNumber);
        if (null == purchaseOrder) {
            return null;
        }

        List<JsonPurchaseOrder> jsonPurchaseOrders = new ArrayList<>();
        populateRelatedToPurchaseOrder(jsonPurchaseOrders, purchaseOrder);
        return jsonPurchaseOrders.isEmpty() ? null : jsonPurchaseOrders.get(0);
    }

    /**
     * Since review can be done in background. Moved logic to thread.
     *
     * @param codeQR
     * @param token
     * @param did
     * @param qid
     * @param ratingCount
     */
    @Mobile
    public boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, String review) {
        executorService.submit(() -> reviewingService(codeQR, token, did, qid, ratingCount, review));
        return true;
    }

    /**
     * Submitting review.
     */
    private void reviewingService(String codeQR, int token, String did, String qid, int ratingCount, String review) {
        boolean reviewSubmitStatus = purchaseOrderManager.reviewService(codeQR, token, did, qid, ratingCount, review);
        if (!reviewSubmitStatus) {
            //TODO(hth) make sure for Guardian this is taken care. Right now its ignore "GQ" add to MySQL Table
            reviewSubmitStatus = reviewHistoricalService(codeQR, token, did, qid, ratingCount, review);
        }

        LOG.info("Review update status={} codeQR={} token={} ratingCount={} hoursSaved={} did={} qid={} review={}",
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
        String did,
        String qid,
        int ratingCount,
        String review
    ) {
        return purchaseOrderManagerJDBC.reviewService(codeQR, token, did, qid, ratingCount, review);
    }
}
