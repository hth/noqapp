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
    private static final int DN = 7;
    private static final int BT = 8;
    private static final int QS = 9;
    private static final int TI = 10;
    private static final int NS = 11;
    private static final int RA = 12;
    private static final int HR = 13;
    private static final int RV = 14;
    private static final int SN = 15;
    private static final int SB = 16;
    private static final int SE = 17;
    private static final int BN = 18;
    private static final int RR = 19;
    private static final int ST = 20;

    private static final int V = 21;
    private static final int U = 22;
    private static final int C = 23;
    private static final int A = 24;
    private static final int D = 25;

    @Override
    public QueueEntity extractData(@NotNull ResultSet rs) throws SQLException {
        QueueEntity queue = new QueueEntity(
                rs.getString(QR),
                rs.getString(DID),
                TokenServiceEnum.valueOf(rs.getString(TS)),
                rs.getString(QID),
                rs.getInt(TN),
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
