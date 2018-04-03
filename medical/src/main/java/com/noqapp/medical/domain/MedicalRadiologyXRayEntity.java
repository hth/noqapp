package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.RadiologyEnum;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 3/15/18 6:04 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_RDO_X")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_rdo_x_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalRadiologyXRayEntity extends BaseEntity {
    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field ("RE")
    private RadiologyEnum radiology;

    @Field("TR")
    private String testResult;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalRadiologyXRayEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public RadiologyEnum getRadiology() {
        return radiology;
    }

    public MedicalRadiologyXRayEntity setRadiology(RadiologyEnum radiology) {
        this.radiology = radiology;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public MedicalRadiologyXRayEntity setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
