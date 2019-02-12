package com.noqapp.view.form.admin;

import com.noqapp.common.utils.ScrubbedInput;

/**
 * hitender
 * 2019-02-11 18:29
 */
public class SendNotificationForm {

    private ScrubbedInput title;
    private ScrubbedInput body;
    private boolean ignoreSentiments;
    private int sentCount;
    private boolean success;

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
}
