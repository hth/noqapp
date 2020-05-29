package com.noqapp.view.controller.open;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;
import static com.noqapp.domain.BizStoreEntity.UNDER_SCORE;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.search.elastic.service.GeoIPLocationService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.FirebaseService;
import com.noqapp.service.JoinAbortService;
import com.noqapp.service.QueueService;
import com.noqapp.service.ShowHTMLService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.service.exceptions.BeforeStartOfStoreException;
import com.noqapp.service.exceptions.JoiningNonApprovedQueueException;
import com.noqapp.service.exceptions.JoiningQueuePermissionDeniedException;
import com.noqapp.service.exceptions.JoiningQueuePreApprovedRequiredException;
import com.noqapp.service.exceptions.LimitedPeriodException;
import com.noqapp.service.exceptions.StoreDayClosedException;
import com.noqapp.service.exceptions.TokenAvailableLimitReachedException;
import com.noqapp.view.form.WebJoinQueueForm;
import com.noqapp.view.util.HttpRequestResponseParser;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 1/29/18 4:19 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/open/join")
public class WebJoinQueueController {
    private static final Logger LOG = LoggerFactory.getLogger(WebJoinQueueController.class);

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO8601_FMT);

    @Value ("${joinQueue:/join/queue}")
    private String joinQueuePage;

    @Value ("${joinQueueConfirm:redirect:/open/join/queueConfirm.htm}")
    private String joinQueueConfirm;

    @Value ("${joinQueueConfirmPage:/join/queueConfirm}")
    private String joinQueueConfirmPage;

    @Value("${firebase.apiKey}")
    private String firebaseApiKey;

    @Value("${firebase.authDomain}")
    private String firebaseAuthDomain;

    @Value("${firebase.databaseURL}")
    private String firebaseDatabaseURL;

    @Value("${firebase.projectId}")
    private String firebaseProjectId;

    @Value("${firebase.storageBucket}")
    private String firebaseStorageBucket;

    @Value("${firebase.messagingSenderId}")
    private String firebaseMessagingSenderId;

    private BizService bizService;
    private ShowHTMLService showHTMLService;
    private TokenQueueService tokenQueueService;
    private QueueService queueService;
    private AccountService accountService;
    private GeoIPLocationService geoIPLocationService;
    private RegisteredDeviceManager registeredDeviceManager;
    private FirebaseService firebaseService;
    private JoinAbortService joinAbortService;

    @Autowired
    public WebJoinQueueController(
        BizService bizService,
        ShowHTMLService showHTMLService,
        TokenQueueService tokenQueueService,
        QueueService queueService,
        AccountService accountService,
        GeoIPLocationService geoIPLocationService,
        RegisteredDeviceManager registeredDeviceManager,
        FirebaseService firebaseService,
        JoinAbortService joinAbortService
    ) {
        this.bizService = bizService;
        this.showHTMLService = showHTMLService;
        this.tokenQueueService = tokenQueueService;
        this.queueService = queueService;
        this.accountService = accountService;
        this.geoIPLocationService = geoIPLocationService;
        this.registeredDeviceManager = registeredDeviceManager;
        this.firebaseService = firebaseService;
        this.joinAbortService = joinAbortService;
    }

    /**
     * Edit landing category name.
     */
    @GetMapping(value = "/queue/{codeQR}")
    public String webJoinQueue(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute("webJoinQueue")
        WebJoinQueueForm webJoinQueue,

        Model model,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        try {
            LOG.info("Coded CodeQR={}", codeQR.getText());
            String codeQRDecoded = new String(Base64.getDecoder().decode(codeQR.getText()), StandardCharsets.ISO_8859_1);

            if (!bizService.isValidCodeQR(codeQRDecoded)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid token");
                return null;
            }

            int requesterTime = geoIPLocationService.requestOriginatorTime(HttpRequestResponseParser.getClientIpAddress(request));
            LOG.info("Web requester originator time is {} codeQRDecoded={}", requesterTime, codeQRDecoded);

            BizStoreEntity bizStore = bizService.findByCodeQR(codeQRDecoded);
            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put("requesterTime", DateFormatter.convertMilitaryTo12HourFormat(requesterTime));
            showHTMLService.populateStore(rootMap, bizStore);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());

            int tokenFrom = bizStore.getTokenAvailableFrom(zonedDateTime.getDayOfWeek());
            int tokenEnd = bizStore.getTokenNotAvailableFrom(zonedDateTime.getDayOfWeek());
            if (requesterTime > tokenFrom && requesterTime < tokenEnd) {
                rootMap.put("tokenAvailableFrom", DateFormatter.convertMilitaryTo12HourFormat(tokenFrom));
                rootMap.put("startHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getStartHour(zonedDateTime.getDayOfWeek())));
                rootMap.put("tokenNotAvailableFrom", DateFormatter.convertMilitaryTo12HourFormat(tokenEnd));
                rootMap.put("endHour", DateFormatter.convertMilitaryTo12HourFormat(bizStore.getEndHour(zonedDateTime.getDayOfWeek())));
            } else {
                if (requesterTime < tokenFrom) {
                    rootMap.put("notOpen", "Yes");
                } else {
                    rootMap.put("closedForTheDay", "Yes");
                }
            }

            rootMap.put("claimed", bizStore.getBizName().isClaimed() ? "Yes" : "No");
            webJoinQueue.setRootMap(rootMap).setCodeQR(new ScrubbedInput(((String) rootMap.get("codeQR"))));

            model.addAttribute("firebaseApiKey", firebaseApiKey);
            model.addAttribute("firebaseAuthDomain", firebaseAuthDomain);
            model.addAttribute("firebaseDatabaseURL", firebaseDatabaseURL);
            model.addAttribute("firebaseProjectId", firebaseProjectId);
            model.addAttribute("firebaseStorageBucket", firebaseStorageBucket);
            model.addAttribute("firebaseMessagingSenderId", firebaseMessagingSenderId);

            return joinQueuePage;
        } catch (Exception e) {
            LOG.error("Failed to load join queue page reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    /**
     * Loading confirmation page on successful joining queue.
     */
    @GetMapping (value = "/{combined}/queueConfirm")
    public String confirm(
        @PathVariable("combined")
        ScrubbedInput combined,

        @ModelAttribute("webJoinQueue")
        WebJoinQueueForm webJoinQueue,

        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        if (StringUtils.isNotBlank(combined.getText())) {
            try {
                String combinedDecoded = new String(Base64.getDecoder().decode(combined.getText()), StandardCharsets.ISO_8859_1);
                String[] data = combinedDecoded.split("#");

                if (Validate.isValidObjectId(data[0])) {
                    if (!bizService.isValidCodeQR(data[0])) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid token");
                        return null;
                    }

                    BizStoreEntity bizStore = bizService.findByCodeQR(data[0]);
                    Map<String, Object> rootMap = new HashMap<>();
                    showHTMLService.populateStore(rootMap, bizStore);

                    String expectedServiceTime;
                    if (data.length == 1) {
                        rootMap.put("registration", "required");
                    } else {
                        rootMap.put("token", data[1]);
                    }

                    if (data.length >= 3) {
                        expectedServiceTime = data[2].length() == 0 ? "" : data[2];
                    } else {
                        expectedServiceTime = "";
                    }
                    rootMap.put("expectedServiceTime", expectedServiceTime);
                    rootMap.put("storeAddress", bizStore.getAddress());
                    rootMap.put("claimed", bizStore.getBizName().isClaimed() ? "Yes" : "No");
                    webJoinQueue.setRootMap(rootMap).setCodeQR(new ScrubbedInput(bizStore.getCodeQR()));
                    return joinQueueConfirmPage;
                }
            } catch (Exception e) {
                LOG.error("Failed loading Web Confirmation text={} reason={}", combined.getText(), e.getLocalizedMessage(), e);
                LOG.warn(
                    "404 request access={} header={}",
                    joinQueueConfirmPage,
                    HttpRequestResponseParser.printHeader(request)
                );
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return null;
            }
        }

        LOG.warn("404 request access={} header={}", joinQueueConfirmPage, HttpRequestResponseParser.printHeader(request));
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }

    @PostMapping(value = "/queue")
    @ResponseBody
    public String joinQueue(
        @ModelAttribute("webJoinQueue")
        WebJoinQueueForm webJoinQueue,

        HttpServletResponse response
    ) throws IOException, ParseException {
        try {
            LOG.info("CodeQR={}", webJoinQueue.getCodeQR().getText());
            String codeQRDecoded = new String(Base64.getDecoder().decode(webJoinQueue.getCodeQR().getText()), StandardCharsets.ISO_8859_1);

            if (!bizService.isValidCodeQR(codeQRDecoded)) {
                LOG.info("Not a valid QRCode={}", codeQRDecoded);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid token");
                return null;
            }
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQRDecoded);
            QueueEntity queue = tokenQueueService.findQueuedByPhone(codeQRDecoded, webJoinQueue.getPhone().getText());

            JsonToken jsonToken;
            if (null != queue) {
                TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQRDecoded);
                jsonToken = new JsonToken(codeQRDecoded, bizStore.getBusinessType())
                    .setToken(queue.getTokenNumber())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setQueueStatus(tokenQueue.getQueueStatus())
                    .setExpectedServiceBegin(queue.getExpectedServiceBegin());
            } else {
                UserProfileEntity userProfile = accountService.checkUserExistsByPhone(webJoinQueue.getPhone().getText());
                if (bizStore.isAllowLoggedInUser() && userProfile == null) {
                    LOG.info("Sent user to register, bizStore required registered user {} {}", bizStore.getDisplayName(), bizStore.getCodeQR());
                    /* Encoding of data. */
                    String combined = codeQRDecoded + "#" + "#";
                    return String.format("{ \"c\" : \"%s\" }", Base64.getEncoder().encodeToString(combined.getBytes()));
                }

                try {
                    joinAbortService.checkCustomerApprovedForTheQueue(userProfile.getQueueUserId(), bizStore);
                } catch (JoiningQueuePreApprovedRequiredException e) {
                    LOG.warn("Store has to pre-approve reason={}", e.getLocalizedMessage());
                    return String.format("{ \"c\" : \"%s\" }", "pre-approved-req");
                } catch (JoiningNonApprovedQueueException e) {
                    LOG.warn("This queue is not approved reason={}", e.getLocalizedMessage());
                    return String.format("{ \"c\" : \"%s\" }", "non-approved");
                } catch (JoiningQueuePermissionDeniedException e) {
                    LOG.warn("Store has denied joining queue reason={}", e.getLocalizedMessage());
                    return String.format("{ \"c\" : \"%s\" }", "denied-joining-queue");
                }

                /* Register device, which happens to be web. */
                String did = UUID.randomUUID().toString();
                RegisteredDeviceEntity registeredDevice = null;
                registeredDevice = registeredDeviceManager.findRecentDevice(userProfile.getQueueUserId());
                if (null != registeredDevice) {
                    did = registeredDevice.getDeviceId();
                }

                jsonToken = tokenQueueService.getNextToken(
                    codeQRDecoded,
                    did,
                    userProfile.getQueueUserId(),
                    null,
                    bizStore.getAverageServiceTime(),
                    TokenServiceEnum.W
                );

                try {
                    switch (jsonToken.getQueueStatus()) {
                        case C:
                            throw new StoreDayClosedException("Store is closed today codeQR " + codeQRDecoded);
                        case B:
                            throw new BeforeStartOfStoreException("Please correct your system time to match your timezone " + codeQRDecoded);
                        case X:
                            throw new LimitedPeriodException("Please wait until set number of days before using this service");
                        case L:
                            throw new TokenAvailableLimitReachedException("Token limit reached");
                        default:
                    }
                } catch (StoreDayClosedException e) {
                    LOG.error("Failed joining queue store closed Web Queue reason={}", e.getLocalizedMessage(), e);
                    return String.format("{ \"c\" : \"%s\" }", "closed");
                } catch (BeforeStartOfStoreException e) {
                    LOG.error("Failed joining queue as trying to join before store opens Web Queue reason={}", e.getLocalizedMessage(), e);
                    return String.format("{ \"c\" : \"%s\" }", "before");
                } catch (LimitedPeriodException e) {
                    LOG.warn("Failed joining queue as limited join allowed qid={}, reason={}", userProfile.getQueueUserId(), e.getLocalizedMessage());
                    return String.format("{ \"c\" : \"%s\" }", "wait");
                } catch (TokenAvailableLimitReachedException e) {
                    LOG.warn("Failed joining queue as token limit reached qid={}, reason={}", userProfile.getQueueUserId(), e.getLocalizedMessage());
                    return String.format("{ \"c\" : \"%s\" }", "limit");
                }

                if (null != userProfile) {
                    queue = queueService.findQueuedOne(codeQRDecoded, did, userProfile.getQueueUserId());
                    tokenQueueService.updateQueueWithUserDetail(codeQRDecoded, userProfile.getQueueUserId(), queue);

                    if (null != registeredDevice) {
                        subscribeDeviceToTopic(codeQRDecoded, userProfile.getQueueUserId(), registeredDevice);
                    }
                } else {
                    queueService.addPhoneNumberToExistingQueue(
                        jsonToken.getToken(),
                        codeQRDecoded,
                        did,
                        webJoinQueue.getPhone().getText());
                }
            }

            if (StringUtils.isNotBlank(jsonToken.getExpectedServiceBegin())) {
                Date date = simpleDateFormat.parse(jsonToken.getExpectedServiceBegin());
                jsonToken.setExpectedServiceBegin(date, bizStore.getTimeZone());
            }

            String expectedServiceBegin = StringUtils.isBlank(jsonToken.getExpectedServiceBegin()) ? "" : jsonToken.getExpectedServiceBegin();
            /* Encoding of data. */
            String combined = jsonToken.getCodeQR()
                + "#" + jsonToken.getToken()
                + "#" + expectedServiceBegin;
            return String.format("{ \"c\" : \"%s\" }", Base64.getEncoder().encodeToString(combined.getBytes()));
        } catch (IOException | ParseException e) {
            LOG.error("Failed Joining Web Queue reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    /**
     * Subscribe device when registered through web.
     *
     * @param codeQRDecoded
     * @param qid
     * @param registeredDevice
     */
    private void subscribeDeviceToTopic(String codeQRDecoded, String qid, RegisteredDeviceEntity registeredDevice) {
        List<String> tokens = new ArrayList<>();
        tokens.add(registeredDevice.getToken());
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQRDecoded);
        String topic = tokenQueue.getCorrectTopic(QueueStatusEnum.N) + UNDER_SCORE + registeredDevice.getDeviceType().name();
        firebaseService.subscribeToTopic(tokens, topic);

        tokenQueueService.sendMessageToSpecificUser(
            "Joined Queue",
            "You have joined queue successfully",
            qid,
            MessageOriginEnum.D);
    }
}
