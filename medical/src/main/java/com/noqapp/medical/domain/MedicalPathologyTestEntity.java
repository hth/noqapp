package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 2/25/18 3:37 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_PATHOLOGY_TEST")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_pathology_test_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalPathologyTestEntity extends BaseEntity {

    /** Maps to MedicalPathologyEntity Id. */
    @NotNull
    @Field("PRI")
    private String medicalPathologyReferenceId;

    /** Maps to PathologyEntity Id. */
    @NotNull
    @Field("PI")
    private String pathologyReferenceId;

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("NA")
    private String name;

    @Field("TR")
    private String testResult;

    public String getMedicalPathologyReferenceId() {
        return medicalPathologyReferenceId;
    }

    public MedicalPathologyTestEntity setMedicalPathologyReferenceId(String medicalPathologyReferenceId) {
        this.medicalPathologyReferenceId = medicalPathologyReferenceId;
        return this;
    }

    public String getPathologyReferenceId() {
        return pathologyReferenceId;
    }

    public MedicalPathologyTestEntity setPathologyReferenceId(String pathologyReferenceId) {
        this.pathologyReferenceId = pathologyReferenceId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalPathologyTestEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getName() {
        return name;
    }

    public MedicalPathologyTestEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public MedicalPathologyTestEntity setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
