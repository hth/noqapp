package com.noqapp.view.controller.business;

import com.noqapp.common.utils.ParseJsonStringToMap;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.service.AccountService;
import com.noqapp.view.helper.AgentRegisteredStatus;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * All business admin related ajax call resides here.
 *
 * hitender
 * 2018-12-25 07:12
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@RestController
@RequestMapping(value = "/business/webService")
public class AjaxAdminBusinessController {
    private static final Logger LOG = LoggerFactory.getLogger(AjaxAdminBusinessController.class);

    private AccountService accountService;

    @Autowired
    public AjaxAdminBusinessController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Ajax call to check if the account is available to register.
     */
    @PostMapping(
        value = "/availability",
        headers = "Accept=application/json",
        produces = "application/json"
    )
    @ResponseBody
    public String getAvailability(@RequestBody String body) throws IOException {
        String email;
        try {
            email = StringUtils.lowerCase(ParseJsonStringToMap.jsonStringToMap(body).get("mail").getText());
        } catch (IOException e) {
            LOG.error("Failed parsing mail reason={}", e.getLocalizedMessage(), e);
            throw e;
        }

        UserProfileEntity userProfileEntity = accountService.doesUserExists(email);
        AgentRegisteredStatus agentRegisteredStatus;
        if (null != userProfileEntity && userProfileEntity.getEmail().equals(email)) {
            LOG.info("Email={} provided during registration exists", email);
            agentRegisteredStatus = AgentRegisteredStatus.notAvailable(email);
            return String.format("{ \"valid\" : %b, \"message\" : \"<b>%s</b> is already registered. %s\" }",
                agentRegisteredStatus.isAvailable(),
                email,
                StringUtils.join(agentRegisteredStatus.getRecommendation()));
        }
        LOG.info("Email available={} for registration", email);
        agentRegisteredStatus = AgentRegisteredStatus.available();
        return String.format("{ \"valid\" : %b }", agentRegisteredStatus.isAvailable());
    }
}
