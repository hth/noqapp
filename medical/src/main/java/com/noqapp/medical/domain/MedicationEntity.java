package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
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
@Document(collection = "M_MDC")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_mdc_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicationEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("ME")
    private List<MedicineEntity> medicines;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicationEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public List<MedicineEntity> getMedicines() {
        return medicines;
    }

    public MedicationEntity setMedicines(List<MedicineEntity> medicines) {
        this.medicines = medicines;
        return this;
    }

    public MedicationEntity addMedicine(MedicineEntity medicine) {
        this.medicines.add(medicine);
        return this;
    }
}
