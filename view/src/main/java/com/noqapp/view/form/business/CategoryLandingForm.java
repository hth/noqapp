package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;

import java.util.Map;

/**
 * hitender
 * 12/20/17 4:36 PM
 */
public class CategoryLandingForm {

    private ScrubbedInput bizNameId;
    private ScrubbedInput bizCategoryId;
    private ScrubbedInput categoryName;
    private Map<String, String> categories;
    private Map<String, Long> categoryCounts;

    public ScrubbedInput getBizNameId() {
        return bizNameId;
    }

    public CategoryLandingForm setBizNameId(ScrubbedInput bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public ScrubbedInput getBizCategoryId() {
        return bizCategoryId;
    }

    public CategoryLandingForm setBizCategoryId(ScrubbedInput bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public ScrubbedInput getCategoryName() {
        return categoryName;
    }

    public CategoryLandingForm setCategoryName(ScrubbedInput categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public CategoryLandingForm setCategories(Map<String, String> categories) {
        this.categories = categories;
        return this;
    }

    public Map<String, Long> getCategoryCounts() {
        return categoryCounts;
    }

    public CategoryLandingForm setCategoryCounts(Map<String, Long> categoryCounts) {
        this.categoryCounts = categoryCounts;
        return this;
    }
}
