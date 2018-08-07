package com.noqapp.domain.types;

/**
 * hitender
 * 3/29/18 3:04 PM
 */
public enum PurchaseOrderStateEnum {
    IN("IN", "Initial"),
    PC("PC", "Price Changed"),
    VB("VB", "Valid Before Purchase"),
    IB("IB", "Invalid Before Purchase"),
    FO("FO", "Failed Order"),
    PO("PO", "Placed Order"),
    NM("NM", "Notified Merchant"),
    OP("OP", "Order being Processed"),
    PR("PR", "Processed"),
    //Based on PurchaseOrder if request pickup or delivery it bifurcates
    RP("RP", "Ready for Pickup"),
    RD("RD", "Ready for Delivery"),
    OW("OW", "On the Way"),
    LO("LO", "Lost"),
    FD("FD", "Failed Delivery"),
    OD("OD", "Order Delivered"),
    DA("DA", "Delivery Re-attempt"),
    CO("CO", "Cancelled Order");

    private final String name;
    private final String description;

    PurchaseOrderStateEnum(String name, String description) {
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
