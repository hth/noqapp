package com.noqapp.domain.mapper;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.QueueUserStateEnum;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: hitender
 * Date: 3/11/17 4:45 PM
 */
public class QueueResultSetExtractor implements ResultSetExtractor {

    @Override
    public QueueEntity extractData(ResultSet rs) throws SQLException {
        QueueEntity queue = new QueueEntity(
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getInt(5),
                rs.getString(6)
        );
        queue.setId(rs.getString(1));
        queue.setQueueUserState(QueueUserStateEnum.valueOf(rs.getString(7)));
        queue.setNotifiedOnService(rs.getInt(8) == 1);
        queue.setRatingCount(rs.getInt(9));
        queue.setHoursSaved(rs.getInt(10));
        queue.setVersion(rs.getInt(11));
        queue.setCreateAndUpdate(rs.getTimestamp(12));
        queue.setCreated(rs.getTimestamp(13));
        if (rs.getInt(14) > 0) {
            queue.active();
        } else {
            queue.inActive();
        }

        if (rs.getInt(15) > 0) {
            queue.markAsDeleted();
        }

        return queue;
    }
}
