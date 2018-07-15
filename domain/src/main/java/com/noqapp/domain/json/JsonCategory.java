package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

/**
 * hitender
 * 12/21/17 1:15 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonCategory extends AbstractDomain {

    @JsonProperty("bc")
    private String bizCategoryId;

    @JsonProperty("cn")
    private String categoryName;

    @JsonProperty("di")
    private String displayImage = "https://noqapp.com/imgs/240x120/f.jpeg";

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonCategory setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public JsonCategory setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public JsonCategory setDisplayImage(String displayImage) {
        //TODO(hth) remove the check
        if (StringUtils.isNotBlank(displayImage)) {
            this.displayImage = displayImage;
        }
        return this;
    }
}
