package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 3/15/18 6:02 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_RADIOLOGY")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_radiology_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalRadiologyEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("RD")
    private List<String> medicalRadiologyXRayIds = new LinkedList<>();

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalRadiologyEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public List<String> getMedicalRadiologyXRayIds() {
        return medicalRadiologyXRayIds;
    }

    public MedicalRadiologyEntity setMedicalRadiologyXRayIds(List<String> medicalRadiologyXRayIds) {
        this.medicalRadiologyXRayIds = medicalRadiologyXRayIds;
        return this;
    }
}
