package com.token.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.token.domain.types.QueueUserStateEnum;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 12/16/16 12:42 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "QUEUE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "queue_idx", def = "{'QR' : -1, 'DID': -1, 'RID': -1}", unique = false, background = true, sparse = true),
        @CompoundIndex (name = "queue_tn_idx", def = "{'QR' : -1, 'TN': -1}", unique = true, background = true)
})
public class QueueEntity extends BaseEntity {

    @NotNull
    @Field ("QR")
    private String codeQR;

    @Field ("DID")
    private String did;

    @Field ("RID")
    private String rid;

    @NotNull
    @Field ("TN")
    private int tokenNumber;

    @NotNull
    @Field ("DN")
    private String displayName;

    @NotNull
    @Field ("QS")
    private QueueUserStateEnum queueUserState = QueueUserStateEnum.Q;

    @NotNull
    @Field ("NS")
    private boolean notifiedOnService = false;

    @NotNull
    @Field ("NC")
    private int attemptToSendNotificationCounts;

    public QueueEntity(String codeQR, String did, String rid, int tokenNumber, String displayName) {
        this.codeQR = codeQR;
        this.did = did;
        this.rid = rid;
        this.tokenNumber = tokenNumber;
        this.displayName = displayName;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public String getDid() {
        return did;
    }

    public String getRid() {
        return rid;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public void setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
    }

    public boolean isNotifiedOnService() {
        return notifiedOnService;
    }

    public void setNotifiedOnService(boolean notifiedOnService) {
        this.notifiedOnService = notifiedOnService;
    }

    public int getAttemptToSendNotificationCounts() {
        return attemptToSendNotificationCounts;
    }
}
