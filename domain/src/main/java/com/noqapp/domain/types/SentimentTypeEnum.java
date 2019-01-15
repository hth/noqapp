package com.noqapp.domain.types;

/**
 * hitender
 * 2019-01-14 22:51
 */
public enum SentimentTypeEnum {
    P("P", "Positive", 1),
    N("N", "Negative", -1),
    B("B", "Neutral", 0); //Balanced

    private final String name;
    private final String description;
    private final int value;

    SentimentTypeEnum(String name, String description, int value) {
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

    public int getValue() {
        return value;
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
