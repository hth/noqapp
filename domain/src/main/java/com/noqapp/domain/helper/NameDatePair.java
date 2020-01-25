package com.noqapp.domain.helper;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

/**
 * hitender
 * 5/30/18 11:19 AM
 */
public class NameDatePair implements Serializable {

    @Field("N")
    private String name;

    @Field("D")
    private String monthYear;

    public String getName() {
        return name;
    }

    public NameDatePair setName(String name) {
        this.name = name;
        return this;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public NameDatePair setMonthYear(String monthYear) {
        this.monthYear = monthYear;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameDatePair that = (NameDatePair) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(monthYear, that.monthYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, monthYear);
    }
}
