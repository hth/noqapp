package com.noqapp.domain.mapper;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.types.DeliveryTypeEnum;
import com.noqapp.domain.types.PaymentTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * hitender
 * 10/4/18 2:54 PM
 */
public class PurchaseOrderResultSetExtractor implements ResultSetExtractor {

    private static final int ID = 1;
    private static final int QID = 2;
    private static final int BS = 3;
    private static final int BN = 4;
    private static final int QR = 5;
    private static final int DM = 6;
    private static final int PT = 7;
    private static final int PS = 8;
    private static final int RA = 9;
    private static final int RV = 10;
    private static final int TN = 11;
    private static final int V = 12;
    private static final int U = 13;
    private static final int C = 14;
    private static final int A = 15;
    private static final int D = 16;

    @Override
    public PurchaseOrderEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        PurchaseOrderEntity queue = new PurchaseOrderEntity(
            rs.getString(QID),
            rs.getString(BS),
            rs.getString(BN),
            rs.getString(QR)
        );
        queue.setId(rs.getString(ID));
        queue.setDeliveryType(DeliveryTypeEnum.valueOf(rs.getString(DM)));
        queue.setPaymentType(PaymentTypeEnum.valueOf(rs.getString(PT)));
        queue.addOrderState(PurchaseOrderStateEnum.valueOf(rs.getString(PS)));
        queue.setRatingCount(rs.getInt(RA));
        queue.setReview(rs.getString(RV));
        queue.setTokenNumber(rs.getInt(TN));
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
