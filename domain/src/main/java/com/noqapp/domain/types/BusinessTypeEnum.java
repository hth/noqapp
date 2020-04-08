package com.noqapp.domain.types;

import static com.noqapp.domain.types.MessageOriginEnum.O;
import static com.noqapp.domain.types.MessageOriginEnum.Q;
import static java.util.stream.Collectors.toMap;

import com.noqapp.domain.types.catgeory.GroceryEnum;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/23/16 4:29 PM
 */
public enum BusinessTypeEnum {
    RS("RS", "Restaurant", O, "Store"),
    FT("FT", "Food Truck", O, "Store"),
    BA("BA", "Bar", O, "Store"),
    ST("ST", "Store", O, "Store"),
    SM("SM", "Shopping Mall", Q, "Queue"),
    MT("MT", "Movie Theater", Q, "Queue"),
    SC("SC", "School", Q, "Queue"),
    GS("GS", "Grocery Store", O, "Store"),
    CF("CF", "Cafe", O, "Store"),
    DO("DO", "Hospital/Doctor", Q, "Queue"),
    HS("HS", "Health Care Services", O, "Store"),
    PH("PH", "Pharmacy", O, "Store"),                //Users cannot directly order these, as these have to be prescribed
    PW("PW", "Place of Worship", Q, "Queue"),
    MU("MU", "Museum", Q, "Queue"),
    TA("TA", "Tourist Attraction", Q, "Queue"),
    NC("NC", "Night Club", Q, "Queue"),
    BK("BK", "Bank", Q, "Queue"),
    PA("PA", "Park", Q, "Queue");

    private final String description;
    private final String name;
    private final MessageOriginEnum messageOrigin;
    private final String classifierTitle;

    BusinessTypeEnum(String name, String description, MessageOriginEnum messageOrigin, String classifierTitle) {
        this.name = name;
        this.description = description;
        switch (messageOrigin) {
            case O:
            case Q:
                this.messageOrigin = messageOrigin;
                break;
            default:
                throw new UnsupportedOperationException("Reached unsupported condition " + messageOrigin);
        }
        this.classifierTitle = classifierTitle;
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

    public String getClassifierTitle() {
        return classifierTitle;
    }

    public static List<BusinessTypeEnum> asList() {
        return Stream.of(BusinessTypeEnum.values())
            .sorted(Comparator.comparing(BusinessTypeEnum::getDescription))
            .collect(Collectors.toList());
    }

    /** For dynamically creating list of business type based on message origin. */
    public static EnumSet<BusinessTypeEnum> getSelectedMessageOrigin(MessageOriginEnum messageOrigin) {
        EnumSet<BusinessTypeEnum> businessTypeEnums = EnumSet.noneOf(BusinessTypeEnum.class);

        for (BusinessTypeEnum businessType : BusinessTypeEnum.values()) {
            if (messageOrigin == businessType.messageOrigin) {
                businessTypeEnums.add(businessType);
            }
        }

        return businessTypeEnums;
    }

    @Override
    public String toString() {
        return description;
    }
}
