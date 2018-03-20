package com.noqapp.medical.form;

import com.noqapp.domain.types.PhysicalExamEnum;

/**
 * hitender
 * 3/8/18 1:18 AM
 */
public class MedicalPhysicalForm {

    private PhysicalExamEnum physicalExam;
    private String value;

    public PhysicalExamEnum getPhysicalExam() {
        return physicalExam;
    }

    public MedicalPhysicalForm setPhysicalExam(PhysicalExamEnum physicalExam) {
        this.physicalExam = physicalExam;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MedicalPhysicalForm setValue(String value) {
        this.value = value;
        return this;
    }
}
