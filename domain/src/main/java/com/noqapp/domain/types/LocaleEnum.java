package com.noqapp.domain.types;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * hitender
 * 8/21/20 1:02 PM
 */
public enum LocaleEnum {
    en_IN("en_IN", "English"),
    hi_IN("hi_IN", "Hindi"),
    kn_IN("kn_IN", "Kannada"),
    pa_IN("pa_IN", "Punjabi");

    private final String name;
    private final String description;

    LocaleEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(en_IN.name, en_IN.description);
            put(hi_IN.name, hi_IN.description);
            put(pa_IN.name, pa_IN.description);
            put(kn_IN.name, kn_IN.description);
        }};
    }

    public static List<LocaleEnum> asList() {
        return Stream.of(LocaleEnum.values())
            .sorted(Comparator.comparing(LocaleEnum::getDescription))
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return description;
    }
}
