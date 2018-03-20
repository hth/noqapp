package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.MedicationRouteEnum;
import com.noqapp.domain.types.MedicationWithFoodEnum;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 2/25/18 3:36 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_MDC_M")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_mdc_m_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicineEntity extends BaseEntity {
    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("NA")
    private String name;

    @Field("ST")
    private int strength;

    @Field("TI")
    private int times;

    @Field("MF")
    private MedicationWithFoodEnum medicationWithFood;

    @Field ("MR")
    private MedicationRouteEnum medicationRoute;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicineEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getName() {
        return name;
    }

    public MedicineEntity setName(String name) {
        this.name = name;
        return this;
    }

    public int getStrength() {
        return strength;
    }

    public MedicineEntity setStrength(int strength) {
        this.strength = strength;
        return this;
    }

    public int getTimes() {
        return times;
    }

    public MedicineEntity setTimes(int times) {
        this.times = times;
        return this;
    }

    public MedicationWithFoodEnum getMedicationWithFood() {
        return medicationWithFood;
    }

    public MedicineEntity setMedicationWithFood(MedicationWithFoodEnum medicationWithFood) {
        this.medicationWithFood = medicationWithFood;
        return this;
    }
}
