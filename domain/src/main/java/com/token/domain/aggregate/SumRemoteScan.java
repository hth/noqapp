package com.token.domain.aggregate;

/**
 * User: hitender
 * Date: 3/31/17 2:42 AM
 */
public class SumRemoteScan {
    private int remoteScanForReceiptUserCount;
    private int remoteScanForInviterCount;

    public int getRemoteScanForReceiptUserCount() {
        return remoteScanForReceiptUserCount;
    }

    public void setRemoteScanForReceiptUserCount(int remoteScanForReceiptUserCount) {
        this.remoteScanForReceiptUserCount = remoteScanForReceiptUserCount;
    }

    public int getRemoteScanForInviterCount() {
        return remoteScanForInviterCount;
    }

    public void setRemoteScanForInviterCount(int remoteScanForInviterCount) {
        this.remoteScanForInviterCount = remoteScanForInviterCount;
    }


    public int getSumOfRemoteScanAvailable() {
        return remoteScanForInviterCount + remoteScanForReceiptUserCount;
    }
}
