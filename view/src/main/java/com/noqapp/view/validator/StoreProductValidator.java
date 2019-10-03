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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "storeCategoryId", "field.required", new Object[]{"Category"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productName", "field.required", new Object[]{"Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productType", "field.required", new Object[]{"Product Categorization"});

        if (!errors.hasErrors()) {
            StoreProductForm form = (StoreProductForm) target;

            switch (form.getBusinessType()) {
                case PH:
                case RS:
                case FT:
                case GS:
                case ST:
                case HS:
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "unitValue", "field.required", new Object[]{"Unit"});
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "unitOfMeasurement", "field.required", new Object[]{"Measurement"});
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "packageSize", "field.required", new Object[]{"Package Size"});
                    break;
                default:
                    LOG.error("Reached unsupported condition={}", form.getBusinessType());
                    throw new UnsupportedOperationException("Reached unsupported condition " + form.getBusinessType());
            }

            if (!errors.hasErrors()) {
                if (storeProductService.existProductName(form.getBizStoreId().getText(), form.getProductName().getText())) {
                    errors.rejectValue("productName",
                            "productName.exists",
                            new Object[]{form.getProductName()},
                            form.getProductName() + " already exists");
                }

                if (!isNumeric(form.getProductPrice().getText())) {
                    errors.rejectValue("productPrice",
                        "field.number",
                        new Object[]{"Price of Product"},
                        "Price of Product should be number and not exceed more than two decimal place");
                }
            }
        }
    }

    private static boolean isNumeric(String strNum) {
        return strNum.matches("\\d+(\\.\\d{1,2})?");
    }
}
