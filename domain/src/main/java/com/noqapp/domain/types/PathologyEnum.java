package com.noqapp.domain.types;

/**
 * hitender
 * 3/15/18 4:22 PM
 */
public enum PathologyEnum {
    AAAP("AAAP", "Amino Acid Analysis, Plasma"),
    AAAU("AAAU", "Amino Acid Analysis, Urine Random"),
    AAUR("AAUR", "Aminolevulinic Acid, Urine Random"),
    DENI("DENI", "Dengue IgM"),
    BHCG("BHCG", "BETA HCG");

    private final String name;
    private final String description;

    PathologyEnum(String name, String description) {
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
