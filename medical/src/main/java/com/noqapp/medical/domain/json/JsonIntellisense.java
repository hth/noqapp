package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Helps parse Professional Profile data dictionary.
 * hitender
 * 6/26/18 3:16 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonIntellisense extends AbstractDomain {

    @JsonProperty("provisional_diagnosis")
    private List<String> provisionalDiagnosis;

    @JsonProperty("clinical_findings")
    private List<String> clinicalFindings;

    @JsonProperty("known_allergies")
    private List<String> knownAllergies;

    @JsonProperty("medicines_treatment_advice")
    private List<String> medicinesTreatmentAdvice;

    @JsonProperty("chief_complaint")
    private List<String> chiefComplaint;

    @JsonProperty("investigation")
    private List<String> investigation;

    @JsonProperty("past_history")
    private List<String> pastHistory;

    @JsonProperty("family_history")
    private List<String> familyHistory;

    public List<String> getProvisionalDiagnosis() {
        return provisionalDiagnosis;
    }

    public void setProvisionalDiagnosis(List<String> provisionalDiagnosis) {
        this.provisionalDiagnosis = provisionalDiagnosis;
    }

    public List<String> getClinicalFindings() {
        return clinicalFindings;
    }

    public void setClinicalFindings(List<String> clinicalFindings) {
        this.clinicalFindings = clinicalFindings;
    }

    public List<String> getKnownAllergies() {
        return knownAllergies;
    }

    public void setKnownAllergies(List<String> knownAllergies) {
        this.knownAllergies = knownAllergies;
    }

    public List<String> getMedicinesTreatmentAdvice() {
        return medicinesTreatmentAdvice;
    }

    public void setMedicinesTreatmentAdvice(List<String> medicinesTreatmentAdvice) {
        this.medicinesTreatmentAdvice = medicinesTreatmentAdvice;
    }

    public List<String> getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(List<String> chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public List<String> getInvestigation() {
        return investigation;
    }

    public void setInvestigation(List<String> investigation) {
        this.investigation = investigation;
    }

    public List<String> getPastHistory() {
        return pastHistory;
    }

    public void setPastHistory(List<String> pastHistory) {
        this.pastHistory = pastHistory;
    }

    public List<String> getFamilyHistory() {
        return familyHistory;
    }

    public void setFamilyHistory(List<String> familyHistory) {
        this.familyHistory = familyHistory;
    }
}