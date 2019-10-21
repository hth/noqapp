package com.noqapp.view.flow.merchant.validator;

import com.noqapp.domain.flow.Questionnaire;
import com.noqapp.domain.types.QuestionTypeEnum;
import com.noqapp.view.controller.access.LandingController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/**
 * User: hitender
 * Date: 10/20/19 7:06 AM
 */
@Component
public class SurveyFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisorFlowValidator.class);

    @SuppressWarnings("unused")
    public String validateLocaleAdded(Questionnaire questionnaire, MessageContext messageContext) {
        LOG.info("validate locale added={}", questionnaire.getQuestionsWithLocale().keySet());

        String status = LandingController.SUCCESS;
        if (questionnaire.getQuestionsWithLocale().keySet().isEmpty()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("question")
                    .defaultText("Select at least one language to proceed")
                    .build());
            status = "failure";
        }

        LOG.info("validatePhoneNumber status={}", status);
        return status;
    }

    @SuppressWarnings("unused")
    public String validateQuestion(Questionnaire questionnaire, MessageContext messageContext) {
        LOG.info("validate questionnaire for locale={}", questionnaire.getLocale());

        String status = LandingController.SUCCESS;
        Map<String, QuestionTypeEnum> questions = questionnaire.getQuestionsWithLocale().get(questionnaire.getLocale());
        if (null == questionnaire.getLocale()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("locale")
                    .defaultText("No select Language")
                    .build());
            status = "failure";
        }

        if (null == questions) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("question")
                    .defaultText("No questions added. Please add question.")
                    .build());
            status = "failure";
        }

        if (null == questionnaire.getQuestionType()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("questionType")
                    .defaultText("Please select Response Format")
                    .build());
            status = "failure";
        }

        if (null != questions && questions.size() > 5) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("question")
                    .defaultText("Questions cannot exceed more than " + 5)
                    .build());
            status = "failure";
        }

        LOG.info("validatePhoneNumber status={}", status);
        return status;
    }

    public String validate(Questionnaire questionnaire, MessageContext messageContext) {
        LOG.info("validate all questionnaire");

        String status = LandingController.SUCCESS;
        Map<Locale, Map<String, QuestionTypeEnum>> questionsWithLocale = questionnaire.getQuestionsWithLocale();
        for (Locale locale : questionsWithLocale.keySet()) {
            Map<String, QuestionTypeEnum> questions = questionsWithLocale.get(locale);
            if (questions.isEmpty()) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("question")
                        .defaultText("Survey in " + locale.getDisplayLanguage() + " language needs at least one question")
                        .build());
                status = "failure";
            }
        }

        LOG.info("validatePhoneNumber status={}", status);
        return status;
    }
}
