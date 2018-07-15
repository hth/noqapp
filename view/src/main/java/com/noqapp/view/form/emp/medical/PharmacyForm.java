package com.noqapp.view.form.emp.medical;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.PharmacyMeasurementUnit;
import com.noqapp.medical.domain.PharmacyEntity;

import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * hitender
 * 4/7/18 7:19 PM
 */
public class PharmacyForm {
    //When editing
    private ScrubbedInput id;

    private ScrubbedInput name;
    private int value;
    private PharmacyMeasurementUnit pharmacyMeasurementUnit;
    private ScrubbedInput companyName;
    //TODO more details to a web page if needed
    private ScrubbedInput referStaticLink;

    private List<PharmacyEntity> pharmacies;

    @Transient
    private List<PharmacyMeasurementUnit> availablePharmacyMeasurementUnit;

    public PharmacyForm() {
        availablePharmacyMeasurementUnit = PharmacyMeasurementUnit.asList();
    }

    public ScrubbedInput getId() {
        return id;
    }

    public PharmacyForm setId(ScrubbedInput id) {
        this.id = id;
        return this;
    }

    public ScrubbedInput getName() {
        return name;
    }

    public PharmacyForm setName(ScrubbedInput name) {
        this.name = name;
        return this;
    }

    public int getValue() {
        return value;
    }

    public PharmacyForm setValue(int value) {
        this.value = value;
        return this;
    }

    public PharmacyMeasurementUnit getPharmacyMeasurementUnit() {
        return pharmacyMeasurementUnit;
    }

    public PharmacyForm setPharmacyMeasurementUnit(PharmacyMeasurementUnit pharmacyMeasurementUnit) {
        this.pharmacyMeasurementUnit = pharmacyMeasurementUnit;
        return this;
    }

    public ScrubbedInput getCompanyName() {
        return companyName;
    }

    public PharmacyForm setCompanyName(ScrubbedInput companyName) {
        this.companyName = companyName;
        return this;
    }

    public ScrubbedInput getReferStaticLink() {
        return referStaticLink;
    }

    public PharmacyForm setReferStaticLink(ScrubbedInput referStaticLink) {
        this.referStaticLink = referStaticLink;
        return this;
    }

    public List<PharmacyEntity> getPharmacies() {
        return pharmacies;
    }

    public PharmacyForm setPharmacies(List<PharmacyEntity> pharmacies) {
        this.pharmacies = pharmacies;
        return this;
    }

    public List<PharmacyMeasurementUnit> getAvailablePharmacyMeasurementUnit() {
        return availablePharmacyMeasurementUnit;
    }
}
