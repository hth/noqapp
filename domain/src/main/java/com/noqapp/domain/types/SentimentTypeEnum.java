package com.noqapp.domain.types;

/**
 * hitender
 * 2019-01-14 22:51
 */
public enum SentimentTypeEnum {
    P("P", "Positive"),
    N("N", "Negative"),
    B("B", "Neutral"); //Balanced

    private final String name;
    private final String description;

    SentimentTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static SentimentTypeEnum byDescription(String description) {
        switch (description) {
            case "Positive":
                return P;
            case "Negative":
                return N;
            case "Neutral":
                return B;
            default:
                throw new UnsupportedOperationException("Un-supported action. Contact Support");
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
