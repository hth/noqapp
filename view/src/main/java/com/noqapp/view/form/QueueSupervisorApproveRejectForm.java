package com.noqapp.view.form;

import com.noqapp.utils.ScrubbedInput;

/**
 * User: hitender
 * Date: 9/20/17 1:39 PM
 */
public class QueueSupervisorApproveRejectForm {

    private ScrubbedInput approveOrReject;
    private ScrubbedInput referenceId;
    private ScrubbedInput storeId;

    public ScrubbedInput getApproveOrReject() {
        return approveOrReject;
    }

    public QueueSupervisorApproveRejectForm setApproveOrReject(ScrubbedInput approveOrReject) {
        this.approveOrReject = approveOrReject;
        return this;
    }

    public ScrubbedInput getReferenceId() {
        return referenceId;
    }

    public QueueSupervisorApproveRejectForm setReferenceId(ScrubbedInput referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public ScrubbedInput getStoreId() {
        return storeId;
    }

    public QueueSupervisorApproveRejectForm setStoreId(ScrubbedInput storeId) {
        this.storeId = storeId;
        return this;
    }
}
