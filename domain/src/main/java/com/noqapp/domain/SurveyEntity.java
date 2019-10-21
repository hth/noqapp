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

    /**
     * Device Id of purchaser. DID is of the purchaserQid. Helps in notifying user of changes through FCM.
     * Or
     * Guardian's DID.
     */
    @DBMapping
    @Field ("DID")
    private String did;

    @Field("OR")
    private int overallRating;

    @Field("DR")
    private String[] detailedResponse;

    @Field("QV")
    private String questionnaireId;
}
