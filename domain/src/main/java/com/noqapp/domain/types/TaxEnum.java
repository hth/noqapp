package com.noqapp.domain.types;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hitender
 * 10/8/20 11:03 AM
 */
public enum TaxEnum {
    ZE("ZE", "0%", new BigDecimal(0)),
    PT("PT", "0.25%", new BigDecimal(25).movePointLeft(2)),
    TH("TH", "3%", new BigDecimal(3)),
    FI("FI", "5%", new BigDecimal(5)),
    TW("TW", "12%", new BigDecimal(12)),
    ET("ET", "18%", new BigDecimal(18)),
    TE("TE", "28%", new BigDecimal(28));

    private final String name;
    private final String description;
    private final BigDecimal value;

    TaxEnum(String name, String description, BigDecimal value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public static Map<String, BigDecimal> asMapWithNameAsKey() {
        return new LinkedHashMap<String, BigDecimal>() {{
            put(ZE.name, ZE.value);
            put(PT.name, PT.value);
            put(TH.name, TH.value);
            put(FI.name, FI.value);
            put(TW.name, TW.value);
            put(ET.name, ET.value);
            put(TE.name, TE.value);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
