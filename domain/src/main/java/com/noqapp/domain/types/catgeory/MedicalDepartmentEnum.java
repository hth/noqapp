package com.noqapp.domain.types.catgeory;

/**
 * hitender
 * 6/25/18 6:38 PM
 */
public enum MedicalDepartmentEnum {
    CRD("CRD", "Cardiologist"),
    CHT("CHT", "Chest Physician"),
    CPY("CPY", "Clinical Psychologist"),
    DNT("DNT", "Dental"),
    DER("DNT", "Dermatologist and Cosmetologist"),
    DIA("DNT", "Diabetologist"),
    DIE("DNT", "Dietitian"),
    ENT("DNT", "E.N.T"),
    GAS("DNT", "Gastroenterology"),
    GPY("GPY", "General Physician"),
    GSR("GSR", "General Surgeon"),
    HOM("DNT", "Homoeopathy"),
    LCS("DNT", "Laproscopic and Colorectal Surgeon"),
    NEP("DNT", "Nephrologist"),
    NEU("DNT", "Neuro Physician"),
    OGY("OGY", "Obstetrician and Gynaecologist"),
    ONC("ONC", "Oncologist"),
    OPT("OPT", "Opthalmologist"),
    ORO("ORO", "Orofacial Pain And TMJ Disorders"),
    ORT("ORT", "Orthopaedic"),
    PAE("PAE", "Paediatrician"),
    PAN("PAN", "Pain Specialist"),
    PNE("PNE", "Pediatric Neurologist"),
    PES("PES", "Pediatric Surgeon"),
    PHY("PHY", "Physiotherapist"),
    PLS("PLS", "Plastic Surgeon"),
    PSY("PSY", "Psychiatrist"),
    RAD("RAD", "Radiologist and Sonologist"),
    RHE("RHE", "Rheumatologist"),
    SPS("SPS", "Spine Surgeon"),
    SPM("SPM", "Sports Medicine"),
    URO("URO", "Urologist");

    private final String description;
    private final String name;

    MedicalDepartmentEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
