package com.noqapp.service;

import com.noqapp.domain.QuestionnaireEntity;
import com.noqapp.domain.SurveyEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonQuestionnaire;
import com.noqapp.domain.json.JsonSurvey;
import com.noqapp.domain.types.QuestionTypeEnum;
import com.noqapp.repository.QuestionnaireManager;
import com.noqapp.repository.SurveyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User: hitender
 * Date: 10/20/19 6:44 AM
 */
@Service
public class SurveyService {
    private static final Logger LOG = LoggerFactory.getLogger(SurveyService.class);

    private SurveyManager surveyManager;
    private QuestionnaireManager questionnaireManager;

    public SurveyService(SurveyManager surveyManager, QuestionnaireManager questionnaireManager) {
        this.surveyManager = surveyManager;
        this.questionnaireManager = questionnaireManager;
    }

    public void saveSurveyQuestionnaire(String bizNameId, Map<Locale, Map<String, QuestionTypeEnum>> questionsWithLocale) {
        QuestionnaireEntity questionnaire = new QuestionnaireEntity()
            .setBizNameId(bizNameId)
            .setQuestions(questionsWithLocale);
        questionnaireManager.save(questionnaire);
    }

    public List<QuestionnaireEntity> findAll(String bizNameId) {
        return questionnaireManager.findAll(bizNameId);
    }

    public QuestionnaireEntity findLatest(String bizNameId) {
        return questionnaireManager.findLatest(bizNameId);
    }

    @Mobile
    public JsonQuestionnaire findOne(String bizNameId) {
        return JsonQuestionnaire.populateJsonQuestionnaire(findLatest(bizNameId));
    }

    @Mobile
    public void saveSurveyResponse(JsonSurvey jsonSurvey) {
        SurveyEntity survey = new SurveyEntity()
            .setBizStoreId(jsonSurvey.getBizStoreId())
            .setBizNameId(jsonSurvey.getBizNameId())
            .setCodeQR(jsonSurvey.getCodeQR())
            .setDid(jsonSurvey.getDid())
            .setOverallRating(jsonSurvey.getOverallRating())
            .setDetailedResponse(jsonSurvey.getDetailedResponse())
            .setQuestionnaireId(jsonSurvey.getQuestionnaireId());
        surveyManager.save(survey);
    }
}
