package com.noqapp.domain;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.apache.commons.lang3.StringUtils;

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
    private int pointsForQueueUserCount;

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
    private int pointsForInviterCount;

    @SuppressWarnings("unused")
    public InviteEntity() {
        //Default constructor, required to keep bean happy
    }

    public InviteEntity(
            String queueUserId,
            String inviterId,
            String inviteeCode,
            int points
    ) {
        this.queueUserId = queueUserId;
        this.pointsForQueueUserCount = points;
        if (StringUtils.isNotBlank(inviteeCode)) {
            this.inviteeCode = inviteeCode;
            this.inviterId = inviterId;
            this.pointsForInviterCount = points;
        }
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public void setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public int getPointsForQueueUserCount() {
        return pointsForQueueUserCount;
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

    public int getPointsForInviterCount() {
        return pointsForInviterCount;
    }

    public void deductPointsForQueueUserCount() {
        assertNotEquals(0, pointsForQueueUserCount);
        this.pointsForQueueUserCount--;
    }

    public void deductPointsForInviterCount() {
        assertNotEquals(0, pointsForInviterCount);
        this.pointsForInviterCount--;
    }
}
