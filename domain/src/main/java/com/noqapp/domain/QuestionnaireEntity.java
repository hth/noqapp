package com.noqapp.domain;

import com.noqapp.domain.annotation.DBMapping;
import com.noqapp.domain.json.survey.SurveyQuestion;
import com.noqapp.domain.types.ValidateStatusEnum;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
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

    @Field("TI")
    private String title;

    @DBMapping
    @Field("QS")
    private Map<Locale, List<SurveyQuestion>> questions = new LinkedHashMap<>();

    @Field ("VS")
    private ValidateStatusEnum validateStatus = ValidateStatusEnum.I;

    @Field("PD")
    private Date publishDate;

    public String getBizNameId() {
        return bizNameId;
    }

    public QuestionnaireEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public QuestionnaireEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public Map<Locale, List<SurveyQuestion>> getQuestions() {
        return questions;
    }

    public QuestionnaireEntity setQuestions(Map<Locale, List<SurveyQuestion>> questions) {
        this.questions = questions;
        return this;
    }

    public ValidateStatusEnum getValidateStatus() {
        return validateStatus;
    }

    public QuestionnaireEntity setValidateStatus(ValidateStatusEnum validateStatus) {
        this.validateStatus = validateStatus;
        return this;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public QuestionnaireEntity setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
        return this;
    }
}
