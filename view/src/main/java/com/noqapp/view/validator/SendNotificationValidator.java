package com.noqapp.view.validator;

import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.service.NLPService;
import com.noqapp.view.form.admin.SendNotificationForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Map;

/**
 * hitender
 * 2019-02-12 06:41
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class SendNotificationValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(StoreCategoryValidator.class);

    private NLPService nlpService;

    @Autowired
    public SendNotificationValidator(NLPService nlpService) {
        this.nlpService = nlpService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SendNotificationForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "field.required", new Object[]{"Title"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "body", "field.required", new Object[]{"Body"});

        if (!errors.hasErrors()) {
            SendNotificationForm form = (SendNotificationForm) target;

            if (!errors.hasErrors()) {
                if (form.getTitle().getText().length() < 3 || form.getTitle().getText().length() > 32) {
                    errors.rejectValue("title",
                        "field.length.min.max",
                        new Object[]{"Title", 3, 32},
                        "Title minimum length is should be greater than 3 and less than 32 characters");
                }

                if (form.getBody().getText().length() < 3 || form.getBody().getText().length() > 256) {
                    errors.rejectValue("body",
                        "field.length.min.max",
                        new Object[]{"Body", 3, 256},
                        "Body minimum length is should be greater than 3 and less than 256 characters");
                }

                if (!form.isIgnoreSentiments()) {
                    SentimentTypeEnum sentimentType = nlpService.computeSentiment(form.getTitle().getText());
                    if (sentimentType == SentimentTypeEnum.N) {
                        LOG.warn("Found {} for {}", sentimentType, form.getTitle().getText());
                        Map<String, SentimentTypeEnum> deconstruct = nlpService.computeSentimentPerSentence(form.getTitle().getText());
                        for (String sentence : deconstruct.keySet()) {
                            LOG.info("{} {}", deconstruct.get(sentence), sentence);
                        }
                        errors.rejectValue("title",
                            "improve.statement.sentiment",
                            new Object[]{"Title"},
                            "Please make message under title better");
                    }

                    sentimentType = nlpService.computeSentiment(form.getBody().getText());
                    if (sentimentType == SentimentTypeEnum.N) {
                        LOG.warn("Found {} for {}", sentimentType, form.getBody().getText());
                        Map<String, SentimentTypeEnum> deconstruct = nlpService.computeSentimentPerSentence(form.getBody().getText());
                        for (String sentence : deconstruct.keySet()) {
                            LOG.info("{} {}", deconstruct.get(sentence), sentence);
                        }
                        errors.rejectValue("body",
                            "improve.statement.sentiment",
                            new Object[]{"Body"},
                            "Please make message under body better");
                    }
                }
            }
        }
    }
}
