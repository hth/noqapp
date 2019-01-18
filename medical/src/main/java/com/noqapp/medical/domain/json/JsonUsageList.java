package com.noqapp.medical.domain.json;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * hitender
 * 2018-11-27 07:16
 */
public class JsonUsageList {

    private int totalInstance;
    private List<JsonUsage> jsonUsages = new ArrayList<>();

    public int getTotalInstance() {
        return totalInstance;
    }

    public JsonUsageList setTotalInstance(int totalInstance) {
        this.totalInstance = totalInstance;
        return this;
    }

    public List<JsonUsage> getJsonUsages() {
        return jsonUsages;
    }

    public JsonUsageList setJsonUsages(List<JsonUsage> jsonUsages) {
        this.jsonUsages = jsonUsages;
        return this;
    }

    public JsonUsageList addJsonUsages(JsonUsage jsonUsage) {
        this.jsonUsages.add(jsonUsage);
        return this;
    }

    public void sortAndRank() {
        jsonUsages.sort(Comparator.comparingInt(JsonUsage::computeFrequentUsage));
        int rank = 1;
        for (JsonUsage jsonUsage : jsonUsages) {
            jsonUsage.setRank(rank);
        }
    }
}
