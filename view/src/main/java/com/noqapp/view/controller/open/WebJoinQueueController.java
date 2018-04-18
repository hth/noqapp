package com.noqapp.view.controller.open;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.QueueService;
import com.noqapp.service.ShowHTMLService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.WebJoinQueueForm;
import com.noqapp.view.util.HttpRequestResponseParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;

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

    private BizService bizService;
    private ShowHTMLService showHTMLService;
    private TokenQueueService tokenQueueService;
    private QueueService queueService;
    private AccountService accountService;

    @Autowired
    public WebJoinQueueController(
            BizService bizService,
            ShowHTMLService showHTMLService,
            TokenQueueService tokenQueueService,
            QueueService queueService,
            AccountService accountService
    ) {
        this.bizService = bizService;
        this.showHTMLService = showHTMLService;
        this.tokenQueueService = tokenQueueService;
        this.queueService = queueService;
        this.accountService = accountService;
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

            HttpServletResponse response
    ) throws IOException {
        LOG.info("CodeQR={}", codeQR.getText());
        String codeQRDecoded = new String(Base64.getDecoder().decode(codeQR.getText()), StandardCharsets.ISO_8859_1);

        if (!bizService.isValidCodeQR(codeQRDecoded)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid token");
            return null;
        }

        BizStoreEntity bizStore = bizService.findByCodeQR(codeQRDecoded);
        Map<String, String> rootMap = new HashMap<>();
        showHTMLService.populateStore(rootMap, bizStore);

        webJoinQueue.setRootMap(rootMap).setCodeQR(new ScrubbedInput(rootMap.get("codeQR")));
        return joinQueuePage;
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
                    Map<String, String> rootMap = new HashMap<>();
                    showHTMLService.populateStore(rootMap, bizStore);

                    rootMap.put("token", data[1]);

                    String expectedServiceTime;
                    if (data.length >= 3) {
                        expectedServiceTime = data[2].length() == 0 ? "" : data[2];
                    } else {
                        expectedServiceTime = "";
                    }
                    rootMap.put("expectedServiceTime", expectedServiceTime);
                    rootMap.put("storeAddress", bizStore.getAddress());

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

        LOG.warn(
                "404 request access={} header={}",
                joinQueueConfirmPage,
                HttpRequestResponseParser.printHeader(request)
        );
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

            JsonToken jsonToken;
            QueueEntity queue = tokenQueueService.findQueuedByPhone(
                    codeQRDecoded,
                    webJoinQueue.getPhone().getText());

            BizStoreEntity bizStore = bizService.findByCodeQR(codeQRDecoded);
            if (null == queue) {
                String did = UUID.randomUUID().toString();
                UserProfileEntity userProfile = accountService.checkUserExistsByPhone(webJoinQueue.getPhone().getText());

                jsonToken = tokenQueueService.getNextToken(
                        codeQRDecoded,
                        did,
                        null != userProfile ? userProfile.getQueueUserId() : null,
                        bizStore.getAverageServiceTime(),
                        TokenServiceEnum.W
                );

                if (null != userProfile) {
                    queue = queueService.findQueuedOne(codeQRDecoded, did, userProfile.getQueueUserId());
                    tokenQueueService.updateQueueWithUserDetail(codeQRDecoded, userProfile.getQueueUserId(), queue);
                } else {
                    queueService.addPhoneNumberToExistingQueue(
                            jsonToken.getToken(),
                            codeQRDecoded,
                            did,
                            webJoinQueue.getPhone().getText());
                }
            } else {
                TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQRDecoded);
                jsonToken = new JsonToken(codeQRDecoded, bizStore.getBusinessType())
                        .setToken(queue.getTokenNumber())
                        .setServingNumber(tokenQueue.getCurrentlyServing())
                        .setDisplayName(tokenQueue.getDisplayName())
                        .setQueueStatus(tokenQueue.getQueueStatus())
                        .setExpectedServiceBegin(queue.getExpectedServiceBegin());
            }

            if (StringUtils.isNotBlank(jsonToken.getExpectedServiceBegin())) {
                Date date = simpleDateFormat.parse(jsonToken.getExpectedServiceBegin());
                jsonToken.setExpectedServiceBegin(date, bizStore.getTimeZone());
            }

            String expectedServiceBegin = StringUtils.isBlank(jsonToken.getExpectedServiceBegin()) ? "" : jsonToken.getExpectedServiceBegin();
            /* Encoding of data. */
            String combined = jsonToken.getCodeQR()
                    + "#" + String.valueOf(jsonToken.getToken())
                    + "#" + expectedServiceBegin;
            return String.format("{ \"c\" : \"%s\" }", Base64.getEncoder().encodeToString(combined.getBytes()));
        } catch (IOException | ParseException e) {
            LOG.error("Failed Joining Web Queue reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }
}
