package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.ValidateStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * hitender
 * 12/28/20 10:01 AM
 */
public class PublishJobForm implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PublishJobForm.class);

    private ScrubbedInput title;
    private String description;

    private Date publishDate = new Date();
    private ValidateStatusEnum validateStatus = ValidateStatusEnum.A;

    /* Other properties. */
    private ScrubbedInput publishId;
    private ScrubbedInput action;
    private boolean active;

    private PublishJobForm() {
    }

    public static PublishJobForm newInstance() {
        return new PublishJobForm();
    }

    public ScrubbedInput getTitle() {
        return title;
    }

    public PublishJobForm setTitle(ScrubbedInput title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PublishJobForm setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public PublishJobForm setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public ValidateStatusEnum getValidateStatus() {
        return validateStatus;
    }

    public PublishJobForm setValidateStatus(ValidateStatusEnum validateStatus) {
        this.validateStatus = validateStatus;
        return this;
    }

    public ScrubbedInput getPublishId() {
        return publishId;
    }

    public PublishJobForm setPublishId(ScrubbedInput publishId) {
        this.publishId = publishId;
        return this;
    }

    public ScrubbedInput getAction() {
        return action;
    }

    public PublishJobForm setAction(ScrubbedInput action) {
        this.action = action;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public PublishJobForm setActive(boolean active) {
        this.active = active;
        return this;
    }
}
