package com.noqapp.view.controller.access;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.view.form.RewardsForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Shows all possible rewards accumulated.
 *
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
@RequestMapping(value = "/access/rewards")
public class RewardsController {
    private static final Logger LOG = LoggerFactory.getLogger(RewardsController.class);

    private String nextPage;

    private BizService bizService;
    private AccountService accountService;

    @Autowired
    public RewardsController(
        @Value("${nextPage:/access/rewards}")
        String nextPage,

        BizService bizService,
        AccountService accountService
    ) {
        this.nextPage = nextPage;
        this.bizService = bizService;
        this.accountService = accountService;
    }

    @GetMapping
    public String loadForm(
        @ModelAttribute("rewards")
        RewardsForm rewards
    ) {
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        List<BizNameEntity> bizNames = bizService.findByInviteeCode(userProfile.getInviteCode());
        rewards.setBizNames(bizNames);

        return nextPage;
    }

}
