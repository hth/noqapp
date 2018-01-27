package com.noqapp.domain.helper;

/**
 * Business admin landing page queue details are stored.
 *
 * User: hitender
 * Date: 9/20/17 3:15 AM
 */
public class QueueDetail {

    private String id;
    private long assignedToQueue;
    private long pendingApprovalToQueue;

    /* Used in Queue Supervisor and Queue Manager Screen. */
    private long previouslyVisitedClientCount;

    /* Used in Queue Supervisor and Queue Manager Screen. */
    private long newVisitClientCount;

    public String getId() {
        return id;
    }

    public QueueDetail setId(String id) {
        this.id = id;
        return this;
    }

    public long getAssignedToQueue() {
        return assignedToQueue;
    }

    public QueueDetail setAssignedToQueue(Long assignedToQueue) {
        this.assignedToQueue = assignedToQueue;
        return this;
    }

    public long getPendingApprovalToQueue() {
        return pendingApprovalToQueue;
    }

    public QueueDetail setPendingApprovalToQueue(Long pendingApprovalToQueue) {
        this.pendingApprovalToQueue = pendingApprovalToQueue;
        return this;
    }

    public long getPreviouslyVisitedClientCount() {
        return previouslyVisitedClientCount;
    }

    public QueueDetail setPreviouslyVisitedClientCount(long previouslyVisitedClientCount) {
        this.previouslyVisitedClientCount = previouslyVisitedClientCount;
        return this;
    }

    public long getNewVisitClientCount() {
        return newVisitClientCount;
    }

    public QueueDetail setNewVisitClientCount(long newVisitClientCount) {
        this.newVisitClientCount = newVisitClientCount;
        return this;
    }
}
