package com.noqapp.domain.types;

import java.util.Date;

/**
 * hitender
 * 10/30/20 1:30 PM
 */
public enum ServiceTimeChangeEnum {
    PU, //Pushed Up
    PD, //Pushed Down
    NC; //No Change

    public static ServiceTimeChangeEnum compare(Date date1, Date date2) {
        if (date1.compareTo(date2) > 0) {
            //Date1 is after Date2;
            return PD;
        } else if (date1.compareTo(date2) < 0) {
            //Date1 is before Date2
            return PU;
        } else {
            //Date1 is equal to Date2
            return NC;
        }
    }
}
