package com.noqapp.medical.form;

/**
 * hitender
 * 3/8/18 1:18 AM
 */
public class MedicalPhysicalForm {

    private String physicalReferenceId;
    private String name;
    private String value;

    public String getPhysicalReferenceId() {
        return physicalReferenceId;
    }

    public MedicalPhysicalForm setPhysicalReferenceId(String physicalReferenceId) {
        this.physicalReferenceId = physicalReferenceId;
        return this;
    }

    public String getName() {
        return name;
    }

    public MedicalPhysicalForm setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MedicalPhysicalForm setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "MedicalPhysicalForm{" +
                "physicalReferenceId='" + physicalReferenceId + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
