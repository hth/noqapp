package com.noqapp.domain.helper;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * hitender
 * 5/30/18 11:19 AM
 */
public class NameDatePair {

    @Field("N")
    private String name;

    @Field("D")
    private Date monthYear;

    public String getName() {
        return name;
    }

    public NameDatePair setName(String name) {
        this.name = name;
        return this;
    }

    public Date getMonthYear() {
        return monthYear;
    }

    public NameDatePair setMonthYear(Date monthYear) {
        this.monthYear = monthYear;
        return this;
    }
}
