package com.noqapp.domain.types;

import static com.noqapp.domain.types.QueueOrderTypeEnum.*;

import java.util.Arrays;
import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 4:29 PM
 */
public enum BusinessTypeEnum {
    RS("RS", "Restaurant", O),
    BA("BA", "Bar", O),
    ST("ST", "Store", O),
    SM("SM", "Shopping Mall", Q),
    MT("MT", "Movie Theater", Q),
    SC("SC", "School", Q),
    GS("GS", "Grocery Store", O),
    CF("CF", "Cafe", O),
    DO("DO", "Hospital/Doctor", Q),
    PH("PH", "Pharmacy", O),
    PW("PW", "Place of Worship", Q),
    MU("MU", "Museum", Q),
    TA("TA", "Tourist Attraction", Q),
    NC("NC", "Night Club", Q),
    BK("BK", "Bank", Q),
    PA("PA", "Park", Q);

    private final String description;
    private final String name;
    private final QueueOrderTypeEnum queueOrderType;

    BusinessTypeEnum(String name, String description, QueueOrderTypeEnum queueOrderType) {
        this.name = name;
        this.description = description;
        this.queueOrderType = queueOrderType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public QueueOrderTypeEnum getQueueOrderType() {
        return queueOrderType;
    }

    public static List<BusinessTypeEnum> asList() {
        BusinessTypeEnum[] all = BusinessTypeEnum.values();
        return Arrays.asList(all);
    }

    @Override
    public String toString() {
        return description;
    }
}