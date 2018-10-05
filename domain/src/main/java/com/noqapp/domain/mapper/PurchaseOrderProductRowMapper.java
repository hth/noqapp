package com.noqapp.domain.mapper;

import com.noqapp.domain.PurchaseOrderProductEntity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: hitender
 * Date: 10/5/18 7:37 PM
 */
public class PurchaseOrderProductRowMapper implements RowMapper<PurchaseOrderProductEntity> {

    @Override
    public PurchaseOrderProductEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        PurchaseOrderProductResultSetExtractor extractor = new PurchaseOrderProductResultSetExtractor();
        return extractor.extractData(rs);
    }
}
