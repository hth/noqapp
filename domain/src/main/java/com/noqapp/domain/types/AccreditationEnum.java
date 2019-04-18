package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 2019-04-18 09:50
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public enum AccreditationEnum {
    NABHP("NABHP", "Pre-Accreditation Entry Level Hospital", "nabhp.jpg");

    private final String name;
    private final String description;
    private final String filename;

    AccreditationEnum(String name, String description, String filename) {
        this.name = name;
        this.description = description;
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return description;
    }
}
