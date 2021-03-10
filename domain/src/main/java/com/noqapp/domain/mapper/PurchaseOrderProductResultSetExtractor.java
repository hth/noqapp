package com.noqapp.domain.mapper;

import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.TaxEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: hitender
 * Date: 10/5/18 8:17 PM
 */
public class PurchaseOrderProductResultSetExtractor implements ResultSetExtractor<PurchaseOrderProductEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderProductResultSetExtractor.class);

    private static final int ID = 1;
    private static final int PN = 2;
    private static final int PP = 3;
    private static final int TA = 4;
    private static final int PD = 5;
    private static final int PT = 6;
    private static final int UV = 7;
    private static final int UM = 8;
    private static final int PS = 9;
    private static final int PQ = 10;
    private static final int PO = 11;
    private static final int QID = 12;
    private static final int BS = 13;
    private static final int BN = 14;
    private static final int QR = 15;
    private static final int BT = 16;

    private static final int V = 17;
    private static final int U = 18;
    private static final int C = 19;
    private static final int A = 20;
    private static final int D = 21;

    @Override
    public PurchaseOrderProductEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        try {
            PurchaseOrderProductEntity purchaseOrderProduct = new PurchaseOrderProductEntity();
            purchaseOrderProduct.setId(rs.getString(ID));
            purchaseOrderProduct.setProductName(rs.getString(PN));
            purchaseOrderProduct.setProductPrice(rs.getInt(PP));
            purchaseOrderProduct.setTax(TaxEnum.valueOf(rs.getString(TA)));
            purchaseOrderProduct.setProductDiscount(rs.getInt(PD));
            purchaseOrderProduct.setProductType(StringUtils.isBlank(rs.getString(PT)) ? null : ProductTypeEnum.valueOf(rs.getString(PT)));
            purchaseOrderProduct.setUnitValue(rs.getInt(UV));
            purchaseOrderProduct.setUnitOfMeasurement(StringUtils.isBlank(rs.getString(UM)) ? null : UnitOfMeasurementEnum.valueOf(rs.getString(UM)));
            purchaseOrderProduct.setPackageSize(rs.getInt(PS));
            purchaseOrderProduct.setProductQuantity(rs.getInt(PQ));
            purchaseOrderProduct.setPurchaseOrderId(rs.getString(PO));
            purchaseOrderProduct.setQueueUserId(rs.getString(QID));
            purchaseOrderProduct.setBizStoreId(rs.getString(BS));
            purchaseOrderProduct.setBizNameId(rs.getString(BN));
            purchaseOrderProduct.setCodeQR(rs.getString(QR));
            purchaseOrderProduct.setBusinessType(BusinessTypeEnum.valueOf(rs.getString(BT)));

            purchaseOrderProduct.setVersion(rs.getInt(V));
            purchaseOrderProduct.setCreateAndUpdate(rs.getTimestamp(U));
            purchaseOrderProduct.setCreated(rs.getTimestamp(C));
            if (rs.getInt(A) > 0) {
                purchaseOrderProduct.active();
            } else {
                purchaseOrderProduct.inActive();
            }

            if (rs.getInt(D) > 0) {
                purchaseOrderProduct.markAsDeleted();
            }

            return purchaseOrderProduct;
        } catch (Exception e) {
            LOG.error("Failed populating extractor {} {}", e.getLocalizedMessage(), e);
            throw e;
        }
    }
}
