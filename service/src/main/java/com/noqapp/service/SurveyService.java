package com.noqapp.service;

import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QuestionnaireEntity;
import com.noqapp.domain.SurveyEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonQuestionnaire;
import com.noqapp.domain.json.JsonSurvey;
import com.noqapp.domain.json.chart.ChartLineData;
import com.noqapp.domain.types.QuestionTypeEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QuestionnaireManager;
import com.noqapp.repository.SurveyManager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: hitender
 * Date: 10/20/19 6:44 AM
 */
@Service
public class SurveyService {
    private static final Logger LOG = LoggerFactory.getLogger(SurveyService.class);

    private SurveyManager surveyManager;
    private QuestionnaireManager questionnaireManager;
    private BizStoreManager bizStoreManager;
    private NLPService nlpService;

    private ExecutorService executorService;

    private final Cache<String, String> cache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(2, TimeUnit.HOURS)
        .build();

    public SurveyService(
        SurveyManager surveyManager,
        QuestionnaireManager questionnaireManager,
        BizStoreManager bizStoreManager,
        NLPService nlpService
    ) {
        this.surveyManager = surveyManager;
        this.questionnaireManager = questionnaireManager;
        this.bizStoreManager = bizStoreManager;
        this.nlpService = nlpService;

        this.executorService = newCachedThreadPool();
    }

    public void saveSurveyQuestionnaire(String bizNameId, Map<Locale, Map<String, QuestionTypeEnum>> localeWithQuestions) {
        QuestionnaireEntity questionnaire = new QuestionnaireEntity()
            .setBizNameId(bizNameId)
            .setQuestions(localeWithQuestions);
        questionnaireManager.save(questionnaire);
    }

    public QuestionnaireEntity findByQuestionnaireId(String questionnaireId) {
        return questionnaireManager.findById(questionnaireId);
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
        executorService.submit(() -> analyzeSurveyResponse(survey));
    }

    /** Support realtime overall rating. */
    public ChartLineData getRecentOverallRating(String bizNameId) {
        SurveyEntity survey = surveyManager.getRecentOverallRating(bizNameId);
        String sentimentColor = "#d3d3d3";
        if (null != survey) {
            BizStoreEntity bizStore = bizStoreManager.getById(survey.getBizStoreId());
            if (null != survey.getSentimentType()) {
                sentimentColor = survey.getSentimentType() == SentimentTypeEnum.P ? "#008800" : "#7C0A02";
            }
            ChartLineData chartLineData = new ChartLineData()
                .setValue(String.valueOf(survey.getOverallRating()))
                .setSentimentColor(sentimentColor)
                .setDate(survey.getCreated().getTime());

            chartLineData.populateLocation(bizStore.getArea(), bizStore.getTown());
            LOG.debug("{}", chartLineData);
            cache.put(bizNameId, sentimentColor);
            return chartLineData;
        }

        sentimentColor = cache.getIfPresent(bizNameId);
        return new ChartLineData()
            .setValue(String.valueOf(0))
            .setSentimentColor(StringUtils.isNotBlank(sentimentColor) ? sentimentColor : "#d3d3d3")
            .setDate(new Date().getTime());
    }

    protected void analyzeSurveyResponse(SurveyEntity survey) {
        QuestionnaireEntity questionnaire = findByQuestionnaireId(survey.getQuestionnaireId());
        Locale locale = questionnaire.getQuestions().keySet().iterator().next();
        Map<String, QuestionTypeEnum> questions = questionnaire.getQuestions().get(locale);

        StringBuilder allText = new StringBuilder();
        int counter = -1;
        for (String key : questions.keySet()) {
            if (counter > -1) {
                if (questions.get(key) == QuestionTypeEnum.T) {
                    allText.append(survey.getDetailedResponse()[counter]);
                }
            }
            counter++;
        }
        LOG.debug("id={} keySet={} counter={} computeSentimentOn={}", questionnaire.getId(), questions.keySet(), counter, allText);

        if (StringUtils.isNotBlank(allText.toString())) {
            SentimentTypeEnum sentimentType = nlpService.computeSentiment(allText.toString());
            LOG.debug("sentiment={} text={}", sentimentType, allText.toString());
            surveyManager.updateSentiment(survey.getId(), sentimentType);
        }
    }
}