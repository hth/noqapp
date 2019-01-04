package com.noqapp.medical.form;

/**
 * hitender
 * 3/8/18 1:18 AM
 */
public class MedicalPhysicalForm {

    private String pulse;
    private String[] bloodPressure;
    //WT in kg
    private String weight;

    public String getPulse() {
        return pulse;
    }

    public MedicalPhysicalForm setPulse(String pulse) {
        this.pulse = pulse;
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
