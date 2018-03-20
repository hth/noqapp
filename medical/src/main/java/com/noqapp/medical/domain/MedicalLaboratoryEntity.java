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
@Document(collection = "M_LAB")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_lab_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalLaboratoryEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @DBRef
    @Field("LT")
    private List<MedicalLabTestEntity> medicalLabTests;

    @Field("TR")
    private String testResult;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalLaboratoryEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public List<MedicalLabTestEntity> getMedicalLabTests() {
        return medicalLabTests;
    }

    public MedicalLaboratoryEntity setMedicalLabTests(List<MedicalLabTestEntity> medicalLabTests) {
        this.medicalLabTests = medicalLabTests;
        return this;
    }

    public MedicalLaboratoryEntity addMedicalLabTests(MedicalLabTestEntity medicalLabTest) {
        this.medicalLabTests.add(medicalLabTest);
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public MedicalLaboratoryEntity setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
