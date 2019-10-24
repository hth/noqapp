package com.noqapp.domain;

import com.noqapp.domain.annotation.DBMapping;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Survey holds response of all the questions.
 *
 * User: hitender
 * Date: 10/19/19 9:38 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "SURVEY")
public class SurveyEntity extends BaseEntity {

    @DBMapping
    @Field("BS")
    private String bizStoreId;

    @DBMapping
    @Field("BN")
    private String bizNameId;

    @DBMapping
    @Field("QR")
    private String codeQR;

    @DBMapping
    @Field ("DID")
    private String did;

    @Field("OR")
    private int overallRating;

    @Field("DR")
    private String[] detailedResponse;

    @Field("QV")
    private String questionnaireId;

    @Field("FE")
    private boolean fetched;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public SurveyEntity setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public SurveyEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public SurveyEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getDid() {
        return did;
    }

    public SurveyEntity setDid(String did) {
        this.did = did;
        return this;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public SurveyEntity setOverallRating(int overallRating) {
        this.overallRating = overallRating;
        return this;
    }

    public String[] getDetailedResponse() {
        return detailedResponse;
    }

    public SurveyEntity setDetailedResponse(String[] detailedResponse) {
        this.detailedResponse = detailedResponse;
        return this;
    }

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public SurveyEntity setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
        return this;
    }

    public boolean isFetched() {
        return fetched;
    }

    public SurveyEntity setFetched(boolean fetched) {
        this.fetched = fetched;
        return this;
    }
}
