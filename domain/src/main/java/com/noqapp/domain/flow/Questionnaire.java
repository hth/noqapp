package com.noqapp.domain.flow;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.json.survey.SurveyQuestion;
import com.noqapp.domain.types.QuestionTypeEnum;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User: hitender
 * Date: 10/20/19 6:52 AM
 */
public class Questionnaire implements Serializable {

    private String questionnaireId;
    private String title;
    private Locale locale;
    private String question;
    private QuestionTypeEnum questionType;

    /** Master record. */
    private Map<Locale, List<SurveyQuestion>> localeWithQuestions = new LinkedHashMap<>();
    private boolean editable = true;

    /** Pre-filled. */
    private Map<String, String> questionTypes = QuestionTypeEnum.asMapWithNameAsKey();
    private Map<String, String> supportedLocales = CommonUtil.getLanguages();

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public Questionnaire setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Questionnaire setTitle(String title) {
        this.title = title;
        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    public Questionnaire setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public Questionnaire setQuestion(String question) {
        this.question = question;
        return this;
    }

    public QuestionTypeEnum getQuestionType() {
        return questionType;
    }

    public Questionnaire setQuestionType(QuestionTypeEnum questionType) {
        this.questionType = questionType;
        return this;
    }

    public Map<Locale, List<SurveyQuestion>> getLocaleWithQuestions() {
        return localeWithQuestions;
    }

    public Questionnaire setLocaleWithQuestions(Map<Locale, List<SurveyQuestion>> localeWithQuestions) {
        this.localeWithQuestions = localeWithQuestions;
        return this;
    }

    public Questionnaire addLocaleWithQuestions(Locale locale, List<SurveyQuestion> questions) {
        this.localeWithQuestions.put(locale, questions);
        return this;
    }

    public boolean isEditable() {
        return editable;
    }

    public Questionnaire setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public Map<String, String> getQuestionTypes() {
        return questionTypes;
    }

    public Questionnaire setQuestionTypes(Map<String, String> questionTypes) {
        this.questionTypes = questionTypes;
        return this;
    }

    public Map<String, String> getSupportedLocales() {
        return supportedLocales;
    }

    public Questionnaire setSupportedLocales(Map<String, String> supportedLocales) {
        this.supportedLocales = supportedLocales;
        return this;
    }
}
