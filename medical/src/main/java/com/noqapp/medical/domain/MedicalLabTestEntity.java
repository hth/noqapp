package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 2/25/18 3:37 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_LAB_T")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_lab_t_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalLabTestEntity extends BaseEntity {
    @NotNull
    @Field("QID")
    private String queueUserId;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalLabTestEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }
}
