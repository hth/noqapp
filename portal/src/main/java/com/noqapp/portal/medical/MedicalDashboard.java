package com.noqapp.portal.medical;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.portal.service.QueuePortalService;
import com.noqapp.service.AccountService;
import com.noqapp.service.QueueService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * hitender
 * 3/20/20 1:45 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/portal/medical/dashboard")
public class MedicalDashboard {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalDashboard.class);

    private QueueService queueService;
    private QueuePortalService queuePortalService;
    private AccountService accountService;

    @Autowired
    public MedicalDashboard(
        QueueService queueService,
        QueuePortalService queuePortalService,
        AccountService accountService
    ) {
        this.queueService = queueService;
        this.queuePortalService = queuePortalService;
        this.accountService = accountService;
    }

    @GetMapping(
        produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"
    )
    public String populateDashBoard(
        @RequestHeader("X-R-DID")
        ScrubbedInput did,

        @RequestHeader ("X-R-DT")
        ScrubbedInput dt,

        @RequestHeader("X-R-MAIL")
        ScrubbedInput mail,

        @RequestHeader ("X-R-AUTH")
        ScrubbedInput auth
    ) {
        LOG.info("Populate Dashboard");
        return "";
    }
}
