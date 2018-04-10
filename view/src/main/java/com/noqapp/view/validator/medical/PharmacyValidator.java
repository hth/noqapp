package com.noqapp.view.validator.medical;

import com.noqapp.common.utils.Constants;
import com.noqapp.medical.service.MedicalMasterDataService;
import com.noqapp.view.form.emp.medical.PharmacyForm;
import com.noqapp.view.validator.BusinessCategoryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static com.noqapp.common.utils.Constants.WORD_PATTERN;

/**
 * hitender
 * 4/8/18 7:31 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class PharmacyValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCategoryValidator.class);

    private MedicalMasterDataService medicalMasterDataService;

    @Autowired
    public PharmacyValidator(MedicalMasterDataService medicalMasterDataService) {
        this.medicalMasterDataService = medicalMasterDataService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PharmacyForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", new Object[]{"Name"});

        if (!errors.hasErrors()) {
            PharmacyForm form = (PharmacyForm) target;

            if (!WORD_PATTERN.matcher(form.getName().getText()).matches()) {
                errors.rejectValue("name",
                        "invalid.characters",
                        new Object[]{form.getName(), Constants.WORD_PATTERN_TEXT},
                        form.getName()
                                + " contains not supported characters. Should contain only characters like "
                                + Constants.WORD_PATTERN_TEXT);
            }

            if (!errors.hasErrors()) {
                if (medicalMasterDataService.existsPharmacy(form.getName().getText())) {
                    errors.rejectValue("name",
                            "name.exists",
                            new Object[]{form.getName().getText()},
                            form.getName().getText() + " already exists");
                }
            }
        }
    }
}
