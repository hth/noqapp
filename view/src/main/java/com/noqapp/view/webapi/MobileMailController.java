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
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/webapi/mobile/mail")
public class MobileMailController {
    private static final Logger LOG = LoggerFactory.getLogger(MobileMailController.class);

    @Value ("${web.access.api.token}")
    private String webApiAccessToken;

    private final MailService mailService;

    @Autowired
    public MobileMailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping(value = "/accountSignup")
    public void accountValidationMail(
            @RequestBody
            String mailJson,

            @RequestHeader ("X-R-API-MOBILE")
            String apiAccessToken,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("starting to send accountValidationMail");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(mailJson);
            } catch (IOException e) {
                LOG.error("could not parse mailJson={} reason={}", mailJson, e.getLocalizedMessage(), e);
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
        String mailJson,

        @RequestHeader ("X-R-API-MOBILE")
        String apiAccessToken,

        HttpServletResponse httpServletResponse
    ) throws IOException {
        LOG.info("starting to send verification mail with otp");

        if (webApiAccessToken.equals(apiAccessToken)) {
            Map<String, ScrubbedInput> map = new HashMap<>();
            try {
                map = ParseJsonStringToMap.jsonStringToMap(mailJson);
            } catch (IOException e) {
                LOG.error("could not parse mailJson={} reason={}", mailJson, e.getLocalizedMessage(), e);
            }

            if (!map.isEmpty()) {
                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put("mailOTP", map.get("mailOTP").getText());

                mailService.sendAnyMail(
                    map.get("userId").getText(),
                    map.get("name").getText(),
                    "Confirmation mail for NoQApp",
                    rootMap,
                    "mail/mail-otp.ftl");
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