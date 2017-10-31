package com.noqapp.view.controller.access;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.view.form.BusinessInviteForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * User: hitender
 * Date: 10/31/17 11:41 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/access/businessInvite")
public class BusinessInviteController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessInviteController.class);

    /**
     * Refers to landing.jsp.
     */
    private String nextPage;

    private BizService bizService;
    private AccountService accountService;

    @Autowired
    public BusinessInviteController(
            @Value("${nextPage:/access/businessInvite}")
            String nextPage,

            BizService bizService,
            AccountService accountService
    ) {
        this.nextPage = nextPage;
        this.bizService = bizService;
        this.accountService = accountService;
    }

    @Timed
    @ExceptionMetered
    @RequestMapping (
            method = RequestMethod.GET
    )
    public String loadForm(
            @ModelAttribute("businessInvite")
            BusinessInviteForm businessInvite
    ) {
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        List<BizNameEntity> bizNames = bizService.findByInviteeCode(userProfile.getInviteCode());
        businessInvite.setBizNames(bizNames);

        return nextPage;
    }

}
