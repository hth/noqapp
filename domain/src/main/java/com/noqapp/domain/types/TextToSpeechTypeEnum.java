package com.noqapp.domain.types;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 12/16/19 3:25 AM
 */
public enum TextToSpeechTypeEnum {
    SN("SN", "Serving Now", Arrays.asList("{currentlyServing}", "{displayName}", "{goTo}"));

    private final String name;
    private final String description;
    private final List<String> dictionary;

    TextToSpeechTypeEnum(String name, String description, List<String> dictionary) {
        this.name = name;
        this.description = description;
        this.dictionary = dictionary;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDictionary() {
        return dictionary;
    }

    public String getDictionaryAsString() {
        StringBuilder builder = new StringBuilder();
        for (String value : dictionary) {
            builder.append(value).append(", ");
        }
        return builder.toString().substring(0, builder.toString().length() - ", ".length());
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(SN.name, SN.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
