package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.annotation.CustomTransactional;
import com.noqapp.domain.mapper.PurchaseOrderRowMapper;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.TransactionViaEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.sql.DataSource;

/**
 * hitender
 * 9/30/18 6:02 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class PurchaseOrderManagerJDBCImpl implements PurchaseOrderManagerJDBC {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderProductManagerJDBCImpl.class);

    private static final String insert =
        "INSERT INTO PURCHASE_ORDER (ID, QID, BS, BN, QR, DM, PM, PY, PS, DA, RA, RV, TN, SD, PP, OP, BT, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D)" +
            " VALUES " +
            "(:id,:qid,:bs,:bn,:qr,:dm,:pm,:py,:ps,:da,:ra,:rv,:tn,:sd,:pp,:op,:bt,:sn,:sb,:se,:ti,:tr,:tm,:tv,:dn,:an,:v,:u,:c,:a,:d)";

    private static final String delete = "DELETE FROM PURCHASE_ORDER WHERE ID = :id";
    private static final String delete_by_id = "DELETE FROM PURCHASE_ORDER WHERE ID = ?";

    private static final String query_by_qid =
        "SELECT ID, QID, BS, BN, QR, DM, PM, PY, PS, DA, RA, RV, TN, SD, PP, OP, BT, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE QID = ? " +
            "ORDER BY C DESC";

    private static final String query_by_qid_and_transactionId =
        "SELECT ID, QID, BS, BN, QR, DM, PM, PY, PS, DA, RA, RV, TN, SD, PP, OP, BT, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE QID = ? AND TI = ? ";

    private static final String query_by_qid_where_ps =
        "SELECT ID, QID, BS, BN, QR, DM, PM, PY, PS, DA, RA, RV, TN, SD, PP, OP, BT, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE QID = ? AND PS = ?" +
            "ORDER BY C DESC";

    private static final String findReviewsByCodeQR =
        "SELECT RA, RV, QID" +
            " FROM " +
            "PURCHASE_ORDER WHERE QR = ? " +
            "AND RA <> 0 " +
            "AND " +
            "C BETWEEN NOW() - INTERVAL ? DAY AND NOW() " +
            "ORDER BY C DESC";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PurchaseOrderManagerJDBCImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void batchPurchaseOrder(List<PurchaseOrderEntity> purchaseOrders) {
        try {
            SqlParameterSource[] maps = new SqlParameterSource[purchaseOrders.size()];

            int i = 0;
            for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
                LOG.info("Added purchaseOrder id={}", purchaseOrder.getId());

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("id", purchaseOrder.getId());
                namedParameters.addValue("qid", purchaseOrder.getQueueUserId());
                namedParameters.addValue("bs", purchaseOrder.getBizStoreId());
                namedParameters.addValue("bn", purchaseOrder.getBizNameId());
                namedParameters.addValue("qr", purchaseOrder.getCodeQR());
                namedParameters.addValue("dm", purchaseOrder.getDeliveryMode().name());
                namedParameters.addValue("pm", null == purchaseOrder.getPaymentMode() ? null : purchaseOrder.getPaymentMode().name());
                namedParameters.addValue("py", null == purchaseOrder.getPaymentStatus() ? PaymentStatusEnum.PP : purchaseOrder.getPaymentStatus().name());
                namedParameters.addValue("ps", purchaseOrder.getPresentOrderState().name());
                namedParameters.addValue("da", purchaseOrder.getDeliveryAddress());
                namedParameters.addValue("ra", purchaseOrder.getRatingCount());
                namedParameters.addValue("rv", purchaseOrder.getReview());
                namedParameters.addValue("tn", purchaseOrder.getTokenNumber());
                namedParameters.addValue("sd", purchaseOrder.getStoreDiscount());
                namedParameters.addValue("pp", purchaseOrder.getPartialPayment());
                namedParameters.addValue("op", purchaseOrder.getOrderPrice());
                namedParameters.addValue("bt", purchaseOrder.getBusinessType().name());
                namedParameters.addValue("sn", purchaseOrder.getServerName());
                namedParameters.addValue("sb", purchaseOrder.getServiceBeginTime());
                namedParameters.addValue("se", purchaseOrder.getServiceEndTime());
                namedParameters.addValue("ti", purchaseOrder.getTransactionId());
                namedParameters.addValue("tr", purchaseOrder.getTransactionReferenceId());
                namedParameters.addValue("tm", purchaseOrder.getTransactionMessage());
                namedParameters.addValue("tv", null == purchaseOrder.getTransactionVia() ? TransactionViaEnum.U : purchaseOrder.getTransactionVia().name());
                namedParameters.addValue("dn", purchaseOrder.getDisplayName());
                namedParameters.addValue("an", purchaseOrder.getAdditionalNote());

                namedParameters.addValue("v", purchaseOrder.getVersion());
                namedParameters.addValue("u", purchaseOrder.getUpdated());
                namedParameters.addValue("c", purchaseOrder.getCreated());
                namedParameters.addValue("a", purchaseOrder.isActive() ? 1 : 0);
                namedParameters.addValue("d", purchaseOrder.isDeleted() ? 1 : 0);

                maps[i] = namedParameters;
                i++;
            }

            int[] rowUpdated = namedParameterJdbcTemplate.batchUpdate(insert, maps);
            LOG.info("Insert count={} rowUpdated={}", maps.length, rowUpdated.length);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed batch update reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public void rollbackPurchaseOrder(List<PurchaseOrderEntity> purchaseOrders) {
        try {
            SqlParameterSource[] maps = new SqlParameterSource[purchaseOrders.size()];

            int i = 0;
            for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
                LOG.info("Delete queue id={}", purchaseOrder.getId());

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("id", purchaseOrder.getId());

                maps[i] = namedParameters;
                i++;
            }

            int[] rowUpdated = namedParameterJdbcTemplate.batchUpdate(delete, maps);
            LOG.info("Insert count={} rowUpdated={}", maps.length, rowUpdated.length);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed batch delete reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseOrderEntity> getByQid(String qid) {
        LOG.info("Fetch historical order by qid={}", qid);
        return jdbcTemplate.query(query_by_qid, new Object[]{qid}, new PurchaseOrderRowMapper());
    }

    //TODO add sentiments "ST"
    @Override
    @CustomTransactional
    public boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, String review) {
        try {
            return this.jdbcTemplate.update(
                "UPDATE PURCHASE_ORDER SET RA = ?, RV = ? WHERE QR = ? AND DID = ? AND QID = ? AND TN = ? AND RA = 0",
                ratingCount, review, codeQR, did, qid, token) > 0;
        } catch (Exception e) {
            LOG.error("Failed order review update codeQR={} token={} did={} qid={} ratingCount={} review={} reason={}",
                codeQR, token, did, qid, ratingCount, review, e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseOrderEntity> findReviews(String codeQR, int reviewLimitedToDays) {
        LOG.info("Fetch order review by codeQR={} limitedToDays={}", codeQR, reviewLimitedToDays);
        return jdbcTemplate.query(
            findReviewsByCodeQR,
            new Object[]{codeQR, reviewLimitedToDays},
            (rs, rowNum) -> new PurchaseOrderEntity(rs.getString(3), null, null, null)
                .setRatingCount(rs.getInt(1))
                .setReview(rs.getString(2)));
    }

    @Override
    public List<PurchaseOrderEntity> findAllOrderWithState(String qid, PurchaseOrderStateEnum purchaseOrderState) {
        LOG.info("Fetch historical order by qid={} with purchaseOrderState={}", qid, purchaseOrderState);
        return jdbcTemplate.query(query_by_qid_where_ps, new Object[]{qid, purchaseOrderState}, new PurchaseOrderRowMapper());
    }

    @Override
    public PurchaseOrderEntity findOrderByTransactionId(String qid, String transactionId) {
        return jdbcTemplate.queryForObject(query_by_qid_and_transactionId, new Object[]{qid, transactionId}, new PurchaseOrderRowMapper());
    }

    @Override
    public void deleteById(String id) {
        jdbcTemplate.update(delete_by_id, id);
    }
}
