package com.noqapp.domain.flow;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.types.QuestionTypeEnum;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: hitender
 * Date: 10/20/19 6:52 AM
 */
public class Questionnaire implements Serializable {

    private Locale locale;
    private String question;
    private QuestionTypeEnum questionType;

    /** Master record. */
    private Map<Locale, Map<String, QuestionTypeEnum>> localeWithQuestions = new LinkedHashMap<>();

    /** Pre-filled. */
    private Map<String, String> questionTypes = QuestionTypeEnum.asMapWithNameAsKey();
    private Map<String, String> supportedLocales = CommonUtil.getLanguages();

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

    public Map<Locale, Map<String, QuestionTypeEnum>> getLocaleWithQuestions() {
        return localeWithQuestions;
    }

    public Questionnaire setLocaleWithQuestions(Map<Locale, Map<String, QuestionTypeEnum>> localeWithQuestions) {
        this.localeWithQuestions = localeWithQuestions;
        return this;
    }

    public Questionnaire addLocaleWithQuestions(Locale locale, Map<String, QuestionTypeEnum> questions) {
        this.localeWithQuestions.put(locale, questions);
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
