package com.noqapp.view.flow.merchant;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.QuestionnaireEntity;
import com.noqapp.domain.flow.Questionnaire;
import com.noqapp.domain.json.survey.SurveyQuestion;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.PublishStatusEnum;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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
    public Questionnaire initiate(String questionnaireId) {
        LOG.info("AddQuestionnaire Start {}", questionnaireId);
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        /* Above condition to make sure users with right roles and access gets access. */

        if (StringUtils.isBlank(questionnaireId)) {
            BusinessUserEntity businessUser = businessUserService.loadBusinessUser();

            QuestionnaireEntity questionnaire = new QuestionnaireEntity().setBizNameId(businessUser.getBizName().getId());
            surveyService.saveSurveyQuestionnaire(questionnaire);
            return new Questionnaire()
                .setQuestionnaireId(questionnaire.getId());
        } else {
            QuestionnaireEntity questionnaire = surveyService.findByQuestionnaireId(questionnaireId);
            return new Questionnaire()
                .setQuestionnaireId(questionnaire.getId())
                .setLocaleWithQuestions(questionnaire.getQuestions())
                .setEditable(surveyService.isEditable(questionnaireId))
                .setTitle(questionnaire.getTitle());
        }
    }

    /** Add locale. */
    @SuppressWarnings("all")
    public Questionnaire addLocale(Questionnaire questionnaire, MessageContext messageContext) {
        if (StringUtils.isBlank(questionnaire.getTitle())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("title")
                    .defaultText("Title cannot be empty")
                    .build());

            return questionnaire;
        }

        if (null == questionnaire.getLocale()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("locale")
                    .defaultText("Language cannot be empty")
                    .build());

            return questionnaire;
        }

        if (questionnaire.getLocaleWithQuestions().keySet().isEmpty() && null != questionnaire.getLocale()) {
            if (!"en".equals(questionnaire.getLocale().getLanguage())) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("locale")
                        .defaultText("First locale has to be English")
                        .build());

                questionnaire.setLocale(Locale.ENGLISH);
                return questionnaire;
            }
        }

        if (questionnaire.getLocaleWithQuestions().containsKey(questionnaire.getLocale())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("locale")
                    .defaultText(questionnaire.getLocale().getDisplayLanguage() + " already exists")
                    .build());

            return questionnaire;
        }

        questionnaire.getLocaleWithQuestions().put(questionnaire.getLocale(), new LinkedList<>());
        updateQuestionnaire(questionnaire, PublishStatusEnum.I);
        questionnaire.setLocale(null);
        return questionnaire;
    }

    /** Add question to each locale. */
    @SuppressWarnings("all")
    public Questionnaire addQuestion(Questionnaire questionnaire, MessageContext messageContext) {
        if (messageContext.hasErrorMessages()) {
            return questionnaire;
        }

        if (StringUtils.isNotBlank(questionnaire.getQuestion())) {
            List<SurveyQuestion> update = questionnaire.getLocaleWithQuestions().get(questionnaire.getLocale());
            SurveyQuestion surveyQuestion = new SurveyQuestion()
                .setQuestion(questionnaire.getQuestion())
                .setQuestionType(questionnaire.getQuestionType());
            update.add(surveyQuestion);

            questionnaire.getLocaleWithQuestions().put(questionnaire.getLocale(), update);
            updateQuestionnaire(questionnaire, PublishStatusEnum.I);
        }
        questionnaire.setLocale(null).setQuestion("").setQuestionType(null);
        return questionnaire;
    }

    /** Completes survey. */
    @SuppressWarnings("all")
    public Questionnaire completeSurvey(Questionnaire questionnaire, MessageContext messageContext) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        updateQuestionnaire(questionnaire, PublishStatusEnum.P);
        return questionnaire;
    }

    private void updateQuestionnaire(Questionnaire questionnaire, PublishStatusEnum publishStatus) {
        QuestionnaireEntity questionnaireEntity = new QuestionnaireEntity()
            .setPublishStatus(publishStatus)
            .setQuestions(questionnaire.getLocaleWithQuestions())
            .setTitle(questionnaire.getTitle());

        questionnaireEntity.setId(questionnaire.getQuestionnaireId());
        surveyService.saveSurveyQuestionnaire(questionnaireEntity);
    }
}
