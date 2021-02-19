package com.noqapp.view.form.admin;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.BusinessTypeEnum;

import java.util.List;

/**
 * hitender
 * 2019-02-11 18:29
 */
public class SendNotificationForm {

    private ScrubbedInput title;
    private ScrubbedInput body;
    private ScrubbedInput imageURL;
    private boolean ignoreSentiments;
    private int sentCount;
    private boolean success;
    private ScrubbedInput qid;
    private String businessName;
    private BusinessTypeEnum businessType;

    private List<BusinessTypeEnum> businessTypes = BusinessTypeEnum.asList();

    public ScrubbedInput getTitle() {
        return title;
    }

    public SendNotificationForm setTitle(ScrubbedInput title) {
        this.title = title;
        return this;
    }

    public ScrubbedInput getBody() {
        return body;
    }

    public SendNotificationForm setBody(ScrubbedInput body) {
        this.body = body;
        return this;
    }

    public ScrubbedInput getImageURL() {
        return imageURL;
    }

    public SendNotificationForm setImageURL(ScrubbedInput imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public boolean isIgnoreSentiments() {
        return ignoreSentiments;
    }

    public SendNotificationForm setIgnoreSentiments(boolean ignoreSentiments) {
        this.ignoreSentiments = ignoreSentiments;
        return this;
    }

    public int getSentCount() {
        return sentCount;
    }

    public SendNotificationForm setSentCount(int sentCount) {
        this.sentCount = sentCount;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public SendNotificationForm setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public ScrubbedInput getQid() {
        return qid;
    }

    public SendNotificationForm setQid(ScrubbedInput qid) {
        this.qid = qid;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public SendNotificationForm setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public SendNotificationForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public List<BusinessTypeEnum> getBusinessTypes() {
        return businessTypes;
    }

    public SendNotificationForm setBusinessTypes(List<BusinessTypeEnum> businessTypes) {
        this.businessTypes = businessTypes;
        return this;
    }
}
