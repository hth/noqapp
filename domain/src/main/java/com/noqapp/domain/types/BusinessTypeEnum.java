package com.noqapp.domain.types;

import static com.noqapp.domain.types.BusinessSupportEnum.MP;
import static com.noqapp.domain.types.BusinessSupportEnum.OD;
import static com.noqapp.domain.types.BusinessSupportEnum.OQ;
import static com.noqapp.domain.types.BusinessSupportEnum.QQ;
import static com.noqapp.domain.types.MessageOriginEnum.M;
import static com.noqapp.domain.types.MessageOriginEnum.O;
import static com.noqapp.domain.types.MessageOriginEnum.Q;
import static com.noqapp.domain.types.TransactionCancelEnum.HTA;
import static com.noqapp.domain.types.TransactionCancelEnum.MEA;
import static com.noqapp.domain.types.TransactionCancelEnum.TMA;
import static com.noqapp.domain.types.TransactionCancelEnum.TNS;

import java.util.ArrayList;
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
    RS("RS", "Restaurant", O, "Store", OD, TMA),
    RSQ("RSQ", "Restaurant (Queue Only)", Q, "Store", OQ, TNS),

    FT("FT", "Food Truck", O, "Store", OD, TMA),
    FTQ("FTQ", "Food Truck (Queue Only)", Q, "Store", OQ, TNS),

    BA("BA", "Bar", O, "Store", OD, TMA),
    BAQ("BAQ", "Bar (Queue Only)", Q, "Store", OQ, TNS),

    ST("ST", "Generic Store", O, "Store", OD, MEA),
    STQ("STQ", "Generic Store (Queue Online)", Q, "Store", OQ, TNS),

    GS("GS", "Grocery Store", O, "Store", OD, MEA),
    GSQ("GSQ", "Grocery Store (Queue Only)", Q, "Store", OQ, TNS),

    CF("CF", "Cafeteria", O, "Store", OD, TMA),
    CFQ("CFQ", "Cafeteria (Queue Online)", Q, "Store", OQ, TNS),

    CD("CD", "CSD", O, "Store", OD, MEA),
    CDQ("CDQ", "CSD (Queue Online)", Q, "Store", OQ, TNS),

    SM("SM", "Shopping Mall", Q, "Queue", QQ, TNS),
    MT("MT", "Movie Theater", Q, "Queue", QQ, TNS),
    SC("SC", "School", Q, "Queue", QQ, TNS),

    //For health service
    DO("DO", "Hospital/Doctor", Q, "Queue", QQ, HTA),
    HS("HS", "Health Care Services", O, "Store", OD, HTA),
    //Users cannot directly order these, as these have to be prescribed
    PH("PH", "Pharmacy", O, "Store", OD, HTA),

    //To be decided on supported TransactionCancel for all the below condition
    PW("PW", "Place of Worship", Q, "Queue", QQ, TNS),
    MU("MU", "Museum", Q, "Queue", QQ, TNS),
    TA("TA", "Tourist Attraction", Q, "Queue", QQ, TNS),
    NC("NC", "Night Club", Q, "Queue", QQ, TNS),
    BK("BK", "Bank", Q, "Queue", QQ, TNS),
    PA("PA", "Park", Q, "Queue", QQ, TNS),

    //For Marketplace
    PR("PR", "Property Rental", M, "Marketplace", MP, TNS),

    //For All, to support when sending to all
    ZZ("ZZ", "Global", Q, "Queue", QQ, TNS);

    private final String description;
    private final String name;
    private final MessageOriginEnum messageOrigin;
    private final String classifierTitle;
    private final BusinessSupportEnum businessSupport;
    private final TransactionCancelEnum transactionCancel;

    BusinessTypeEnum(
        String name,
        String description,
        MessageOriginEnum messageOrigin,
        String classifierTitle,
        BusinessSupportEnum businessSupport,
        TransactionCancelEnum transactionCancel
    ) {
        this.name = name;
        this.description = description;
        switch (messageOrigin) {
            case O:
            case Q:
            case M:
                this.messageOrigin = messageOrigin;
                break;
            default:
                throw new UnsupportedOperationException("Reached unsupported condition " + messageOrigin);
        }
        this.classifierTitle = classifierTitle;
        this.businessSupport = businessSupport;
        this.transactionCancel = transactionCancel;
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

    public TransactionCancelEnum getTransactionCancel() {
        return transactionCancel;
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

    public static List<BusinessTypeEnum> marketPlaces() {
        return new ArrayList<>() {{
            add(PR);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
