package com.noqapp.view.controller.emp.validator;

import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.view.form.marketplace.MarketplaceForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * hitender
 * 11/7/21 8:29 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class MarketplaceValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        MarketplaceForm marketplaceForm = (MarketplaceForm) target;
        if (ActionTypeEnum.REJECT == marketplaceForm.getActionType() && null == marketplaceForm.getMarketplaceRejectReason()) {
            errors.rejectValue("marketplaceRejectReason",
                "field.reject",
                new Object[]{""},
                "Please provide the reason for rejection");
        }
    }
}
