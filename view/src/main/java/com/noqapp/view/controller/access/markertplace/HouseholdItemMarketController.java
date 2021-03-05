package com.noqapp.view.controller.access.markertplace;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.service.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * hitender
 * 2/24/21 6:07 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/access/marketplace/household")
public class HouseholdItemMarketController {
    private static final Logger LOG = LoggerFactory.getLogger(HouseholdItemMarketController.class);

    private String householdItemMarketplaceFlowActions;

    private AccountService accountService;

    @Autowired
    public HouseholdItemMarketController(
        @Value("${householdItemMarketplaceFlowActions:redirect:/access/marketplace/householdItem}")
        String householdItemMarketplaceFlowActions,

        AccountService accountService
    ) {
        this.householdItemMarketplaceFlowActions = householdItemMarketplaceFlowActions;
        this.accountService = accountService;
    }

    @GetMapping(value = "/post")
    public String postOnMarketplace(RedirectAttributes redirectAttributes) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Requested post on marketplace {}", queueUser.getQueueUserId());

        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        if (accountService.accountOpenedInLast10Days(queueUser.getQueueUserId()) || userProfile.isProfileVerified()) {
            redirectAttributes.addFlashAttribute("postingAllowed", true);
        } else {
            LOG.error("Restricted posting to market place {}", queueUser.getQueueUserId());
            redirectAttributes.addFlashAttribute("postingAllowed", false);
        }
        redirectAttributes.addFlashAttribute("businessTypeAsString", BusinessTypeEnum.HI.name());
        redirectAttributes.addFlashAttribute("editMode", false);
        return householdItemMarketplaceFlowActions;
    }

    @GetMapping(value = "/edit/{businessTypeAsString}/{postId}")
    public String fetchPostOnMarketplace(
        @PathVariable("businessTypeAsString")
        String businessTypeAsString,

        @PathVariable("postId")
        String postId,

        RedirectAttributes redirectAttributes
    ) {
        LOG.info("Requested post on marketplace {}", householdItemMarketplaceFlowActions);

        redirectAttributes.addFlashAttribute("postId", postId);
        redirectAttributes.addFlashAttribute("businessTypeAsString", businessTypeAsString);
        redirectAttributes.addFlashAttribute("postingAllowed", true);
        redirectAttributes.addFlashAttribute("editMode", true);
        return householdItemMarketplaceFlowActions;
    }
}
