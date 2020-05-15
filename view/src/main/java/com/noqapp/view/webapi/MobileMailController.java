package com.noqapp.view.webapi;

import com.noqapp.common.utils.ParseJsonStringToMap;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.service.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * Invoke email from mobile. Cannot send email from mobile service hence need this controller to send email.
 * User: hitender
 * Date: 11/7/14 11:43 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@RestController
@RequestMapping(value = "/webapi/mobile/mail")
public class MobileMailController {
    private static final Logger LOG = LoggerFactory.getLogger(MobileMailController.class);

    @Value("${web.access.api.token}")
    private String webApiAccessToken;

    private MailService mailService;

    @Autowired
    public MobileMailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping(value = "/accountSignup")
    public void accountValidationMail(
        @RequestBody
        String accountSignup,

        @RequestHeader("X-R-API-MOBILE")
        String apiAccessToken,

        HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("starting to send accountValidationMail");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(accountSignup);
            } catch (IOException e) {
                LOG.error("Could not parse mailJson={} reason={}", accountSignup, e.getLocalizedMessage(), e);
            }

            if (!map.isEmpty()) {
                mailService.sendValidationMailOnAccountCreation(
                    map.get("userId").getText(),
                    map.get("qid").getText(),
                    map.get("name").getText());
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            }
        } else {
            LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        }
    }

    @PostMapping(value = "/mailChange")
    public void mailChange(
        @RequestBody
        String mailChange,

        @RequestHeader("X-R-API-MOBILE")
        String apiAccessToken,

        HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("Verification mail being sent with OTP");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(mailChange);
            } catch (IOException e) {
                LOG.error("Could not parse mailJson={} reason={}", mailChange, e.getLocalizedMessage(), e);
            }

            if (!map.isEmpty()) {
                mailService.sendOTPMail(
                    map.get("userId").getText(),
                    map.get("name").getText(),
                    map.get("mailOTP").getText(),
                    "email address");
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            }
        } else {
            LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        }
    }

    @PostMapping(value = "/feedback")
    public void mailFeedback(
        @RequestBody
        String feedback,

        @RequestHeader("X-R-API-MOBILE")
        String apiAccessToken,

        HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("Feedback mail being generated");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(feedback);
            } catch (IOException e) {
                LOG.error("Could not parse feedbackJson={} reason={}", feedback, e.getLocalizedMessage(), e);
            }

            if (!map.isEmpty()) {
                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put("userId", map.get("userId").getText());
                rootMap.put("qid", map.get("qid").getText());
                rootMap.put("name", map.get("name").getText());
                rootMap.put("subject", map.get("subject").getText());
                rootMap.put("body", map.get("body").getText());

                mailService.sendAnyMail(
                    "contact@noqapp.com",
                    "NoQueue",
                    "Feedback from: " + map.get("name").getText(),
                    rootMap,
                    "mail/feedback.ftl");
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            }
        } else {
            LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        }
    }

    @PostMapping(value = "/negativeReview")
    public void negativeReview(
        @RequestBody
        String reviewSentiment,

        @RequestHeader("X-R-API-MOBILE")
        String apiAccessToken,

        HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("Feedback mail being generated");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(reviewSentiment);
            } catch (IOException e) {
                LOG.error("Could not parse feedbackJson={} reason={}", reviewSentiment, e.getLocalizedMessage(), e);
            }

            if (!map.isEmpty()) {
                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put("storeName", map.get("storeName").getText());
                rootMap.put("reviewerName", map.get("reviewerName").getText());
                rootMap.put("reviewerPhone", map.get("reviewerPhone").getText());
                rootMap.put("ratingCount", map.get("ratingCount").getText());
                rootMap.put("hourSaved", map.get("hourSaved").getText());
                rootMap.put("review", map.get("review").getText());
                rootMap.put("sentiment", map.get("sentiment").getText());

                mailService.sendAnyMail(
                    map.get("sentimentWatcherEmail").getText(),
                    "Customer Sentiment Watcher",
                    "Review for: " + map.get("storeName").getText(),
                    rootMap,
                    "mail/reviewSentiment.ftl");
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            }
        } else {
            LOG.warn("not matching X-R-API-MOBILE key={}", apiAccessToken);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        }
    }
}
