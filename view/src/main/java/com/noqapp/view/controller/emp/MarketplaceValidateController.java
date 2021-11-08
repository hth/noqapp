package com.noqapp.view.controller.emp;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.service.MarketplaceElasticService;
import com.noqapp.service.AccountService;
import com.noqapp.service.FtpService;
import com.noqapp.service.exceptions.NotAValidObjectIdException;
import com.noqapp.service.market.HouseholdItemService;
import com.noqapp.service.market.PropertyRentalService;
import com.noqapp.view.controller.emp.validator.MarketplaceValidator;
import com.noqapp.view.form.marketplace.MarketplaceForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    private String bucketName;
    private String nextPagePropertyRental;
    private String nextPageHouseholdItem;

    private AccountService accountService;
    private PropertyRentalService propertyRentalService;
    private HouseholdItemService householdItemService;
    private MarketplaceElasticService marketplaceElasticService;
    private MarketplaceValidator marketplaceValidator;

    @Autowired
    public MarketplaceValidateController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        @Value("${nextPage:/emp/marketplace/preview/propertyRental}")
        String nextPagePropertyRental,

        @Value("${nextPage:/emp/marketplace/preview/householdItem}")
        String nextPageHouseholdItem,

        AccountService accountService,
        PropertyRentalService propertyRentalService,
        HouseholdItemService householdItemService,
        MarketplaceElasticService marketplaceElasticService,
        MarketplaceValidator marketplaceValidator
    ) {
        this.bucketName = bucketName;
        this.nextPagePropertyRental = nextPagePropertyRental;
        this.nextPageHouseholdItem = nextPageHouseholdItem;

        this.accountService = accountService;
        this.propertyRentalService = propertyRentalService;
        this.householdItemService = householdItemService;
        this.marketplaceElasticService = marketplaceElasticService;
        this.marketplaceValidator = marketplaceValidator;
    }

    /**
     * Loading landing page for marketplace post preview.
     * Gymnastic for PRG.
     */
    @GetMapping(value = "/approval/{id}/{businessTypeEnum}/preview", produces = "text/html;charset=UTF-8")
    public String preview(
        @PathVariable("id")
        ScrubbedInput id,

        @PathVariable("businessTypeEnum")
        ScrubbedInput businessTypeEnum,

        @ModelAttribute ("marketplaceForm")
        MarketplaceForm marketplaceForm,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to marketplace approval for {} {} by {}", id, businessTypeEnum, queueUser.getQueueUserId());

        try {
            if (!Validate.isValidObjectId(id.getText())) {
                LOG.error("Marketplace id should be ObjectId but is {}", id.getText());
                throw new NotAValidObjectIdException("Failed to validated id " + id.getText());
            }

            //Gymnastic to show BindingResult errors if any
            if (model.asMap().containsKey("result")) {
                model.addAttribute("org.springframework.validation.BindingResult.marketplaceForm", model.asMap().get("result"));
                switch (BusinessTypeEnum.valueOf(businessTypeEnum.getText().toUpperCase())) {
                    case PR:
                        PropertyRentalEntity propertyRental = propertyRentalService.findOneById(id.getText());
                        marketplaceForm.setMarketplace(propertyRental);
                        model.addAttribute("userProfile", accountService.findProfileByQueueUserId(propertyRental.getQueueUserId()));
                        return nextPagePropertyRental;
                    case HI:
                        HouseholdItemEntity householdItem = householdItemService.findOneById(id.getText());
                        marketplaceForm.setMarketplace(householdItem);
                        model.addAttribute("userProfile", accountService.findProfileByQueueUserId(householdItem.getQueueUserId()));
                        return nextPageHouseholdItem;
                    default:
                        LOG.error("Reached unsupported condition {}", businessTypeEnum.getText());
                        throw new UnsupportedOperationException("Reached un-supported condition");
                }
            } else {
                BusinessTypeEnum businessType = BusinessTypeEnum.valueOf(businessTypeEnum.getText().toUpperCase());
                model.addAttribute("bucketName", FtpService.marketBucketName(bucketName, businessType));
                switch (businessType) {
                    case PR:
                        PropertyRentalEntity propertyRental = propertyRentalService.findOneById(id.getText());
                        marketplaceForm.setMarketplace(propertyRental);
                        model.addAttribute("userProfile", accountService.findProfileByQueueUserId(propertyRental.getQueueUserId()));
                        return nextPagePropertyRental;
                    case HI:
                        HouseholdItemEntity householdItem = householdItemService.findOneById(id.getText());
                        marketplaceForm.setMarketplace(householdItem);
                        model.addAttribute("userProfile", accountService.findProfileByQueueUserId(householdItem.getQueueUserId()));
                        return nextPageHouseholdItem;
                    default:
                        LOG.error("Reached unsupported condition {}", businessTypeEnum.getText());
                        throw new UnsupportedOperationException("Reached un-supported condition");
                }
            }
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

    @PostMapping(value = "/action")
    public String action(
        @ModelAttribute("marketplaceForm")
        MarketplaceForm marketplaceForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Action on marketplaceId={} action={} qid={}", marketplaceForm.getMarketplaceId(), marketplaceForm.getActionType(), queueUser.getQueueUserId());

        if (!Validate.isValidObjectId(marketplaceForm.getMarketplaceId().getText())) {
            LOG.error("Marketplace id should be ObjectId but is {}", marketplaceForm.getMarketplaceId().getText());
            throw new NotAValidObjectIdException("Failed to validated id " + marketplaceForm.getMarketplaceId().getText());
        }

        switch (marketplaceForm.getBusinessType()) {
            case PR:
                marketplaceForm.setMarketplace(propertyRentalService.findOneById(marketplaceForm.getMarketplaceId().getText()));
                break;
            case HI:
                marketplaceForm.setMarketplace(householdItemService.findOneById(marketplaceForm.getMarketplaceId().getText()));
                break;
            default:
                LOG.error("Reached unsupported condition {}", marketplaceForm.getBusinessType());
                throw new UnsupportedOperationException("Reached un-supported condition");
        }

        marketplaceValidator.validate(marketplaceForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/emp/marketplace/approval/" + marketplaceForm.getMarketplaceId()  + "/" + marketplaceForm.getBusinessType().name() + "/preview";
        }

        MarketplaceEntity marketplace;
        switch (marketplaceForm.getBusinessType()) {
            case HI:
                marketplace = householdItemService.changeStatusOfMarketplace(
                    marketplaceForm.getMarketplaceId().getText(),
                    marketplaceForm.getActionType(),
                    marketplaceForm.getMarketplaceRejectReason(),
                    queueUser.getQueueUserId());
                if (ValidateStatusEnum.A == marketplace.getValidateStatus()) {
                    marketplaceElasticService.save(DomainConversion.getAsMarketplaceElastic(marketplace));
                }
                break;
            case PR:
                marketplace = propertyRentalService.changeStatusOfMarketplace(
                    marketplaceForm.getMarketplaceId().getText(),
                    marketplaceForm.getActionType(),
                    marketplaceForm.getMarketplaceRejectReason(),
                    queueUser.getQueueUserId());
                if (ValidateStatusEnum.A == marketplace.getValidateStatus()) {
                    marketplaceElasticService.save(DomainConversion.getAsMarketplaceElastic(marketplace));
                }
                break;
            default:
                LOG.warn("Reached un-reachable condition {}", marketplaceForm.getActionType());
                throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
        }
        return "redirect:/emp/landing";
    }
}
