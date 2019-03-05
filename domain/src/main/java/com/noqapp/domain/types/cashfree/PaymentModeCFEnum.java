package com.noqapp.domain.types.cashfree;

/**
 * hitender
 * 2019-03-05 10:27
 */
public enum PaymentModeCFEnum {
    DEBIT_CARD("DEBIT_CARD", "Debit Card"),
    CREDIT_CARD("CREDIT_CARD", "Credit Card"),
    CREDIT_CARD_EMI("CREDIT_CARD_EMI", "Credit Card EMI"),
    NET_BANKING("NET_BANKING", "Internet Banking"),
    UPI("UPI", "UPI"),
    Paypal("Paypal", "Paypal"),
    PhonePe("PhonePe", "PhonePe"),
    Paytm("Paytm", "Paytm"),
    AmazonPay("AmazonPay", "AmazonPay"),
    AIRTEL_MONEY("AIRTEL_MONEY", "Airtel Money Wallet"),
    FreeCharge("FreeCharge", "Freecharge Wallet\n"),
    MobiKwik("MobiKwik", "MobiKwik Wallet"),
    OLA("OLA", "Ola Wallet"),
    JioMoney("JioMoney", "JioMoney Wallet"),
    ZestMoney("ZestMoney", "ZestMoney"),
    Instacred("Instacred", "AmazonPay"),
    LazyPay("LazyPay", "LazyPay");

    private final String name;
    private final String description;

    PaymentModeCFEnum(String name, String description) {
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
