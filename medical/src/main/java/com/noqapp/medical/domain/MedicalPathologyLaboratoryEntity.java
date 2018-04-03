package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * hitender
 * 2/24/18 8:26 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_PAT_LAB")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_pat_lab_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalPathologyLaboratoryEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @DBRef
    @Field("PE")
    private List<MedicalPathologyEntity> medicalPathologies;

    @Field("TR")
    private String testResult;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalPathologyLaboratoryEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public List<MedicalPathologyEntity> getMedicalPathologies() {
        return medicalPathologies;
    }

    public MedicalPathologyLaboratoryEntity setMedicalPathologies(List<MedicalPathologyEntity> medicalPathologies) {
        this.medicalPathologies = medicalPathologies;
        return this;
    }

    public MedicalPathologyLaboratoryEntity addMedicalLabTests(MedicalPathologyEntity medicalLabTest) {
        this.medicalPathologies.add(medicalLabTest);
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public MedicalPathologyLaboratoryEntity setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
