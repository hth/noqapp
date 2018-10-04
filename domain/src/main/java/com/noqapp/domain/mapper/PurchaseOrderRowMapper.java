package com.noqapp.domain.mapper;

import com.noqapp.domain.PurchaseOrderEntity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * hitender
 * 10/4/18 2:53 PM
 */
public class PurchaseOrderRowMapper implements RowMapper<PurchaseOrderEntity> {

    @Override
    public PurchaseOrderEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        PurchaseOrderResultSetExtractor extractor = new PurchaseOrderResultSetExtractor();
        return extractor.extractData(rs);
    }
}
