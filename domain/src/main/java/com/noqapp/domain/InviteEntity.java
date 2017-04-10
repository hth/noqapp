package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import org.junit.Assert;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 3/29/17 6:31 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "INVITE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "invite_rid_iid_idx", def = "{'RID': 1, 'IID' : 1}", background = true, unique = true),
        @CompoundIndex (name = "invite_ic_idx", def = "{'IC': 1}", background = true)
})
public class InviteEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    /* RSR maps to RID. */
    @NotNull
    @Field ("RSR")
    private int remoteScanForReceiptUserCount = 2;

    @NotNull
    @Field ("IC")
    private String inviteeCode;

    @NotNull
    @Field ("IID")
    private String inviterId;

    /* IID maps to RSI. */
    @NotNull
    @Field ("RSI")
    private int remoteScanForInviterCount = 2;

    public InviteEntity(String receiptUserId, String inviterId, String inviteeCode) {
        this.receiptUserId = receiptUserId;
        this.inviterId = inviterId;
        this.inviteeCode = inviteeCode;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public int getRemoteScanForReceiptUserCount() {
        return remoteScanForReceiptUserCount;
    }

    public String getInviteeCode() {
        return inviteeCode;
    }

    public void setInviteeCode(String inviteeCode) {
        this.inviteeCode = inviteeCode;
    }

    public String getInviterId() {
        return inviterId;
    }

    public void setInviterId(String inviterId) {
        this.inviterId = inviterId;
    }

    public int getRemoteScanForInviterCount() {
        return remoteScanForInviterCount;
    }

    public void deductRemoteScanForReceiptUserCount() {
        Assert.assertNotEquals(0, remoteScanForReceiptUserCount);
        this.remoteScanForReceiptUserCount --;
    }

    public void deductRemoteScanForInviterCount() {
        Assert.assertNotEquals(0, remoteScanForInviterCount);
        this.remoteScanForInviterCount --;
    }
}
