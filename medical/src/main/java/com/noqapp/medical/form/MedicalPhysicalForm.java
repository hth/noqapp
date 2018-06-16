package com.noqapp.medical.form;

/**
 * hitender
 * 3/8/18 1:18 AM
 */
public class MedicalPhysicalForm {

    private String pluse;
    private String[] bloodPressure;
    //WT in kg
    private String weight;

    public String getPluse() {
        return pluse;
    }

    public MedicalPhysicalForm setPluse(String pluse) {
        this.pluse = pluse;
        return this;
    }

    public String[] getBloodPressure() {
        return bloodPressure;
    }

    public MedicalPhysicalForm setBloodPressure(String[] bloodPressure) {
        this.bloodPressure = bloodPressure;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public MedicalPhysicalForm setWeight(String weight) {
        this.weight = weight;
        return this;
    }
}
