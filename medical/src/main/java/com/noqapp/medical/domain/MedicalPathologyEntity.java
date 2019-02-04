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
@Document(collection = "M_PATHOLOGY")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_pathology_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalPathologyEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("PD")
    private List<String> medicalPathologyTestIds = new LinkedList<>();

    @Field("IM")
    private List<String> images;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalPathologyEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public List<String> getMedicalPathologyTestIds() {
        return medicalPathologyTestIds;
    }

    public MedicalPathologyEntity setMedicalPathologyTestIds(List<String> medicalPathologyTestIds) {
        this.medicalPathologyTestIds = medicalPathologyTestIds;
        return this;
    }

    public MedicalPathologyEntity addMedicalPathologyTestId(String medicalPathologyTestId) {
        this.medicalPathologyTestIds.add(medicalPathologyTestId);
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public MedicalPathologyEntity setImages(List<String> images) {
        this.images = images;
        return this;
    }
}
