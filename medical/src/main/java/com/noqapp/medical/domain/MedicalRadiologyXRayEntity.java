package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 3/15/18 6:04 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_RADIOLOGY_XRAY")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_radiology_xray_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalRadiologyXRayEntity extends BaseEntity {

    /** Maps to MedicalRadiologyEntity Id. */
    @NotNull
    @Field("RRI")
    private String medicalRadiologyReferenceId;

    /** Maps to RadiologyEntity Id. */
    @NotNull
    @Field("RD")
    private String radiologyReferenceId;

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("NA")
    private String name;

    @Field("TR")
    private String testResult;

    public String getMedicalRadiologyReferenceId() {
        return medicalRadiologyReferenceId;
    }

    public MedicalRadiologyXRayEntity setMedicalRadiologyReferenceId(String medicalRadiologyReferenceId) {
        this.medicalRadiologyReferenceId = medicalRadiologyReferenceId;
        return this;
    }

    public String getRadiologyReferenceId() {
        return radiologyReferenceId;
    }

    public MedicalRadiologyXRayEntity setRadiologyReferenceId(String radiologyReferenceId) {
        this.radiologyReferenceId = radiologyReferenceId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalRadiologyXRayEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getName() {
        return name;
    }

    public MedicalRadiologyXRayEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public MedicalRadiologyXRayEntity setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
