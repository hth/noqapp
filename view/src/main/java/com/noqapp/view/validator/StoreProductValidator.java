package com.noqapp.view.validator;

import com.noqapp.service.StoreProductService;
import com.noqapp.view.form.StoreProductForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * hitender
 * 3/22/18 1:12 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class StoreProductValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(StoreProductValidator.class);

    private StoreProductService storeProductService;

    @Autowired
    public StoreProductValidator(StoreProductService storeProductService) {
        this.storeProductService = storeProductService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StoreProductForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productName", "field.required", new Object[]{"Product Name"});

        if (!errors.hasErrors()) {
            StoreProductForm form = (StoreProductForm) target;


            if (!errors.hasErrors()) {
                if (storeProductService.existProductName(form.getBizStoreId().getText(), form.getProductName().getText())) {
                    errors.rejectValue("productName",
                            "productName.exists",
                            new Object[]{form.getProductName()},
                            form.getProductName() + " already exists");
                }

                switch (form.getBusinessType()) {
                    case PH:
                        LOG.warn("Cannot add Product when store business type is of pharmacy");
                        errors.rejectValue("productName",
                                "unsupported.for.businessType",
                                new Object[]{"Product", form.getBusinessType().getDescription()},
                                "Product" + " addition is not supported for " + form.getBusinessType().getDescription());
                        break;
                    default:
                        //Ignore for rest
                }
            }
        }
    }
}
