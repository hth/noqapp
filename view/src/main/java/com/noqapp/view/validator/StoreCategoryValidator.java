package com.noqapp.view.validator;

import com.noqapp.service.StoreCategoryService;
import com.noqapp.view.form.StoreCategoryForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * hitender
 * 3/22/18 2:40 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class StoreCategoryValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(StoreCategoryValidator.class);

    private StoreCategoryService storeCategoryService;

    @Autowired
    public StoreCategoryValidator(StoreCategoryService storeCategoryService) {
        this.storeCategoryService = storeCategoryService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StoreCategoryForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "categoryName", "field.required", new Object[]{"Category Name"});

        if (!errors.hasErrors()) {
            StoreCategoryForm form = (StoreCategoryForm) target;

            if (!errors.hasErrors()) {
                if (storeCategoryService.existCategoryName(form.getBizStoreId().getText(), form.getCategoryName().getText())) {
                    errors.rejectValue("categoryName",
                            "category.exists",
                            new Object[]{form.getCategoryName()},
                            form.getCategoryName() + " already exists");
                }

                switch (form.getBusinessType()) {
                    case HS:
                    case PH:
                        LOG.warn("Cannot add category name={} when store business type is of pharmacy bizStoreId={}",
                            form.getCategoryName(), form.getBizStoreId());

                        errors.rejectValue("categoryName",
                                "unsupported.for.businessType",
                                new Object[]{"Category", form.getBusinessType().getDescription()},
                                "Category" + " addition is not supported for " + form.getBusinessType().getDescription());
                        break;
                    default:
                        //Ignore for rest
                }
            }
        }
    }
}
