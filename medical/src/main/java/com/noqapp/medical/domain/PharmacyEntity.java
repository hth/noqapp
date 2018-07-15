package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.PharmacyMeasurementUnit;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 4/4/18 3:33 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "PHARMACY")
@CompoundIndexes(value = {
        @CompoundIndex(name = "pharmacy_idx", def = "{'NA' : 1}", unique = true),
})
public class PharmacyEntity extends BaseEntity {

    @Field("NA")
    private String name;

    @Field("VA")
    private int value;

    @Field("MU")
    private PharmacyMeasurementUnit pharmacyMeasurementUnit;

    @Field("CN")
    private String companyName;

    //TODO more details to a web page if needed
    @Field("SL")
    private String referStaticLink;

    public String getName() {
        return name;
    }

    public PharmacyEntity setName(String name) {
        this.name = name;
        return this;
    }

    public int getValue() {
        return value;
    }

    public PharmacyEntity setValue(int value) {
        this.value = value;
        return this;
    }

    public PharmacyMeasurementUnit getPharmacyMeasurementUnit() {
        return pharmacyMeasurementUnit;
    }

    public PharmacyEntity setPharmacyMeasurementUnit(PharmacyMeasurementUnit pharmacyMeasurementUnit) {
        this.pharmacyMeasurementUnit = pharmacyMeasurementUnit;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public PharmacyEntity setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getReferStaticLink() {
        return referStaticLink;
    }

    public PharmacyEntity setReferStaticLink(String referStaticLink) {
        this.referStaticLink = referStaticLink;
        return this;
    }
}
