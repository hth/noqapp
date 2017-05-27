package com.noqapp.domain;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private int remoteJoinForReceiptUserCount = 2;

    @NotNull
    @Field ("IC")
    private String inviteeCode;

    @NotNull
    @Field ("IID")
    private String inviterId;

    /* IID maps to RSI. */
    @NotNull
    @Field ("RSI")
    private int remoteJoinForInviterCount = 2;

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

    public int getRemoteJoinForReceiptUserCount() {
        return remoteJoinForReceiptUserCount;
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

    public int getRemoteJoinForInviterCount() {
        return remoteJoinForInviterCount;
    }

    public void deductRemoteJoinForReceiptUserCount() {
        assertNotEquals(0, remoteJoinForReceiptUserCount);
        this.remoteJoinForReceiptUserCount--;
    }

    public void deductRemoteJoinForInviterCount() {
        assertNotEquals(0, remoteJoinForInviterCount);
        this.remoteJoinForInviterCount--;
    }
}
