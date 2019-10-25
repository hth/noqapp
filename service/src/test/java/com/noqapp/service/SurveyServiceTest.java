package com.noqapp.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.noqapp.domain.QuestionnaireEntity;
import com.noqapp.domain.SurveyEntity;
import com.noqapp.domain.json.JsonSurvey;
import com.noqapp.domain.types.QuestionTypeEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QuestionnaireManager;
import com.noqapp.repository.SurveyManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * User: hitender
 * Date: 10/25/19 8:16 PM
 */
class SurveyServiceTest {

    @Mock private SurveyManager surveyManager;
    @Mock private QuestionnaireManager questionnaireManager;
    @Mock private BizStoreManager bizStoreManager;
    private NLPService nlpService;

    private SurveyService surveyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        nlpService = new NLPService(new StanfordCoreNLP(props));

        surveyService = new SurveyService(
            surveyManager,
            questionnaireManager,
            bizStoreManager,
            nlpService
        );
    }

    @Test
    void analyzeSurveyResponse() {
        JsonSurvey jsonSurvey = new JsonSurvey()
            .setBizNameId("bizNameId")
            .setBizStoreId("bizStoreId")
            .setCodeQR("codeQR")
            .setDetailedResponse(new String[] {"This is good text"})
            .setDid("did")
            .setOverallRating(7)
            .setQuestionnaireId("questionnaireId");

        SurveyEntity survey = new SurveyEntity()
            .setQuestionnaireId("questionnaireId")
            .setDetailedResponse(new String[] {"This is good text"})
            .setOverallRating(7)
            .setDid("did")
            .setCodeQR("codeQR")
            .setBizNameId("bizNameId")
            .setBizStoreId("bizStoreId");

        Map<String, QuestionTypeEnum> questionForEnglishLocale = new LinkedHashMap<>();
        questionForEnglishLocale.put("Rate this service", QuestionTypeEnum.R);
        questionForEnglishLocale.put("Describe in your own word", QuestionTypeEnum.T);

        Map<Locale, Map<String, QuestionTypeEnum>> questions = new HashMap<>();
        questions.put(Locale.ENGLISH, questionForEnglishLocale);
        QuestionnaireEntity questionnaire = new QuestionnaireEntity()
            .setBizNameId("bizNameId")
            .setQuestions(questions);

        when(questionnaireManager.findById(anyString())).thenReturn(questionnaire);
        surveyService.analyzeSurveyResponse(survey);
    }
}
