package com.noqapp.domain.types.medical;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 9/14/19 1:03 AM
 */
public enum MedicalRecordFieldFilterEnum {
    ND("ND", "Self Note or Work Done"),
    NP("NP", "Patient Note or Treatment Plan"),
    CC("CC", "Chief Complain"),
    DI("DI", "Diagnosis"),
    PP("PP", "Plan to Patient");

    private final String description;
    private final String name;

    MedicalRecordFieldFilterEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (MedicalRecordFieldFilterEnum medicalFieldFilter : MedicalRecordFieldFilterEnum.values()) {
            a.add(medicalFieldFilter.description);
        }
        return a;
    }

    public static MedicalRecordFieldFilterEnum byDescription(String description) {
        switch (description) {
            case "Self Note or Work Done":
                return ND;
            case "Patient Note or Treatment Plan":
                return NP;
            case "Chief Complain":
                return CC;
            case "Diagnosis":
                return DI;
            case "Plan to Patient":
                return PP;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
