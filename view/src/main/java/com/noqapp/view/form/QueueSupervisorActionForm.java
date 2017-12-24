package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;

/**
 * User: hitender
 * Date: 9/20/17 1:39 PM
 */
public class QueueSupervisorActionForm {

    private ScrubbedInput action;
    private ScrubbedInput businessUserId;
    private ScrubbedInput bizStoreId;

    public ScrubbedInput getAction() {
        return action;
    }

    public QueueSupervisorActionForm setAction(ScrubbedInput action) {
        this.action = action;
        return this;
    }

    public ScrubbedInput getBusinessUserId() {
        return businessUserId;
    }

    public QueueSupervisorActionForm setBusinessUserId(ScrubbedInput businessUserId) {
        this.businessUserId = businessUserId;
        return this;
    }

    public ScrubbedInput getBizStoreId() {
        return bizStoreId;
    }

    public QueueSupervisorActionForm setBizStoreId(ScrubbedInput bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }
}
