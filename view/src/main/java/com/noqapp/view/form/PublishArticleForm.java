package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.ValidateStatusEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * hitender
 * 2018-12-30 00:40
 */
public class PublishArticleForm extends FileUploadForm implements Serializable {

    private String title;
    private String description;
    private String bannerImage;

    private Date publishDate;
    private ValidateStatusEnum validateStatus = ValidateStatusEnum.I;

    /* Other properties. */
    private ScrubbedInput publishId;
    private ScrubbedInput action;
    private boolean active;

    private PublishArticleForm() {
    }

    public static PublishArticleForm newInstance() {
        return new PublishArticleForm();
    }

    public String getTitle() {
        return title;
    }

    public PublishArticleForm setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PublishArticleForm setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public PublishArticleForm setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
        return this;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public PublishArticleForm setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public ValidateStatusEnum getValidateStatus() {
        return validateStatus;
    }

    public PublishArticleForm setValidateStatus(ValidateStatusEnum validateStatus) {
        this.validateStatus = validateStatus;
        return this;
    }

    public ScrubbedInput getPublishId() {
        return publishId;
    }

    public PublishArticleForm setPublishId(ScrubbedInput publishId) {
        this.publishId = publishId;
        return this;
    }

    public ScrubbedInput getAction() {
        return action;
    }

    public PublishArticleForm setAction(ScrubbedInput action) {
        this.action = action;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public PublishArticleForm setActive(boolean active) {
        this.active = active;
        return this;
    }
}
