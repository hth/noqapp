package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 3/7/18 9:32 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_PHYSICAL_EXAMINATION")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_physical_examination_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalPhysicalExaminationEntity extends BaseEntity {

    /** Maps to MedicalPhysicalEntity Id. */
    @NotNull
    @Field("PRI")
    private String medicalPhysicalReferenceId;

    /** Maps to PhysicalEntity Id. */
    @NotNull
    @Field("PI")
    private String physicalReferenceId;

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("NA")
    private String name;

    @Field("TR")
    private String testResult;

    public String getMedicalPhysicalReferenceId() {
        return medicalPhysicalReferenceId;
    }

    public MedicalPhysicalExaminationEntity setMedicalPhysicalReferenceId(String medicalPhysicalReferenceId) {
        this.medicalPhysicalReferenceId = medicalPhysicalReferenceId;
        return this;
    }

    public String getPhysicalReferenceId() {
        return physicalReferenceId;
    }

    public MedicalPhysicalExaminationEntity setPhysicalReferenceId(String physicalReferenceId) {
        this.physicalReferenceId = physicalReferenceId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalPhysicalExaminationEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getName() {
        return name;
    }

    public MedicalPhysicalExaminationEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public MedicalPhysicalExaminationEntity setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
