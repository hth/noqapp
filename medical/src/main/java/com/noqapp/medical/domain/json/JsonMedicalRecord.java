package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.medical.FormVersionEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 3/26/18 4:26 PM
 */
@SuppressWarnings ({
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
public class JsonMedicalRecord extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMedicalRecord.class);

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("ph")
    private String pastHistory;

    @JsonProperty("fh")
    private String familyHistory;

    @JsonProperty("ka")
    private String knownAllergies;

    @JsonProperty("pe")
    private JsonMedicalPhysical medicalPhysical;

    @JsonProperty("cc")
    private String chiefComplain;

    @JsonProperty("xm")
    private String examination;

    @JsonProperty("cf")
    private String clinicalFinding;

    @JsonProperty("dd")
    private String provisionalDifferentialDiagnosis;

    @JsonProperty("le")
    private List<JsonMedicalPathology> medicalPathologies = new ArrayList<>();

    @JsonProperty("pr")
    private String pathologyTestResult;

    @JsonProperty("re")
    private List<JsonMedicalRadiology> medicalRadiologies = new ArrayList<>();

    @JsonProperty("me")
    private List<JsonMedicalMedicine> medicalMedicines = new ArrayList<>();

    @JsonProperty("di")
    private String diagnosis;

    @JsonProperty("sf")
    private String storeIdPharmacy;

    @JsonProperty("sr")
    private String storeIdRadiology;

    @JsonProperty("sp")
    private String storeIdPathology;

    @JsonProperty("pp")
    private String planToPatient;

    @JsonProperty("fp")
    private String followUpInDays;

    @JsonProperty("dbi")
    private String diagnosedById;

    @JsonProperty("ra")
    private List<JsonRecordAccess> recordAccess = new ArrayList<>();

    @JsonProperty("n")
    private String businessName;

    /** Convert to Category Name instead of Id. */
    @JsonProperty ("bc")
    private String bizCategoryName;

    @JsonProperty ("u")
    private String createDate;

    @JsonProperty ("qr")
    private String codeQR;

    @JsonProperty("fv")
    private FormVersionEnum formVersion;

    @JsonProperty("rr")
    private String recordReferenceId;

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonMedicalRecord setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonMedicalRecord setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public JsonMedicalRecord setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public JsonMedicalRecord setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public JsonMedicalRecord setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public JsonMedicalPhysical getMedicalPhysical() {
        return medicalPhysical;
    }

    public JsonMedicalRecord setMedicalPhysical(JsonMedicalPhysical medicalPhysical) {
        this.medicalPhysical = medicalPhysical;
        return this;
    }

    public String getChiefComplain() {
        return chiefComplain;
    }

    public JsonMedicalRecord setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
        return this;
    }

    public String getExamination() {
        return examination;
    }

    public JsonMedicalRecord setExamination(String examination) {
        this.examination = examination;
        return this;
    }

    public String getClinicalFinding() {
        return clinicalFinding;
    }

    public JsonMedicalRecord setClinicalFinding(String clinicalFinding) {
        this.clinicalFinding = clinicalFinding;
        return this;
    }

    public String getProvisionalDifferentialDiagnosis() {
        return provisionalDifferentialDiagnosis;
    }

    public JsonMedicalRecord setProvisionalDifferentialDiagnosis(String provisionalDifferentialDiagnosis) {
        this.provisionalDifferentialDiagnosis = provisionalDifferentialDiagnosis;
        return this;
    }

    public List<JsonMedicalPathology> getMedicalPathologies() {
        return medicalPathologies;
    }

    public JsonMedicalRecord setMedicalPathologies(List<JsonMedicalPathology> medicalPathologies) {
        this.medicalPathologies = medicalPathologies;
        return this;
    }

    public JsonMedicalRecord addMedicalPathology(JsonMedicalPathology medicalPathology) {
        this.medicalPathologies.add(medicalPathology);
        return this;
    }

    public String getPathologyTestResult() {
        return pathologyTestResult;
    }

    public JsonMedicalRecord setPathologyTestResult(String pathologyTestResult) {
        this.pathologyTestResult = pathologyTestResult;
        return this;
    }

    public List<JsonMedicalRadiology> getMedicalRadiologies() {
        return medicalRadiologies;
    }

    public JsonMedicalRecord setMedicalRadiologies(List<JsonMedicalRadiology> medicalRadiologies) {
        this.medicalRadiologies = medicalRadiologies;
        return this;
    }

    public JsonMedicalRecord addMedicalRadiology(JsonMedicalRadiology medicalRadiology) {
        this.medicalRadiologies.add(medicalRadiology);
        return this;
    }

    public List<JsonMedicalMedicine> getMedicalMedicines() {
        return medicalMedicines;
    }

    public JsonMedicalRecord setMedicalMedicines(List<JsonMedicalMedicine> medicalMedicines) {
        this.medicalMedicines = medicalMedicines;
        return this;
    }

    public JsonMedicalRecord addMedicine(JsonMedicalMedicine medicine) {
        this.medicalMedicines.add(medicine);
        return this;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public JsonMedicalRecord setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public String getStoreIdPharmacy() {
        return storeIdPharmacy;
    }

    public JsonMedicalRecord setStoreIdPharmacy(String storeIdPharmacy) {
        this.storeIdPharmacy = storeIdPharmacy;
        return this;
    }

    public String getStoreIdRadiology() {
        return storeIdRadiology;
    }

    public JsonMedicalRecord setStoreIdRadiology(String storeIdRadiology) {
        this.storeIdRadiology = storeIdRadiology;
        return this;
    }

    public String getStoreIdPathology() {
        return storeIdPathology;
    }

    public JsonMedicalRecord setStoreIdPathology(String storeIdPathology) {
        this.storeIdPathology = storeIdPathology;
        return this;
    }

    public String getPlanToPatient() {
        return planToPatient;
    }

    public JsonMedicalRecord setPlanToPatient(String planToPatient) {
        this.planToPatient = planToPatient;
        return this;
    }

    public String getFollowUpInDays() {
        return followUpInDays;
    }

    public JsonMedicalRecord setFollowUpInDays(String followUpInDays) {
        this.followUpInDays = followUpInDays;
        return this;
    }

    public String getDiagnosedById() {
        return diagnosedById;
    }

    public JsonMedicalRecord setDiagnosedById(String diagnosedById) {
        this.diagnosedById = diagnosedById;
        return this;
    }

    public List<JsonRecordAccess> getRecordAccess() {
        return recordAccess;
    }

    public JsonMedicalRecord setRecordAccess(List<JsonRecordAccess> recordAccess) {
        this.recordAccess = recordAccess;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonMedicalRecord setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBizCategoryName() {
        return bizCategoryName;
    }

    public JsonMedicalRecord setBizCategoryName(String bizCategoryName) {
        this.bizCategoryName = bizCategoryName;
        return this;
    }

    public String getCreateDate() {
        return createDate;
    }

    public JsonMedicalRecord setCreateDate(String createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonMedicalRecord setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public FormVersionEnum getFormVersion() {
        return formVersion;
    }

    public JsonMedicalRecord setFormVersion(FormVersionEnum formVersion) {
        this.formVersion = formVersion;
        return this;
    }

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public JsonMedicalRecord setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
        return this;
    }
}
