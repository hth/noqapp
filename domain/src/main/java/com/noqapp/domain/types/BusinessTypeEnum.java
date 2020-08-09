package com.noqapp.domain.types;

import static com.noqapp.domain.types.BusinessSupportEnum.OQ;
import static com.noqapp.domain.types.BusinessSupportEnum.OD;
import static com.noqapp.domain.types.BusinessSupportEnum.QQ;
import static com.noqapp.domain.types.MessageOriginEnum.O;
import static com.noqapp.domain.types.MessageOriginEnum.Q;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/23/16 4:29 PM
 */
public enum BusinessTypeEnum {
    RS("RS", "Restaurant", O, "Store", OD),
    RSQ("RSQ", "Restaurant (Queue Only)", Q, "Store", OQ),

    FT("FT", "Food Truck", O, "Store", OD),
    FTQ("FTQ", "Food Truck (Queue Only)", Q, "Store", OQ),

    BA("BA", "Bar", O, "Store", OD),
    BAQ("BAQ", "Bar (Queue Only)", Q, "Store", OQ),

    ST("ST", "Generic Store", O, "Store", OD),
    STQ("STQ", "Generic Store (Queue Online)", Q, "Store", OQ),

    GS("GS", "Grocery Store", O, "Store", OD),
    GSQ("GSQ", "Grocery Store (Queue Only)", Q, "Store", OQ),

    CF("CF", "Cafeteria", O, "Store", OD),
    CFQ("CFQ", "Cafeteria (Queue Online)", Q, "Store", OQ),

    CD("CD", "CSD", O, "Store", OD),
    CDQ("CDQ", "CSD (Queue Online)", Q, "Store", OQ),

    SM("SM", "Shopping Mall", Q, "Queue", QQ),
    MT("MT", "Movie Theater", Q, "Queue", QQ),
    SC("SC", "School", Q, "Queue", QQ),
    DO("DO", "Hospital/Doctor", Q, "Queue", QQ),
    HS("HS", "Health Care Services", O, "Store", OD),
    PH("PH", "Pharmacy", O, "Store", OD),                //Users cannot directly order these, as these have to be prescribed
    PW("PW", "Place of Worship", Q, "Queue", QQ),
    MU("MU", "Museum", Q, "Queue", QQ),
    TA("TA", "Tourist Attraction", Q, "Queue", QQ),
    NC("NC", "Night Club", Q, "Queue", QQ),
    BK("BK", "Bank", Q, "Queue", QQ),
    PA("PA", "Park", Q, "Queue", QQ);

    private final String description;
    private final String name;
    private final MessageOriginEnum messageOrigin;
    private final String classifierTitle;
    private final BusinessSupportEnum businessSupport;

    BusinessTypeEnum(String name, String description, MessageOriginEnum messageOrigin, String classifierTitle, BusinessSupportEnum businessSupport) {
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
        this.businessSupport = businessSupport;
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

    public BusinessSupportEnum getBusinessSupport() {
        return businessSupport;
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

    public static List<BusinessTypeEnum> excludeHospital() {
        List<BusinessTypeEnum> list = Stream.of(BusinessTypeEnum.values()).collect(Collectors.toList());
        list.remove(HS);
        list.remove(DO);
        return list;
    }

    public static List<BusinessTypeEnum> excludeCanteen() {
        List<BusinessTypeEnum> list = Stream.of(BusinessTypeEnum.values()).collect(Collectors.toList());
        list.remove(CD);
        list.remove(CDQ);
        return list;
    }

    public static List<BusinessTypeEnum> excludePlaceOfWorship() {
        List<BusinessTypeEnum> list = Stream.of(BusinessTypeEnum.values()).collect(Collectors.toList());
        list.remove(PW);
        return list;
    }

    @Override
    public String toString() {
        return description;
    }
}
