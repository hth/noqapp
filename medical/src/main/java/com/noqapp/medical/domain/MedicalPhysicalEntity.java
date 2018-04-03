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
 * 3/7/18 9:31 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_PHY")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_phy_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalPhysicalEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("PE")
    private Set<String> medicalPhysicalExaminations = new HashSet<>();

    public MedicalPhysicalEntity(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalPhysicalEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public Set<String> getMedicalPhysicalExaminations() {
        return medicalPhysicalExaminations;
    }

    public MedicalPhysicalEntity setMedicalPhysicalExaminations(Set<String> medicalPhysicalExaminations) {
        this.medicalPhysicalExaminations = medicalPhysicalExaminations;
        return this;
    }
}
