package com.noqapp.view.validator;

import static com.noqapp.domain.types.TextToSpeechTypeEnum.SN;

import com.noqapp.domain.json.tts.TextToSpeechTemplate;
import com.noqapp.view.form.business.CustomTextToSpeechForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Set;

/**
 * User: hitender
 * Date: 12/16/19 8:26 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class CustomTextToSpeechValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(CustomTextToSpeechValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "textToSpeechType", "field.required", new Object[]{"Customize for"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "languageTag", "field.required", new Object[]{"Select Language"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "template", "field.required", new Object[]{"Voice Announcement"});

        if (!errors.hasErrors()) {
            CustomTextToSpeechForm customTextToSpeechForm = (CustomTextToSpeechForm) target;
            switch (customTextToSpeechForm.getTextToSpeechType()) {
                case SN:
                    for (String word : SN.getDictionary()) {
                        if (!customTextToSpeechForm.getTemplate().contains(word)) {
                            errors.rejectValue("template",
                                "missing.pattern",
                                new Object[]{word, "Voice Announcement", SN.getDictionaryAsString()},
                                word + " is missing from Voice Announcement. Your sentence should contain each of these " + SN.getDictionaryAsString());
                        }
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Reached Unsupported Condition");
            }

            if (null != customTextToSpeechForm.getCustomTextToSpeech() && null != customTextToSpeechForm.getCustomTextToSpeech().getTextToSpeechTemplates()) {
                switch (customTextToSpeechForm.getTextToSpeechType()) {
                    case SN:
                        Set<TextToSpeechTemplate> textToSpeechTemplateSet = customTextToSpeechForm.getCustomTextToSpeech().getTextToSpeechTemplates().get(SN.name());
                        if (null != textToSpeechTemplateSet && textToSpeechTemplateSet.size() >= 2) {
                            errors.rejectValue("template",
                                "max.limit",
                                new Object[]{"Voice Announcement", 2, "announcement"},
                                "Voice Announcement has reached max of " + 2 + " announcement");
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("Reached Unsupported Condition");
                }
            }
        }
    }
}
