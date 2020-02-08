package com.noqapp.domain.types;

/**
 * hitender
 * 3/27/18 10:03 AM
 */
public enum PaymentModeEnum {
    CA("CA", "Cash"),
    CQ("CQ", "Cheque"),
    DC("DC", "Debit Card"),
    CC("CC", "Credit Card"),
    CCE("CCE", "Credit Card EMI"),
    NTB("NTB", "Internet Banking"),
    UPI("UPI", "UPI"),
    PAL("PAL", "Paypal"),
    PPE("PPE", "PhonePe"),
    PTM("PTM", "Paytm"),
    AMZ("AMZ", "AmazonPay"),
    AIR("AIR", "Airtel Money Wallet"),
    FCH("FCH", "Freecharge Wallet"),
    MKK("MKK", "MobiKwik Wallet"),
    OLA("OLA", "Ola Wallet"),
    OMP("OMP", "Ola Money Post Paid"),
    JIO("JIO", "JioMoney Wallet"),
    ZST("ZST", "ZestMoney"),
    INS("INS", "AmazonPay"),
    LPY("LPY", "LazyPay");

    private final String description;
    private final String name;

    PaymentModeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
