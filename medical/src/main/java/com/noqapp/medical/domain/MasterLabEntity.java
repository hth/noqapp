package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;

import org.apache.commons.lang.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * hitender
 * 11/16/18 12:18 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "MAS_LAB")
@CompoundIndexes(value = {
    @CompoundIndex(name = "mas_lab_idx", def = "{'PN' : 1, 'HS' : 1}", unique = true),
})
public class MasterLabEntity extends BaseEntity {

    @Field("PN")
    private String productName;

    @Field("SN")
    private String productShortName;

    @Field("HS")
    private HealthCareServiceEnum healthCareService;

    @Field("MD")
    private List<MedicalDepartmentEnum> medicalDepartments  = new ArrayList<>();

    @Field("TF")
    private int timesFlagged;

    @Field("FB")
    private List<String> flaggedBy;

    public String getProductName() {
        return productName;
    }

    public MasterLabEntity setProductName(String productName) {
        this.productName = productName;
        if (StringUtils.isBlank(productShortName)) {
            this.productShortName = productName;
        }
        return this;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public MasterLabEntity setProductShortName(String productShortName) {
        this.productShortName = productShortName;
        return this;
    }

    public HealthCareServiceEnum getHealthCareService() {
        return healthCareService;
    }

    public MasterLabEntity setHealthCareService(HealthCareServiceEnum healthCareService) {
        this.healthCareService = healthCareService;
        return this;
    }

    public List<MedicalDepartmentEnum> getMedicalDepartments() {
        return medicalDepartments;
    }

    public MasterLabEntity addMedicalDepartment(MedicalDepartmentEnum medicalDepartment) {
        this.medicalDepartments.add(medicalDepartment);
        return this;
    }

    public MasterLabEntity setMedicalDepartments(List<MedicalDepartmentEnum> medicalDepartments) {
        this.medicalDepartments = medicalDepartments;
        return this;
    }

    public int getTimesFlagged() {
        return timesFlagged;
    }

    public MasterLabEntity setTimesFlagged(int timesFlagged) {
        this.timesFlagged = timesFlagged;
        return this;
    }

    public List<String> getFlaggedBy() {
        return flaggedBy;
    }

    public MasterLabEntity setFlaggedBy(List<String> flaggedBy) {
        this.flaggedBy = flaggedBy;
        return this;
    }

    public MasterLabEntity addFlaggedBy(String qid) {
        if (flaggedBy == null) {
            flaggedBy = new LinkedList<String>() {{
                add(qid);
            }};
        } else {
            flaggedBy.add(qid);
        }

        return this;
    }

    @Transient
    public String toCommaSeparatedString() {
        String colonSeparated = medicalDepartments.stream().map(MedicalDepartmentEnum::name).collect(Collectors.joining(":"));
        return productName + "," + productShortName + "," + healthCareService.name() + "," + colonSeparated;
    }
}
