package com.noqapp.view.validator;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.nlp.NLPService;
import com.noqapp.view.form.admin.SendNotificationForm;

import org.apache.commons.lang3.StringUtils;

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
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class SendNotificationValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(StoreCategoryValidator.class);
    private static final int MAX_BODY_TEXT_LIMIT = 400;
    private static final int MIN_TITLE_TEXT_LIMIT = 3;
    private static final int MAX_TITLE_TEXT_LIMIT = 32;

    private NLPService nlpService;
    private AccountService accountService;

    @Autowired
    public SendNotificationValidator(NLPService nlpService, AccountService accountService) {
        this.nlpService = nlpService;
        this.accountService = accountService;
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
                if (form.getTitle().getText().length() < MIN_TITLE_TEXT_LIMIT || form.getTitle().getText().length() > MAX_TITLE_TEXT_LIMIT) {
                    LOG.warn("Title length is {}", form.getTitle().getText().length());
                    errors.rejectValue("title",
                        "field.length.min.max",
                        new Object[]{"Title", MIN_TITLE_TEXT_LIMIT, MAX_TITLE_TEXT_LIMIT},
                        "Title minimum length is should be greater than " + MIN_TITLE_TEXT_LIMIT + " and less than " + MAX_TITLE_TEXT_LIMIT + " characters");
                }

                if (form.getBody().getText().length() < MIN_TITLE_TEXT_LIMIT || form.getBody().getText().length() > MAX_BODY_TEXT_LIMIT) {
                    LOG.warn("Body length is {}", form.getBody().getText().length());
                    errors.rejectValue("body",
                        "field.length.min.max",
                        new Object[]{"Body", MIN_TITLE_TEXT_LIMIT, MAX_BODY_TEXT_LIMIT},
                        "Body minimum length is should be greater than " + MIN_TITLE_TEXT_LIMIT + " and less than " + MAX_BODY_TEXT_LIMIT + " characters");
                }

                if (null != form.getQid() && StringUtils.isNotBlank(form.getQid().getText())) {
                    UserAccountEntity userAccount = accountService.findByQueueUserId(form.getQid().getText());
                    if (userAccount == null) {
                        LOG.warn("QID not found {}", form.getQid().getText());
                        errors.rejectValue("qid",
                            "field.qid.notFound",
                            new Object[]{form.getQid().getText()},
                            "No match or invalid QID " + form.getQid().getText());
                    }
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
