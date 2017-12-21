package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;

import java.util.Map;

/**
 * hitender
 * 12/20/17 4:36 PM
 */
public class CategoryLandingForm {

    private ScrubbedInput bizNameId;
    private ScrubbedInput categoryId;
    private ScrubbedInput categoryName;
    private Map<String, String> categories;

    public ScrubbedInput getBizNameId() {
        return bizNameId;
    }

    public CategoryLandingForm setBizNameId(ScrubbedInput bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public ScrubbedInput getCategoryId() {
        return categoryId;
    }

    public CategoryLandingForm setCategoryId(ScrubbedInput categoryId) {
        this.categoryId = categoryId;
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
}
