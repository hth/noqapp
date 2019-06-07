package com.noqapp.domain.types;

/**
 * hitender
 * 7/30/18 5:25 PM
 */
public enum MessageOriginEnum {
    Q,  //Queued                    (NOT In App DB)
    QA, //Queue Appointment         (In App DB)
    QR, //Queue Review              (In App DB)
    O,  //Order                     (NOT In App DB)
    OR, //Order Review              (In App DB)
    D,  //Display                   (NOT In App DB)
    A,  //Alert                     (In App DB)
    MF, //Medical Follow Up         (In App DB)
    CQO //Current Queue And Order   (In App DB)
}
