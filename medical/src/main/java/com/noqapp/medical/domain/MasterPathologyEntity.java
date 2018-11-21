package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

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
@Document(collection = "MAS_PATHOLOGY")
@CompoundIndexes(value = {
    @CompoundIndex(name = "mas_pathology_idx", def = "{'PN' : 1}", unique = true),
})
public class MasterPathologyEntity extends BaseEntity {

    @Field("PN")
    private String productName;

    @Field("MD")
    private List<MedicalDepartmentEnum> medicalDepartments = new ArrayList<>();

    public String getProductName() {
        return productName;
    }

    public MasterPathologyEntity setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public List<MedicalDepartmentEnum> getMedicalDepartments() {
        return medicalDepartments;
    }

    public MasterPathologyEntity addMedicalDepartment(MedicalDepartmentEnum medicalDepartment) {
        this.medicalDepartments.add(medicalDepartment);
        return this;
    }

    public MasterPathologyEntity setMedicalDepartments(List<MedicalDepartmentEnum> medicalDepartments) {
        this.medicalDepartments = medicalDepartments;
        return this;
    }

    @Transient
    public String toCommaSeparatedString() {
        return productName + ",";
    }
}
