package com.noqapp.view.controller.emp;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.service.market.HouseholdItemService;
import com.noqapp.service.market.PropertyRentalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * hitender
 * 6/11/21 12:11 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/emp/marketplace")
public class MarketplaceValidateController {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceValidateController.class);

    private String nextPage;

    private PropertyRentalService propertyRentalService;
    private HouseholdItemService householdItemService;

    @Autowired
    public MarketplaceValidateController(
        @Value("${nextPage:/emp/marketplace/preview}")
        String nextPage,

        PropertyRentalService propertyRentalService,
        HouseholdItemService householdItemService
    ) {
        this.nextPage = nextPage;

        this.propertyRentalService = propertyRentalService;
        this.householdItemService = householdItemService;
    }

    @GetMapping(value = "/approval/{id}/{businessTypeEnum}/preview", produces = "text/html;charset=UTF-8")
    public String preview(
        @PathVariable("id")
        ScrubbedInput id,

        @PathVariable("businessTypeEnum")
        ScrubbedInput businessTypeEnum,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to marketplace approval for {} {} by {}", id, businessTypeEnum, queueUser.getQueueUserId());

        try {
            BusinessTypeEnum businessType = BusinessTypeEnum.valueOf(businessTypeEnum.getText().toUpperCase());
            MarketplaceEntity marketplace;
            switch (businessType) {
                case PR:
                    marketplace = propertyRentalService.findOneById(id.getText());
                    break;
                case HI:
                    marketplace = householdItemService.findOneById(id.getText());
                    break;
                default:
                    LOG.error("Reached unsupported condition {}", businessTypeEnum.getText());
                    throw new UnsupportedOperationException("Reached un-supported condition");
            }
            model.addAttribute("marketplace", marketplace);
            return nextPage;
        } catch (Exception e) {
            LOG.error("Failed updated status for marketplace id={} businessType={} qid={} reason={}",
                id.getText(),
                businessTypeEnum.getText(),
                queueUser.getQueueUserId(),
                e.getLocalizedMessage(),
                e);

            return "redirect:/emp/landing";
        }
    }
}
