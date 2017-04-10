package com.noqapp.domain.mapper;

import org.springframework.jdbc.core.RowMapper;

import com.noqapp.domain.QueueEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: hitender
 * Date: 3/11/17 4:44 PM
 */
public class QueueRowMapper implements RowMapper<QueueEntity> {
    
    @Override
    public QueueEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        QueueResultSetExtractor extractor = new QueueResultSetExtractor();
        return extractor.extractData(rs);
    }
}
