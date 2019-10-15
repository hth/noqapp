package com.noqapp.domain.types;

import java.util.EnumSet;

/**
 * hitender
 * 3/29/18 3:04 PM
 */
public enum PurchaseOrderStateEnum {
    IN("IN", "Initial", "Initial"),
    PC("PC", "Price Changed", "Price Changed"),
    VB("VB", "Valid Before Purchase", "Order Received"),
    IB("IB", "Invalid Before Purchase", "Invalid Before Purchase"),
    FO("FO", "Failed Order", "Failed Order"),
    PO("PO", "Placed Order", "Placed Order"),
    NM("NM", "Notified Merchant", "Notified Merchant"),
    OP("OP", "Order being Processed", "Order being Processed"),
    PR("PR", "Processed", "Processed"),
    //Based on PurchaseOrder if request pickup or delivery it bifurcates
    RP("RP", "Ready for Pickup", "Ready for Pickup"),
    RD("RD", "Ready for Delivery", "Ready for Delivery"),
    OW("OW", "On the Way", "On the Way"),
    LO("LO", "Lost", "Lost"),
    FD("FD", "Failed Delivery", "Failed Delivery"),
    DA("DA", "Delivery Re-attempt", "Delivery Re-attempt"),
    OD("OD", "Order Delivered", "Order Delivered"),
    CO("CO", "Cancelled Order", "Cancelled Order");

    private final String name;
    private final String description;
    private final String friendlyDescription;

    PurchaseOrderStateEnum(String name, String description, String friendlyDescription) {
        this.name = name;
        this.description = description;
        this.friendlyDescription = friendlyDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFriendlyDescription() {
        return friendlyDescription;
    }

    //Home Delivery
    public static EnumSet<PurchaseOrderStateEnum> HD = EnumSet.of(PO, OP, PR, RP);

    //To Go /Takeaway Mode (Either one of the choice LO, FD, DA, OD)
    public static EnumSet<PurchaseOrderStateEnum> TO = EnumSet.of(PO, OP, RD, OW, LO, FD, DA, OD);

    @Override
    public String toString() {
        return description;
    }
}
