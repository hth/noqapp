package com.noqapp.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.noqapp.domain.QuestionnaireEntity;
import com.noqapp.domain.SurveyEntity;
import com.noqapp.domain.json.JsonSurvey;
import com.noqapp.domain.json.survey.SurveyQuestion;
import com.noqapp.domain.types.QuestionTypeEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QuestionnaireManager;
import com.noqapp.repository.SurveyManager;
import com.noqapp.service.nlp.NLPService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment, pos, lemma, ner, dcoref");
        props.setProperty("ner.model",
            "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz," +
                "edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz," +
                "edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz," +
                "nlp/noqueue/ner/medical-symptoms-ner-model.ser.gz");
        props.setProperty("parse.maxlen", "100");
        nlpService = new NLPService(
            new StanfordCoreNLP(props),
            new MaxentTagger("nlp/stanford/models/english-bidirectional-distsim.tagger"));

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

        List<SurveyQuestion> questionForEnglishLocale = new ArrayList<>();
        questionForEnglishLocale.add(new SurveyQuestion().setQuestion("Rate this service").setQuestionType(QuestionTypeEnum.R));
        questionForEnglishLocale.add(new SurveyQuestion().setQuestion("Describe in your own word").setQuestionType(QuestionTypeEnum.T));

        Map<Locale, List<SurveyQuestion>> questions = new HashMap<>();
        questions.put(Locale.ENGLISH, questionForEnglishLocale);
        QuestionnaireEntity questionnaire = new QuestionnaireEntity()
            .setBizNameId("bizNameId")
            .setQuestions(questions);

        when(questionnaireManager.findById(anyString())).thenReturn(questionnaire);
        surveyService.analyzeSurveyResponse(survey);
    }
}
