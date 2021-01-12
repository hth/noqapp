package com.noqapp.view.controller.access;

import com.noqapp.service.market.PropertyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * hitender
 * 1/11/21 4:52 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/access/marketplace")
public class MarketplaceController {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceController.class);

    private String postOnMarketplaceFlowActions;

    private PropertyService propertyService;

    @Autowired
    public MarketplaceController(
        @Value("${postOnMarketplaceFlowActions:redirect:/access/postOnMarketplace.htm}")
        String postOnMarketplaceFlowActions,

        PropertyService propertyService
    ) {
        this.postOnMarketplaceFlowActions = postOnMarketplaceFlowActions;
        this.propertyService = propertyService;
    }

    @GetMapping(value = "/post")
    public String postOnMarketplace() {
        LOG.info("Requested post on marketplace {}", postOnMarketplaceFlowActions);
        return postOnMarketplaceFlowActions;
    }

    @GetMapping(value = "/edit/{businessTypeAsString}/{postId}")
    public String fetchPostOnMarketplace(
        @PathVariable("businessTypeAsString")
        String businessTypeAsString,

        @PathVariable("postId")
        String postId,

        RedirectAttributes redirectAttributes
    ) {
        LOG.info("Requested post on marketplace {}", postOnMarketplaceFlowActions);

        redirectAttributes.addFlashAttribute("businessTypeAsString", businessTypeAsString);
        redirectAttributes.addFlashAttribute("postId", postId);
        return postOnMarketplaceFlowActions;
    }
}
