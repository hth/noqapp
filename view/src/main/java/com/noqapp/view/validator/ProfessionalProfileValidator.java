package com.noqapp.view.validator;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.view.form.ProfessionalProfileEditForm;
import com.noqapp.view.form.ProfessionalProfileForm;
import com.noqapp.view.form.business.CategoryLandingForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 6/28/18 1:10 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class ProfessionalProfileValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ProfessionalProfileValidator.class);

    private ProfessionalProfileService professionalProfileService;

    @Autowired
    public ProfessionalProfileValidator(ProfessionalProfileService professionalProfileService) {
        this.professionalProfileService = professionalProfileService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CategoryLandingForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", new Object[]{"Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "monthYear", "field.required", new Object[]{"Date Achieved"});

        ProfessionalProfileEditForm form = (ProfessionalProfileEditForm) target;

        switch (form.getAction()) {
            case "awards":
                if (!isProfessionalProfileComplete(form.getQid())) {
                    errors.reject(
                        "inCompleteProfessionalProfile",
                        "Education or License in professional profile cannot be empty. Please fill these up first.");
                }
                break;
            case "licenses":
            case "education":
                break;
            default:
                LOG.error("Reached un-supported condition {}", form.getAction());
                throw new UnsupportedOperationException("Reached un-supported condition");
        }

        if (StringUtils.isNotBlank(form.getMonthYear()) && !DateUtil.DOB_PATTERN.matcher(form.getMonthYear()).matches()) {
            errors.rejectValue("monthYear",
                "field.invalid",
                new Object[]{"Date Achieved", form.getMonthYear()},
                "Date Achieved should be of format " + DateUtil.YYYY_MM_DD);
        }
    }

    public void validateProfessionalProfileForm(Object target, Errors errors) {
        ProfessionalProfileForm form = (ProfessionalProfileForm) target;

        if (!isProfessionalProfileComplete(form.getQid())) {
            errors.reject(
                "inCompleteProfessionalProfile",
                "Education or License in professional profile cannot be empty. Please fill these up first.");
        }

        if (StringUtils.isNotBlank(form.getPracticeStart()) && !DateUtil.DOB_PATTERN.matcher(form.getPracticeStart()).matches()) {
            errors.rejectValue("practiceStart",
                "field.invalid",
                new Object[]{"Practicing Since", form.getPracticeStart()},
                "Practicing Since should be of format " + DateUtil.YYYY_MM_DD);
        }
    }

    /**
     * Validate is professional profile is complete.
     *
     * @param qid
     * @return
     */
    private boolean isProfessionalProfileComplete(String qid) {
        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(qid);
        return !professionalProfile.getLicenses().isEmpty() || !professionalProfile.getEducation().isEmpty();
    }
}
