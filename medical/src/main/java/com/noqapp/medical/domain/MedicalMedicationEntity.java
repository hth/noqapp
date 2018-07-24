package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

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
@Document(collection = "M_MEDICATION")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_medication_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalMedicationEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("MD")
    private List<String> medicineIds = new LinkedList<>();

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalMedicationEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public List<String> getMedicineIds() {
        return medicineIds;
    }

    public MedicalMedicationEntity setMedicineIds(List<String> medicineIds) {
        this.medicineIds = medicineIds;
        return this;
    }

    public MedicalMedicationEntity addMedicineId(String medicineId) {
        this.medicineIds.add(medicineId);
        return this;
    }
}
