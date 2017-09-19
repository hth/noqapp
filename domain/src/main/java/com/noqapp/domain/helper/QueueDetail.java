package com.noqapp.domain.helper;

/**
 * Business admin landing page queue details are stored.
 *
 * User: hitender
 * Date: 9/20/17 3:15 AM
 */
public class QueueDetail {

    private String id;
    private Long assignedToQueue;
    private Long pendingApprovalToQueue;

    public String getId() {
        return id;
    }

    public QueueDetail setId(String id) {
        this.id = id;
        return this;
    }

    public Long getAssignedToQueue() {
        return assignedToQueue;
    }

    public QueueDetail setAssignedToQueue(Long assignedToQueue) {
        this.assignedToQueue = assignedToQueue;
        return this;
    }

    public Long getPendingApprovalToQueue() {
        return pendingApprovalToQueue;
    }

    public QueueDetail setPendingApprovalToQueue(Long pendingApprovalToQueue) {
        this.pendingApprovalToQueue = pendingApprovalToQueue;
        return this;
    }
}
