package com.noqapp.domain.types;

/**
 * hitender
 * 7/30/18 5:25 PM
 */
public enum MessageOriginEnum {
    Q,  //Queued        (In App DB)
    QR, //Queue Review  (In App DB)
    O,  //Order         (In App DB)
    OR, //Order Review  (In App DB)
    D,  //Display       (NOT In App DB)
    A   //Alert         (In App DB)
}
