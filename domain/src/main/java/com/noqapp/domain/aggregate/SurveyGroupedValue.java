package com.noqapp.domain.aggregate;

/**
 * User: hitender
 * Date: 10/26/19 9:33 AM
 */
public class SurveyGroupedValue {

    private String bizStoreId;
    private float summationOverallRating;
    private int numberOfSurvey;
    private int sumOfPositiveSentiments;
    private int sumOfNegativeSentiments;

    private String area;
    private String town;
    private String displayName;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public SurveyGroupedValue setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public float getSummationOverallRating() {
        return summationOverallRating;
    }

    public SurveyGroupedValue setSummationOverallRating(float summationOverallRating) {
        this.summationOverallRating = summationOverallRating;
        return this;
    }

    public int getNumberOfSurvey() {
        return numberOfSurvey;
    }

    public SurveyGroupedValue setNumberOfSurvey(int numberOfSurvey) {
        this.numberOfSurvey = numberOfSurvey;
        return this;
    }

    public int getSumOfPositiveSentiments() {
        return sumOfPositiveSentiments;
    }

    public SurveyGroupedValue setSumOfPositiveSentiments(int sumOfPositiveSentiments) {
        this.sumOfPositiveSentiments = sumOfPositiveSentiments;
        return this;
    }

    public int getSumOfNegativeSentiments() {
        return sumOfNegativeSentiments;
    }

    public SurveyGroupedValue setSumOfNegativeSentiments(int sumOfNegativeSentiments) {
        this.sumOfNegativeSentiments = sumOfNegativeSentiments;
        return this;
    }

    public String getArea() {
        return area;
    }

    public SurveyGroupedValue setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public SurveyGroupedValue setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SurveyGroupedValue setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
}
