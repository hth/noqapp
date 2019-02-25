package com.noqapp.domain.types;

import static com.noqapp.domain.types.MessageOriginEnum.O;
import static com.noqapp.domain.types.MessageOriginEnum.Q;

import java.util.Arrays;
import java.util.EnumSet;
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
    HS("HS", "Health Care Services", O),
    PH("PH", "Pharmacy", O),                //Users cannot directly order these, as these have to be prescribed
    PW("PW", "Place of Worship", Q),
    MU("MU", "Museum", Q),
    TA("TA", "Tourist Attraction", Q),
    NC("NC", "Night Club", Q),
    BK("BK", "Bank", Q),
    PA("PA", "Park", Q);

    public static EnumSet<BusinessTypeEnum> ORDERS = EnumSet.of(RS, BA, ST, GS, CF, HS, PH);
    public static EnumSet<BusinessTypeEnum> QUEUES = EnumSet.of(SM, MT, SC, DO, PW, MU, TA, NC, BK, PA);

    private final String description;
    private final String name;
    private final MessageOriginEnum messageOrigin;

    BusinessTypeEnum(String name, String description, MessageOriginEnum messageOrigin) {
        this.name = name;
        this.description = description;
        this.messageOrigin = messageOrigin;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
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