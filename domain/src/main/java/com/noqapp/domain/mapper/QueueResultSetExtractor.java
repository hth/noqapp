package com.noqapp.domain.mapper;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: hitender
 * Date: 3/11/17 4:45 PM
 */
public class QueueResultSetExtractor implements ResultSetExtractor {

    public static final int ID = 1;
    public static final int QR = 2;
    public static final int DID = 3;
    public static final int TS = 4;
    public static final int QID = 5;
    public static final int TN = 6;
    public static final int DN = 7;
    public static final int QS = 8;
    public static final int NS = 9;
    public static final int RA = 10;
    public static final int HR = 11;
    public static final int SN = 12;
    public static final int SB = 13;
    public static final int SE = 14;
    public static final int V = 15;
    public static final int U = 16;
    public static final int C = 17;
    public static final int A = 18;
    public static final int D = 19;

    @Override
    public QueueEntity extractData(ResultSet rs) throws SQLException {
        QueueEntity queue = new QueueEntity(
                rs.getString(QR),
                rs.getString(DID),
                rs.getString(QID),
                rs.getInt(TN),
                rs.getString(DN),
                TokenServiceEnum.valueOf(rs.getString(TS))
        );
        queue.setId(rs.getString(ID));
        queue.setQueueUserState(QueueUserStateEnum.valueOf(rs.getString(QS)));
        queue.setNotifiedOnService(rs.getInt(NS) == 1);
        queue.setRatingCount(rs.getInt(RA));
        queue.setHoursSaved(rs.getInt(HR));
        queue.setServerName(rs.getString(SN));
        queue.setServiceBeginTime(rs.getTimestamp(SB));
        queue.setServiceEndTime(rs.getTimestamp(SE));
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
