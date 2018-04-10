package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
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
@Document(collection = "M_PHYSICAL")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_physical_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalPhysicalEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("PD")
    private Set<String> medicalPhysicalExaminationIds = new LinkedHashSet<>();

    public MedicalPhysicalEntity(@NotNull String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public Set<String> getMedicalPhysicalExaminationIds() {
        return medicalPhysicalExaminationIds;
    }

    public MedicalPhysicalEntity setMedicalPhysicalExaminationIds(Set<String> medicalPhysicalExaminationIds) {
        this.medicalPhysicalExaminationIds = medicalPhysicalExaminationIds;
        return this;
    }
}
