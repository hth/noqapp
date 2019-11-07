package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 10/20/19 2:11 AM
 */
public enum QuestionTypeEnum {
    B("B", "Boolean"),
    S("S", "Single Choice"),
    M("M", "Multiple Choice"),
    R("R", "Rating"),
    T("T", "Free Text");

    private final String name;
    private final String description;

    QuestionTypeEnum(String name, String description) {
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
            put(B.name, B.description);
            put(S.name, S.description);
            put(M.name, M.description);
            put(R.name, R.description);
            put(T.name, T.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
