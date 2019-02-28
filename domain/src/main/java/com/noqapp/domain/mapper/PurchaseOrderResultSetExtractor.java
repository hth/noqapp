package com.noqapp.domain.mapper;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryTypeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
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
    private static final int PY = 7+1;
    private static final int PS = 8+1;
    private static final int DA = 9+1;
    private static final int RA = 10+1;
    private static final int RV = 11+1;
    private static final int TN = 12+1;

    private static final int SD = 13+1;
    private static final int OP = 14+1;
    private static final int BT = 15+1;
    private static final int SN = 16+1;
    private static final int SB = 17+1;
    private static final int SE = 18+1;
    private static final int TI = 19+1;
    private static final int DN = 20+1;
    private static final int AN = 21+1;

    private static final int V = 22+1;
    private static final int U = 23+1;
    private static final int C = 24+1;
    private static final int A = 25+1;
    private static final int D = 26+1;

    @Override
    public PurchaseOrderEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(
                rs.getString(QID),
                rs.getString(BS),
                rs.getString(BN),
                rs.getString(QR)
        );
        purchaseOrder.setId(rs.getString(ID));
        purchaseOrder.setDeliveryType(DeliveryTypeEnum.valueOf(rs.getString(DM)));
        purchaseOrder.setPaymentType(PaymentTypeEnum.valueOf(rs.getString(PT)));
        purchaseOrder.setPaymentStatus(PaymentStatusEnum.valueOf(rs.getString(PY)));
        purchaseOrder.addOrderState(PurchaseOrderStateEnum.valueOf(rs.getString(PS)));
        purchaseOrder.setDeliveryAddress(rs.getString(DA));
        purchaseOrder.setRatingCount(rs.getInt(RA));
        purchaseOrder.setReview(rs.getString(RV));
        purchaseOrder.setTokenNumber(rs.getInt(TN));
        purchaseOrder.setStoreDiscount(rs.getInt(SD));
        purchaseOrder.setOrderPrice(rs.getString(OP));
        purchaseOrder.setBusinessType(BusinessTypeEnum.valueOf(rs.getString(BT)));
        purchaseOrder.setServerName(rs.getString(SN));
        purchaseOrder.setServiceBeginTime(rs.getTimestamp(SB));
        purchaseOrder.setServiceEndTime(rs.getTimestamp(SE));
        purchaseOrder.setTransactionId(rs.getString(TI));
        purchaseOrder.setDisplayName(rs.getString(DN));
        purchaseOrder.setAdditionalNote(rs.getString(AN));

        purchaseOrder.setVersion(rs.getInt(V));
        purchaseOrder.setCreateAndUpdate(rs.getTimestamp(U));
        purchaseOrder.setCreated(rs.getTimestamp(C));
        if (rs.getInt(A) > 0) {
            purchaseOrder.active();
        } else {
            purchaseOrder.inActive();
        }

        if (rs.getInt(D) > 0) {
            purchaseOrder.markAsDeleted();
        }

        return purchaseOrder;
    }
}
