package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * hitender
 * 3/15/18 6:02 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_RDO")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_rdo_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalRadiologyEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("XR")
    private Set<MedicalRadiologyXRayEntity> medicalRadiologyXRays = new HashSet<>();

    @Field("TR")
    private String testResult;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalRadiologyEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public Set<MedicalRadiologyXRayEntity> getMedicalRadiologyXRays() {
        return medicalRadiologyXRays;
    }

    public MedicalRadiologyEntity setMedicalRadiologyXRays(Set<MedicalRadiologyXRayEntity> medicalRadiologyXRays) {
        this.medicalRadiologyXRays = medicalRadiologyXRays;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public MedicalRadiologyEntity setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
