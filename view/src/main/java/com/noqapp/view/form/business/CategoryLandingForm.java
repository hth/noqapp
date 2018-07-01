package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;

import java.util.Map;

/**
 * Global business category. This is unique to a business. Store will have the capabilities to copy all the
 * categories from business. For a store to define its own category, they need to add Store Category. Store
 * Category is unique to a store and its not shared across other stores.
 *
 * hitender
 * 12/20/17 4:36 PM
 */
public class CategoryLandingForm {

    private ScrubbedInput bizNameId;

    private Map<String, String> categories;
    private Map<String, Long> categoryCounts;

    public ScrubbedInput getBizNameId() {
        return bizNameId;
    }

    public CategoryLandingForm setBizNameId(ScrubbedInput bizNameId) {
        this.bizNameId = bizNameId;
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
