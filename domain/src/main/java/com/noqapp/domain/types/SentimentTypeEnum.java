package com.noqapp.domain.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 2019-01-14 22:51
 */
public enum SentimentTypeEnum {
    P("P", "Positive", 1),
    N("N", "Negative", -1),
    B("B", "Neutral", 0); //Balanced

    private static final Logger LOG = LoggerFactory.getLogger(SentimentTypeEnum.class);

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
            case "Very positive":
                return P;
            case "Negative":
            case "Very negative":
                return N;
            case "Neutral":
                return B;
            default:
                LOG.error("Failed as sentimentType={} not defined", description);
                throw new UnsupportedOperationException("Un-supported action. Contact Support");
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
