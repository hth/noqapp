package com.noqapp.view.validator;

import com.noqapp.common.utils.Constants;
import com.noqapp.service.BizService;
import com.noqapp.view.form.business.CategoryLandingForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;

import static com.noqapp.common.utils.Constants.WORD_PATTERN;

/**
 * hitender
 * 12/20/17 5:52 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class BusinessCategoryValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCategoryValidator.class);

    private BizService bizService;

    @Autowired
    public BusinessCategoryValidator(BizService bizService) {
        this.bizService = bizService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CategoryLandingForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "categoryName", "field.required", new Object[]{"New Category"});

        if (!errors.hasErrors()) {
            CategoryLandingForm form = (CategoryLandingForm) target;

            if (!WORD_PATTERN.matcher(form.getCategoryName().getText()).matches()) {
                errors.rejectValue("categoryName",
                        "invalid.characters",
                        new Object[]{form.getCategoryName(), Constants.WORD_PATTERN_TEXT},
                        form.getCategoryName()
                                + " category contains not supported characters. Should contain only characters like "
                                + Constants.WORD_PATTERN_TEXT);
            }

            if (!errors.hasErrors()) {
                if (bizService.existCategory(form.getCategoryName().getText(), form.getBizNameId().getText())) {
                    errors.rejectValue("categoryName",
                            "category.exists",
                            new Object[]{form.getCategoryName()},
                            form.getCategoryName() + " category already exists");
                }
            }
        }
    }
}
