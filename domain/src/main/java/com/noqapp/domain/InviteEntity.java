package com.noqapp.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
        @CompoundIndex (name = "invite_qid_iid_idx", def = "{'QID': 1, 'IID' : 1}", background = true, unique = true),
        @CompoundIndex (name = "invite_ic_idx", def = "{'IC': 1}", background = true)
})
public class InviteEntity extends BaseEntity {

    @NotNull
    @Field ("QID")
    private String queueUserId;

    @Deprecated
    /* RJQ maps to QID. */
    @NotNull
    @Field ("RJQ")
    private int remoteJoinForQueueUserCount;

    @NotNull
    @Field ("IC")
    private String inviteeCode;

    @NotNull
    @Field ("IID")
    private String inviterId;

    @Deprecated
    /* IID maps to RJI. */
    @NotNull
    @Field ("RJI")
    private int remoteJoinForInviterCount;

    @SuppressWarnings("unused")
    public InviteEntity() {
        //Default constructor, required to keep bean happy
    }

    public InviteEntity(
            String queueUserId,
            String inviterId,
            String inviteeCode,
            int freeRemoteJoins
    ) {
        this.queueUserId = queueUserId;
        this.remoteJoinForQueueUserCount = freeRemoteJoins;
        if (StringUtils.isNotBlank(inviteeCode)) {
            this.inviteeCode = inviteeCode;
            this.inviterId = inviterId;
            this.remoteJoinForInviterCount = freeRemoteJoins;
        }
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public void setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public int getRemoteJoinForQueueUserCount() {
        return remoteJoinForQueueUserCount;
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

    public void deductRemoteJoinForQueueUserCount() {
        assertNotEquals(0, remoteJoinForQueueUserCount);
        this.remoteJoinForQueueUserCount--;
    }

    public void deductRemoteJoinForInviterCount() {
        assertNotEquals(0, remoteJoinForInviterCount);
        this.remoteJoinForInviterCount--;
    }
}
