package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizCategoryEntity;

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
    private ScrubbedInput bizCategoryId;
    private ScrubbedInput categoryName;
    private ScrubbedInput displayImage;

    private Map<String, BizCategoryEntity> categories;
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

    public ScrubbedInput getDisplayImage() {
        return displayImage;
    }

    public CategoryLandingForm setDisplayImage(ScrubbedInput displayImage) {
        this.displayImage = displayImage;
        return this;
    }

    public Map<String, BizCategoryEntity> getCategories() {
        return categories;
    }

    public CategoryLandingForm setCategories(Map<String, BizCategoryEntity> categories) {
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
