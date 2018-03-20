package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.PhysicalExamEnum;
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
@Document(collection = "M_PHY_E")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_phy_e_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalPhysicalExaminationEntity extends BaseEntity {

    @NotNull
    @Field("MP")
    private String medicalPhysicalReferenceId;

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field ("PE")
    private PhysicalExamEnum physicalExam;

    @Field ("VA")
    private String value;

    public String getMedicalPhysicalReferenceId() {
        return medicalPhysicalReferenceId;
    }

    public MedicalPhysicalExaminationEntity setMedicalPhysicalReferenceId(String medicalPhysicalReferenceId) {
        this.medicalPhysicalReferenceId = medicalPhysicalReferenceId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalPhysicalExaminationEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public PhysicalExamEnum getPhysicalExam() {
        return physicalExam;
    }

    public MedicalPhysicalExaminationEntity setPhysicalExam(PhysicalExamEnum physicalExam) {
        this.physicalExam = physicalExam;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MedicalPhysicalExaminationEntity setValue(String value) {
        this.value = value;
        return this;
    }
}
