package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

/**
 * Contains General Examination, Systemic Examination, Local Examination.
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

    /** General Physical Exam Starts. */
    @Field("TE")
    private String temperature;

    @Field("PL")
    private String pluse;

    @Field("BP")
    private String[] bloodPressure;

    @Field("OX")
    private String oxygen;

    //WT in kg
    @Field("WT")
    private String weight;

    /** General Physical Exam Ends. */

    public MedicalPhysicalEntity(@NotNull String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getTemperature() {
        return temperature;
    }

    public MedicalPhysicalEntity setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getPluse() {
        return pluse;
    }

    public MedicalPhysicalEntity setPluse(String pluse) {
        this.pluse = pluse;
        return this;
    }

    public String[] getBloodPressure() {
        return bloodPressure;
    }

    public MedicalPhysicalEntity setBloodPressure(String[] bloodPressure) {
        this.bloodPressure = bloodPressure;
        return this;
    }

    public String getOxygen() {
        return oxygen;
    }

    public MedicalPhysicalEntity setOxygen(String oxygen) {
        this.oxygen = oxygen;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public MedicalPhysicalEntity setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public String toString() {
        return "MedicalPhysicalEntity{" +
                "queueUserId='" + queueUserId + '\'' +
                ", pluse='" + pluse + '\'' +
                ", bloodPressure=" + Arrays.toString(bloodPressure) +
                ", weight='" + weight + '\'' +
                '}';
    }
}
