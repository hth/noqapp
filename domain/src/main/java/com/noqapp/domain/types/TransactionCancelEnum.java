package com.noqapp.domain.types;

/**
 * hitender
 * 10/18/20 7:32 PM
 */
public enum TransactionCancelEnum {
    /* Hospital, Pharmacy, Health Services. */
    HTA("HTA", "Health Time Action"),

    /* Restaurant or Food Truck */
    TMA("TMA", "Time Merchant Action"),

    /* Grocery */
    MEA("MEA", "Merchant Action"),

    /* For just queues */
    TNS("TNS", "Transaction Not Supported");

    private final String name;
    private final String description;

    TransactionCancelEnum(String name, String description) {
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
