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

    private static final int ID = 1;
    private static final int QR = 2;
    private static final int DID = 3;
    private static final int TS = 4;
    private static final int QID = 5;
    private static final int TN = 6;
    private static final int DN = 7;
    private static final int QS = 8;
    private static final int NS = 9;
    private static final int RA = 10;
    private static final int HR = 11;
    private static final int SN = 12;
    private static final int SB = 13;
    private static final int SE = 14;
    private static final int V = 15;
    private static final int U = 16;
    private static final int C = 17;
    private static final int A = 18;
    private static final int D = 19;

    @Override
    public QueueEntity extractData(ResultSet rs) throws SQLException {
        QueueEntity queue = new QueueEntity(
                rs.getString(QR),
                rs.getString(DID),
                TokenServiceEnum.valueOf(rs.getString(TS)),
                rs.getString(QID),
                rs.getInt(TN),
                rs.getString(DN)
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
