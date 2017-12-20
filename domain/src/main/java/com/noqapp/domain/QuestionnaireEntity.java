package com.noqapp.domain;

import com.noqapp.domain.types.QuestionnaireTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User is allowed to make only three changes to the questions in a year.
 *
 * hitender
 * 12/18/17 1:53 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "QUESTIONNAIRE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "questionnaire_idx", def = "{'BS' : -1}", unique = false, background = true)
})
public class QuestionnaireEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionnaireEntity.class);

    @NotNull
    @Field("QE")
    private QuestionnaireTypeEnum questionnaire;

    @NotNull
    @Field("BS")
    private String bizStoreId;

    @NotNull
    @Field("Q")
    private String question;

    public QuestionnaireTypeEnum getQuestionnaire() {
        return questionnaire;
    }

    public QuestionnaireEntity setQuestionnaire(QuestionnaireTypeEnum questionnaire) {
        this.questionnaire = questionnaire;
        return this;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public QuestionnaireEntity setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public QuestionnaireEntity setQuestion(String question) {
        this.question = question;
        return this;
    }
}
