package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.PathologyEnum;
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
@Document(collection = "M_PAT")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_pat_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalPathologyEntity extends BaseEntity {
    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("PA")
    private PathologyEnum pathology;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalPathologyEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public PathologyEnum getPathology() {
        return pathology;
    }

    public MedicalPathologyEntity setPathology(PathologyEnum pathology) {
        this.pathology = pathology;
        return this;
    }
}
