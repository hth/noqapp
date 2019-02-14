package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.medical.LabCategoryEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

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

    @Field("LC")
    private LabCategoryEnum labCategory;

    @Field("RD")
    private List<String> medicalRadiologyXRayIds = new LinkedList<>();

    /** Image resides inside this record. */
    @Field("IM")
    private List<String> images;

    @Field("TI")
    private String transactionId;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalRadiologyEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public LabCategoryEnum getLabCategory() {
        return labCategory;
    }

    public MedicalRadiologyEntity setLabCategory(LabCategoryEnum labCategory) {
        this.labCategory = labCategory;
        return this;
    }

    public List<String> getMedicalRadiologyXRayIds() {
        return medicalRadiologyXRayIds;
    }

    public MedicalRadiologyEntity setMedicalRadiologyXRayIds(List<String> medicalRadiologyXRayIds) {
        this.medicalRadiologyXRayIds = medicalRadiologyXRayIds;
        return this;
    }

    public MedicalRadiologyEntity addMedicalRadiologyXRayIds(String medicalRadiologyXRayId) {
        this.medicalRadiologyXRayIds.add(medicalRadiologyXRayId);
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public MedicalRadiologyEntity setImages(List<String> images) {
        this.images = images;
        return this;
    }

    public MedicalRadiologyEntity addImage(String image) {
        if (images == null) {
            images = new LinkedList<>();
        }
        this.images.add(image);
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public MedicalRadiologyEntity setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
}
