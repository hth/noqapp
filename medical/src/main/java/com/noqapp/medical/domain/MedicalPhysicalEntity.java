package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

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

    @Field("PL")
    private String pluse;

    @Field("BP")
    private String[] bloodPressure;

    //WT in kg
    @Field("WT")
    private String weight;

    public MedicalPhysicalEntity(@NotNull String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getQueueUserId() {
        return queueUserId;
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
