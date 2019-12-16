package com.noqapp.view.validator;

import com.noqapp.view.form.ForgotRecoverForm;
import com.noqapp.view.form.SearchForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Used for all kind of search.
 * hitender
 * 2/7/18 11:57 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class SearchValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ForgotRecoverValidator.class);

    @Value("${SearchValidator.searchLength}")
    private int searchLength;

    @Override
    public boolean supports(Class<?> clazz) {
        return ForgotRecoverForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "search", "field.required", new Object[]{"Search"});

        if (!errors.hasErrors()) {
            SearchForm searchForm = (SearchForm) obj;
            if (searchForm.getSearch().getText().length() < searchLength) {
                errors.rejectValue("search",
                        "field.length",
                        new Object[]{"Search", searchLength},
                        "Search minimum length is less than " + searchLength + " characters");
            }
        }
    }
}
