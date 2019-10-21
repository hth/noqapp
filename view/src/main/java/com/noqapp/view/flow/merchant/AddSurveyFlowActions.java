package com.noqapp.view.flow.merchant;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.flow.Questionnaire;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.QuestionTypeEnum;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.SurveyService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 10/20/19 6:56 AM
 */
@Component
public class AddSurveyFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AddSurveyFlowActions.class);

    private BusinessUserService businessUserService;
    private SurveyService surveyService;

    @Autowired
    public AddSurveyFlowActions(
        BusinessUserService businessUserService,
        SurveyService surveyService
    ) {
        this.businessUserService = businessUserService;
        this.surveyService = surveyService;
    }

    @SuppressWarnings("all")
    public Questionnaire initiate(ExternalContext externalContext) {
        LOG.info("AddQuestionnaire Start");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        /* Above condition to make sure users with right roles and access gets access. */

        return new Questionnaire();
    }

    /** Add locale. */
    @SuppressWarnings("all")
    public Questionnaire addLocale(Questionnaire questionnaire, MessageContext messageContext) {
        if (null == questionnaire.getLocale()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("locale")
                    .defaultText("Language cannot be empty")
                    .build());

            return questionnaire;
        }

        if (questionnaire.getQuestionsWithLocale().containsKey(questionnaire.getLocale())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("locale")
                    .defaultText(questionnaire.getLocale().getDisplayLanguage() + " already exists")
                    .build());

            return questionnaire;
        }
        questionnaire.getQuestionsWithLocale().put(questionnaire.getLocale(), new LinkedHashMap<>());
        questionnaire.setLocale(null);
        return questionnaire;
    }

    /** Add question to each locale. */
    @SuppressWarnings("all")
    public Questionnaire addQuestion(Questionnaire questionnaire) {
        if (StringUtils.isNotBlank(questionnaire.getQuestion())) {
            Map<String, QuestionTypeEnum> update =  questionnaire.getQuestionsWithLocale().get(questionnaire.getLocale());
            update.put(questionnaire.getQuestion(), questionnaire.getQuestionType());
            questionnaire.getQuestionsWithLocale().put(questionnaire.getLocale(), update);
        }
        questionnaire.setLocale(null).setQuestion("").setQuestionType(null);
        return questionnaire;
    }

    /** Completes survey. */
    @SuppressWarnings("all")
    public Questionnaire completeSurvey(Questionnaire questionnaire, MessageContext messageContext) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        surveyService.saveSurveyQuestionnaire(businessUser.getBizName().getId(), questionnaire.getQuestionsWithLocale());
        return questionnaire;
    }
}
