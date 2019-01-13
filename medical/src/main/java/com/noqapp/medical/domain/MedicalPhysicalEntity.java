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
    private String pulse;

    @Field("BP")
    private String[] bloodPressure;

    @Field("OX")
    private String oxygen;

    @Field("RP")
    private String respiratory;

    //WT in kg
    @Field("WT")
    private String weight;

    //Height in cms
    @Field("HT")
    private String height;
    /** General Physical Exam Ends. */

    @NotNull
    @Field("DBI")
    private String diagnosedById;

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

    public String getPulse() {
        return pulse;
    }

    public MedicalPhysicalEntity setPulse(String pulse) {
        this.pulse = pulse;
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

    public String getRespiratory() {
        return respiratory;
    }

    public MedicalPhysicalEntity setRespiratory(String respiratory) {
        this.respiratory = respiratory;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public MedicalPhysicalEntity setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public MedicalPhysicalEntity setHeight(String height) {
        this.height = height;
        return this;
    }

    public String getDiagnosedById() {
        return diagnosedById;
    }

    public MedicalPhysicalEntity setDiagnosedById(String diagnosedById) {
        this.diagnosedById = diagnosedById;
        return this;
    }

    @Override
    public String toString() {
        return "MedicalPhysicalEntity{" +
                "queueUserId='" + queueUserId + '\'' +
                ", pulse='" + pulse + '\'' +
                ", bloodPressure=" + Arrays.toString(bloodPressure) +
                ", weight='" + weight + '\'' +
                '}';
    }
}
