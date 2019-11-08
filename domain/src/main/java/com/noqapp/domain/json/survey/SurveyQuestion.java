package com.noqapp.domain.json.survey;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.QuestionTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Set;

/**
 * Used in flow and as a form.
 * User: hitender
 * Date: 11/6/19 1:33 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyQuestion extends AbstractDomain implements Serializable {

    @JsonProperty("q")
    @Field("Q")
    private String question;

    @JsonProperty("t")
    @Field("T")
    private QuestionTypeEnum questionType;

    @JsonProperty("a")
    @Field("A")
    private Set<String> availableResponses;

    @JsonProperty("r")
    @Field("R")
    private boolean required;

    /* Self defined category. */
    @JsonProperty("c")
    @Field("C")
    private String questionCategoryId;

    /* Skip works on boolean fields. */
    @JsonProperty("s")
    @Field("S")
    private String skipTo;

    public String getQuestion() {
        return question;
    }

    public SurveyQuestion setQuestion(String question) {
        this.question = question;
        return this;
    }

    public QuestionTypeEnum getQuestionType() {
        return questionType;
    }

    public SurveyQuestion setQuestionType(QuestionTypeEnum questionType) {
        this.questionType = questionType;
        return this;
    }

    public Set<String> getAvailableResponses() {
        return availableResponses;
    }

    public SurveyQuestion setAvailableResponses(Set<String> availableResponses) {
        this.availableResponses = availableResponses;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public SurveyQuestion setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getQuestionCategoryId() {
        return questionCategoryId;
    }

    public SurveyQuestion setQuestionCategoryId(String questionCategoryId) {
        this.questionCategoryId = questionCategoryId;
        return this;
    }

    public String getSkipTo() {
        return skipTo;
    }

    public SurveyQuestion setSkipTo(String skipTo) {
        this.skipTo = skipTo;
        return this;
    }
}
