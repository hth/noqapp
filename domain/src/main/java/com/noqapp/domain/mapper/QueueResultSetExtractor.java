package com.noqapp.domain.mapper;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 3/11/17 4:45 PM
 */
public class QueueResultSetExtractor implements ResultSetExtractor {

    private static final int ID = 1;
    private static final int QR = 2;
    private static final int DID = 3;
    private static final int TS = 4;
    private static final int QID = 5;
    private static final int TN = 6;
    private static final int DT = 7;
    private static final int DN = 8;
    private static final int BT = 9;
    private static final int QS = 10;
    private static final int TI = 11;
    private static final int NS = 12;
    private static final int RA = 13;
    private static final int HR = 14;
    private static final int RV = 15;
    private static final int SN = 16;
    private static final int SB = 17;
    private static final int SE = 18;
    private static final int BN = 19;
    private static final int RR = 20;
    private static final int ST = 21;
    private static final int AC = 22;

    private static final int V = 23;
    private static final int U = 24;
    private static final int C = 25;
    private static final int A = 26;
    private static final int D = 27;

    @Override
    public QueueEntity extractData(@NotNull ResultSet rs) throws SQLException {
        QueueEntity queue = new QueueEntity(
                rs.getString(QR),
                rs.getString(DID),
                TokenServiceEnum.valueOf(rs.getString(TS)),
                rs.getString(QID),
                rs.getInt(TN),
                rs.getString(DT),
                rs.getString(DN),
                BusinessTypeEnum.valueOf(rs.getString(BT))
        );
        queue.setId(rs.getString(ID));
        queue.setQueueUserState(QueueUserStateEnum.valueOf(rs.getString(QS)));
        queue.setNotifiedOnService(rs.getInt(NS) == 1);
        queue.setRatingCount(rs.getInt(RA));
        queue.setHoursSaved(rs.getInt(HR));
        queue.setReview(rs.getString(RV));
        queue.setServerName(rs.getString(SN));
        queue.setTransactionId(rs.getString(TI));
        queue.setServiceBeginTime(rs.getTimestamp(SB));
        queue.setServiceEndTime(rs.getTimestamp(SE));
        queue.setBizNameId(rs.getString(BN));
        queue.setRecordReferenceId(rs.getString(RR));
        queue.setSentimentType(null == rs.getString(ST) ? null : SentimentTypeEnum.valueOf(rs.getString(ST)));
        queue.setAuthorizedCheckByQid(rs.getString(AC));
        queue.setVersion(rs.getInt(V));
        queue.setCreateAndUpdate(rs.getTimestamp(U));
        queue.setCreated(rs.getTimestamp(C));
        if (rs.getInt(A) > 0) {
            queue.active();
        } else {
            queue.inActive();
        }

        if (rs.getInt(D) > 0) {
            queue.markAsDeleted();
        }

        return queue;
    }
}
