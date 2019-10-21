package com.noqapp.domain;

import com.noqapp.domain.annotation.DBMapping;
import com.noqapp.domain.types.QuestionTypeEnum;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Questionnaire are holders of questions.
 *
 * User: hitender
 * Date: 10/19/19 11:40 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "QUESTIONNAIRE")
public class QuestionnaireEntity extends BaseEntity {

    @DBMapping
    @Field("BN")
    private String bizNameId;

    @DBMapping
    @Field("QS")
    private Map<Locale, Map<String, QuestionTypeEnum>> questions = new LinkedHashMap<>();

    public String getBizNameId() {
        return bizNameId;
    }

    public QuestionnaireEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public Map<Locale, Map<String, QuestionTypeEnum>> getQuestions() {
        return questions;
    }

    public QuestionnaireEntity setQuestions(Map<Locale, Map<String, QuestionTypeEnum>> questions) {
        this.questions = questions;
        return this;
    }
}
