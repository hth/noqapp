package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;

import java.util.Map;

/**
 * Store Category is unique to a store and its not shared with other stores. Nor
 * its copied to a Business.
 *
 * hitender
 * 3/22/18 2:39 PM
 */
public class StoreCategoryForm {
    private ScrubbedInput displayName;

    private ScrubbedInput bizStoreId;
    private ScrubbedInput storeCategoryId;
    private ScrubbedInput categoryName;

    private Map<String, String> categories;
    private Map<String, Long> categoryCounts;

    public ScrubbedInput getDisplayName() {
        return displayName;
    }

    public StoreCategoryForm setDisplayName(ScrubbedInput displayName) {
        this.displayName = displayName;
        return this;
    }

    public ScrubbedInput getBizStoreId() {
        return bizStoreId;
    }

    public StoreCategoryForm setBizStoreId(ScrubbedInput bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public ScrubbedInput getStoreCategoryId() {
        return storeCategoryId;
    }

    public StoreCategoryForm setStoreCategoryId(ScrubbedInput storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
        return this;
    }

    public ScrubbedInput getCategoryName() {
        return categoryName;
    }

    public StoreCategoryForm setCategoryName(ScrubbedInput categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public StoreCategoryForm setCategories(Map<String, String> categories) {
        this.categories = categories;
        return this;
    }

    public Map<String, Long> getCategoryCounts() {
        return categoryCounts;
    }

    public StoreCategoryForm setCategoryCounts(Map<String, Long> categoryCounts) {
        this.categoryCounts = categoryCounts;
        return this;
    }
}
