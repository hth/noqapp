package com.noqapp.domain.mapper;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PaymentModeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.TransactionViaEnum;

import org.apache.commons.lang3.StringUtils;

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
    private static final int PM = 7;
    private static final int PY = 8;
    private static final int PS = 9;
    private static final int DA = 10;
    private static final int RA = 11;
    private static final int RV = 12;
    private static final int TN = 13;

    private static final int SD = 14;
    private static final int PP = 15;
    private static final int OP = 16;
    private static final int BT = 17;
    private static final int SN = 18;
    private static final int SB = 19;
    private static final int SE = 20;
    private static final int TI = 21;
    private static final int TR = 22;
    private static final int TM = 23;
    private static final int TV = 24;
    private static final int DN = 25;
    private static final int AN = 26;

    private static final int V = 27;
    private static final int U = 28;
    private static final int C = 29;
    private static final int A = 30;
    private static final int D = 31;

    @Override
    public PurchaseOrderEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(
                rs.getString(QID),
                rs.getString(BS),
                rs.getString(BN),
                rs.getString(QR)
        );
        purchaseOrder.setId(rs.getString(ID));
        purchaseOrder.setDeliveryMode(DeliveryModeEnum.valueOf(rs.getString(DM)));
        purchaseOrder.setPaymentMode(StringUtils.isBlank(rs.getString(PM)) ? null : PaymentModeEnum.valueOf(rs.getString(PM)));
        purchaseOrder.setPaymentStatus(PaymentStatusEnum.valueOf(rs.getString(PY)));
        purchaseOrder.addOrderState(PurchaseOrderStateEnum.valueOf(rs.getString(PS)));
        purchaseOrder.setDeliveryAddress(rs.getString(DA));
        purchaseOrder.setRatingCount(rs.getInt(RA));
        purchaseOrder.setReview(rs.getString(RV));
        purchaseOrder.setTokenNumber(rs.getInt(TN));
        purchaseOrder.setStoreDiscount(rs.getInt(SD));
        purchaseOrder.setPartialPayment(rs.getString(PP));
        purchaseOrder.setOrderPrice(rs.getString(OP));
        purchaseOrder.setBusinessType(BusinessTypeEnum.valueOf(rs.getString(BT)));
        purchaseOrder.setServerName(rs.getString(SN));
        purchaseOrder.setServiceBeginTime(rs.getTimestamp(SB));
        purchaseOrder.setServiceEndTime(rs.getTimestamp(SE));
        purchaseOrder.setTransactionId(rs.getString(TI));
        purchaseOrder.setTransactionReferenceId(rs.getString(TR));
        purchaseOrder.setTransactionMessage(rs.getString(TM));
        purchaseOrder.setTransactionVia(TransactionViaEnum.valueOf(rs.getString(TV)));
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
