package com.noqapp.medical.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.BusinessTypeEnum;
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

    @JsonProperty("cc")
    private String chiefComplain;

    @JsonProperty("ph")
    private String pastHistory;

    @JsonProperty("fh")
    private String familyHistory;

    @JsonProperty("ka")
    private String knownAllergies;

    @JsonProperty("pe")
    private List<JsonMedicalPhysicalExamination> medicalPhysicalExaminations = new ArrayList<>();

    @JsonProperty("cf")
    private String clinicalFinding;

    @JsonProperty("dd")
    private String provisionalDifferentialDiagnosis;

    @JsonProperty("le")
    private List<JsonPathology> pathologies = new ArrayList<>();

    @JsonProperty("pr")
    private String pathologyTestResult;

    @JsonProperty("re")
    private List<JsonMedicalRadiologyXRay> medicalRadiologyXRays = new ArrayList<>();

    @JsonProperty("me")
    private List<JsonMedicine> medicines = new ArrayList<>();

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

    @JsonProperty ("c")
    private String codeQR;

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

    public String getChiefComplain() {
        return chiefComplain;
    }

    public JsonMedicalRecord setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
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

    public List<JsonMedicalPhysicalExamination> getMedicalPhysicalExaminations() {
        return medicalPhysicalExaminations;
    }

    public JsonMedicalRecord setMedicalPhysicalExaminations(List<JsonMedicalPhysicalExamination> medicalPhysicalExaminations) {
        this.medicalPhysicalExaminations = medicalPhysicalExaminations;
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

    public List<JsonPathology> getPathologies() {
        return pathologies;
    }

    public JsonMedicalRecord setPathologies(List<JsonPathology> pathologies) {
        this.pathologies = pathologies;
        return this;
    }

    public String getPathologyTestResult() {
        return pathologyTestResult;
    }

    public JsonMedicalRecord setPathologyTestResult(String pathologyTestResult) {
        this.pathologyTestResult = pathologyTestResult;
        return this;
    }

    public List<JsonMedicalRadiologyXRay> getMedicalRadiologyXRays() {
        return medicalRadiologyXRays;
    }

    public JsonMedicalRecord setMedicalRadiologyXRays(List<JsonMedicalRadiologyXRay> medicalRadiologyXRays) {
        this.medicalRadiologyXRays = medicalRadiologyXRays;
        return this;
    }

    public List<JsonMedicine> getMedicines() {
        return medicines;
    }

    public JsonMedicalRecord setMedicines(List<JsonMedicine> medicines) {
        this.medicines = medicines;
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
}
