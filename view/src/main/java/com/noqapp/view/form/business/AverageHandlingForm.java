package com.noqapp.view.form.business;

import com.noqapp.domain.helper.ExpectedHandlingTime;

import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hitender
 * 6/30/20 5:39 PM
 */
public class AverageHandlingForm {

    private String bizStoreId;
    private String codeQR;
    private String displayName;
    private DayOfWeek selectedDayOfWeek;
    private Map<DayOfWeek, String> availableDayOfWeeks = new LinkedHashMap<>();

    private int availableTokenCount;
    private boolean hasBreakTimeEnabled;
    private long averageServiceTime;

    private Map<String, ExpectedHandlingTime> openDurationEachDayOfWeek = new LinkedHashMap<>();

    public String getBizStoreId() {
        return bizStoreId;
    }

    public AverageHandlingForm setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public AverageHandlingForm setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AverageHandlingForm setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public DayOfWeek getSelectedDayOfWeek() {
        return selectedDayOfWeek;
    }

    public AverageHandlingForm setSelectedDayOfWeek(DayOfWeek selectedDayOfWeek) {
        this.selectedDayOfWeek = selectedDayOfWeek;
        return this;
    }

    public Map<DayOfWeek, String> getAvailableDayOfWeeks() {
        return availableDayOfWeeks;
    }

    public AverageHandlingForm setAvailableDayOfWeeks(Map<DayOfWeek, String> availableDayOfWeeks) {
        this.availableDayOfWeeks = availableDayOfWeeks;
        return this;
    }

    public AverageHandlingForm addAvailableDayOfWeeks(DayOfWeek dayOfWeek) {
        this.availableDayOfWeeks.put(dayOfWeek, StringUtils.capitalize(dayOfWeek.toString().toLowerCase()));
        return this;
    }

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public AverageHandlingForm setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
        return this;
    }

    public boolean isHasBreakTimeEnabled() {
        return hasBreakTimeEnabled;
    }

    public AverageHandlingForm setHasBreakTimeEnabled(boolean hasBreakTimeEnabled) {
        this.hasBreakTimeEnabled = hasBreakTimeEnabled;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public AverageHandlingForm setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
        return this;
    }

    public Map<String, ExpectedHandlingTime> getOpenDurationEachDayOfWeek() {
        return openDurationEachDayOfWeek;
    }

    public AverageHandlingForm setOpenDurationEachDayOfWeek(Map<String, ExpectedHandlingTime> openDurationEachDayOfWeek) {
        this.openDurationEachDayOfWeek = openDurationEachDayOfWeek;
        return this;
    }
}
