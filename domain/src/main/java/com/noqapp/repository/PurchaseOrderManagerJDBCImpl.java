package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.annotation.CustomTransactional;
import com.noqapp.domain.mapper.PurchaseOrderRowMapper;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.PaymentStatusEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TransactionViaEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
        "INSERT INTO PURCHASE_ORDER (ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D)" +
            " VALUES " +
            "(:id,:qid,:bs,:bn,:qr,:did,:dm,:pm,:py,:ps,:ai,:ra,:rv,:st,:tn,:dt,:sd,:pp,:op,:ta,:gt,:bt,:pq,:fq,:cq,:ci,:sn,:sb,:se,:ti,:tr,:tm,:tv,:dn,:an,:v,:u,:c,:a,:d)";

    private static final String delete = "DELETE FROM PURCHASE_ORDER WHERE ID = :id";
    private static final String delete_by_id = "DELETE FROM PURCHASE_ORDER WHERE ID = ?";

    private static final String query_by_qid =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE QID = ? AND BT <> ?" +
            "ORDER BY C DESC";

    private static final String query_by_qid_and_transactionId =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE QID = ? AND TI = ? ";

    private static final String query_by_transactionId =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE TI = ? ";

    private static final String query_where_coupon_applied =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE BN = ? AND CI <> ? ";

    private static final String query_by_qid_where_ps =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE QID = ? AND PS = ? " +
            "ORDER BY C DESC";

    private static final String findReviewsByCodeQR =
        "SELECT RA, RV, QID, C" +
            " FROM " +
            "PURCHASE_ORDER WHERE QR = ? " +
            "AND RA <> 0 " +
            "AND " +
            "C BETWEEN NOW() - INTERVAL ? DAY AND NOW() " +
            "ORDER BY C DESC";

    /** Group by Date and Payment Status on Paid and Pending Payment. */
    private static final String computeEarning =
        "SELECT SUM(OP), SUM(TA), SUM(GT), PY, DATE(C) DateOnly" +
        " FROM " +
        "PURCHASE_ORDER WHERE BN = ? AND (PS LIKE ? or PS LIKE ?) AND TV = ? AND C BETWEEN NOW() - INTERVAL ? DAY AND NOW() " +
        "GROUP BY DateOnly, PY ORDER BY DateOnly DESC";

    private static final String transactionOnDay =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE BN = ? AND C BETWEEN ? AND ? " +
            "ORDER BY TN";

    private static final String qidAndBizNameId =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE QID = ? AND BN = ? " +
            "ORDER BY C DESC";

    private static final String query_by_codeQR =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE QR = ? AND C BETWEEN NOW() - INTERVAL ? DAY AND NOW() " +
            "ORDER BY C DESC";

    private static final String query_by_transactionId_and_storeId =
        "SELECT ID, QID, BS, BN, QR, DID, DM, PM, PY, PS, AI, RA, RV, ST, TN, DT, SD, PP, OP, TA, GT, BT, PQ, FQ, CQ, CI, SN, SB, SE, TI, TR, TM, TV, DN, AN, V, U, C, A, D" +
            " FROM " +
            "PURCHASE_ORDER WHERE TI = ? AND BS = ? ";

    private static final String clientVisitedStoreDate =
        "SELECT C FROM PURCHASE_ORDER WHERE QR = ? AND QID = ? AND PY = ? ORDER BY C DESC LIMIT 1";

    private static final String checkIfClientVisitedStore =
        "SELECT EXISTS(SELECT 1 FROM PURCHASE_ORDER WHERE QR = ? AND QID = ? AND PY = ? LIMIT 1)";

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
                namedParameters.addValue("did", purchaseOrder.getDid());
                namedParameters.addValue("dm", purchaseOrder.getDeliveryMode().name());
                namedParameters.addValue("pm", null == purchaseOrder.getPaymentMode() ? null : purchaseOrder.getPaymentMode().name());
                namedParameters.addValue("py", null == purchaseOrder.getPaymentStatus() ? PaymentStatusEnum.PP.name() : purchaseOrder.getPaymentStatus().name());
                namedParameters.addValue("ps", purchaseOrder.getPresentOrderState().name());
                namedParameters.addValue("ai", purchaseOrder.getUserAddressId());
                namedParameters.addValue("ra", purchaseOrder.getRatingCount());
                namedParameters.addValue("rv", purchaseOrder.getReview());
                namedParameters.addValue("st", null == purchaseOrder.getSentimentType() ? null : purchaseOrder.getSentimentType().name());
                namedParameters.addValue("tn", purchaseOrder.getTokenNumber());
                namedParameters.addValue("dt", purchaseOrder.getDisplayToken());
                namedParameters.addValue("sd", purchaseOrder.getStoreDiscount());
                namedParameters.addValue("pp", purchaseOrder.getPartialPayment());
                namedParameters.addValue("op", purchaseOrder.getOrderPrice());
                namedParameters.addValue("ta", purchaseOrder.getTax());
                namedParameters.addValue("gt", purchaseOrder.getGrandTotal());
                namedParameters.addValue("bt", purchaseOrder.getBusinessType().name());
                namedParameters.addValue("pq", purchaseOrder.getPartialPaymentAcceptedByQid());
                namedParameters.addValue("fq", purchaseOrder.getFullPaymentAcceptedByQid());
                namedParameters.addValue("cq", purchaseOrder.getCouponAddedByQid());
                namedParameters.addValue("ci", purchaseOrder.getCouponId());
                namedParameters.addValue("sn", purchaseOrder.getServerName());
                namedParameters.addValue("sb", purchaseOrder.getServiceBeginTime());
                namedParameters.addValue("se", purchaseOrder.getServiceEndTime());
                namedParameters.addValue("ti", purchaseOrder.getTransactionId());
                namedParameters.addValue("tr", purchaseOrder.getTransactionReferenceId());
                namedParameters.addValue("tm", purchaseOrder.getTransactionMessage());
                namedParameters.addValue("tv", null == purchaseOrder.getTransactionVia() ? TransactionViaEnum.U.name() : purchaseOrder.getTransactionVia().name());
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
    public List<PurchaseOrderEntity> getByQid(String qid, BusinessTypeEnum ignoreBusinessType) {
        LOG.info("Fetch historical order by qid={} ignoring {}", qid, ignoreBusinessType);
        return jdbcTemplate.query(query_by_qid, new Object[]{qid, ignoreBusinessType.getName()}, new PurchaseOrderRowMapper());
    }

    @Override
    @CustomTransactional
    public boolean reviewService(String codeQR, int token, String qid, int ratingCount, String review, SentimentTypeEnum sentimentType) {
        try {
            return this.jdbcTemplate.update(
                "UPDATE PURCHASE_ORDER SET RA = ?, RV = ?, ST = ? WHERE QR = ? AND QID = ? AND TN = ?",
                ratingCount, review, sentimentType == null ? null : sentimentType.name(), codeQR, qid, token) > 0;
        } catch (Exception e) {
            LOG.error("Failed order review update codeQR={} token={} sentimentType={} qid={} ratingCount={} review={} reason={}",
                codeQR, token, sentimentType, qid, ratingCount, review, e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseOrderEntity> findReviews(String codeQR, int reviewLimitedToDays) {
        LOG.info("Fetch order review by codeQR={} limitedToDays={}", codeQR, reviewLimitedToDays);
        return jdbcTemplate.query(
            findReviewsByCodeQR,
            new Object[]{codeQR, reviewLimitedToDays},
            (rs, rowNum) -> {
                PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(rs.getString(3), null, null, null)
                    .setRatingCount(rs.getInt(1))
                    .setReview(rs.getString(2));
                purchaseOrder.setCreated(rs.getDate(4));
                return purchaseOrder;
            });
    }

    @Override
    public List<PurchaseOrderEntity> findAllOrderWithState(String qid, PurchaseOrderStateEnum purchaseOrderState) {
        LOG.info("Fetch historical order by qid={} with purchaseOrderState={}", qid, purchaseOrderState);
        return jdbcTemplate.query(query_by_qid_where_ps, new Object[]{qid, purchaseOrderState}, new PurchaseOrderRowMapper());
    }

    @Override
    public PurchaseOrderEntity findOrderByTransactionId(String qid, String transactionId) {
        try {
            return jdbcTemplate.queryForObject(query_by_qid_and_transactionId, new Object[]{qid, transactionId}, new PurchaseOrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            //TODO fix this error or query
            LOG.error("Failed to find transactionId={} qid={} reason={}", transactionId, qid, e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public PurchaseOrderEntity findOrderByTransactionId(String transactionId) {
        try {
            return jdbcTemplate.queryForObject(query_by_transactionId, new Object[]{transactionId}, new PurchaseOrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            //TODO fix this error or query
            LOG.error("Failed to find transactionId={} reason={}", transactionId, e.getLocalizedMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        jdbcTemplate.update(delete_by_id, id);
    }

    @Override
    public List<PurchaseOrderEntity> computeEarning(String bizNameId, TransactionViaEnum transactionVia, int durationInDays) {
        return jdbcTemplate.query(
            computeEarning,
            new Object[]{bizNameId, PurchaseOrderStateEnum.OD.name(), PurchaseOrderStateEnum.PO.name(), transactionVia.name(), durationInDays},
            (rs, rowNum) -> {
                PurchaseOrderEntity purchaseOrder = new PurchaseOrderEntity(null, null, bizNameId, null)
                    .setTransactionVia(transactionVia)
                    .setOrderPrice(rs.getString(1))
                    .setTax(rs.getString(2))
                    .setGrandTotal(rs.getString(3))
                    .setPaymentStatus(PaymentStatusEnum.valueOf(rs.getString(4)));
                purchaseOrder.setCreated(rs.getDate(5));
                return purchaseOrder;
            }
        );
    }

    @Override
    public List<PurchaseOrderEntity> findAllOrderByCodeQR(String codeQR, int durationInDays) {
        return jdbcTemplate.query(query_by_codeQR, new Object[]{codeQR, durationInDays}, new PurchaseOrderRowMapper());
    }

    @Override
    public PurchaseOrderEntity findByTransactionIdAndBizStore(String transactionId, String bizStoreId) {
        try {
            return jdbcTemplate.queryForObject(query_by_transactionId_and_storeId, new Object[]{transactionId, bizStoreId}, new PurchaseOrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            //TODO fix this error or query
            LOG.error("Failed to find transactionId={} bizStoreId={} reason={}", transactionId, bizStoreId, e.getLocalizedMessage(), e);
            return null;
        }
    }

    @Override
    public Date clientVisitedStoreAndServicedDate(String codeQR, String qid) {
        LOG.info("Fetch payment made by codeQR={} qid={}", codeQR, qid);
        try {
            return jdbcTemplate.queryForObject(clientVisitedStoreDate, new Object[]{codeQR, qid, PaymentStatusEnum.PA.getName()}, Date.class);
        } catch (EmptyResultDataAccessException e) {
            //TODO fix this error or query
            return null;
        }
    }

    @Override
    public boolean hasClientVisitedThisStoreAndServiced(String codeQR, String qid) {
        LOG.info("Fetch history by codeQR={} qid={}", codeQR, qid);
        return jdbcTemplate.queryForObject(checkIfClientVisitedStore, new Object[]{codeQR, qid, PaymentStatusEnum.PA.getName()}, Boolean.class);
    }

    @Override
    public List<PurchaseOrderEntity> findPurchaseMadeUsingCoupon(String bizNameId) {
        LOG.info("Fetch historical order by bizNameId={}", bizNameId);
        return jdbcTemplate.query(query_where_coupon_applied, new Object[]{bizNameId}, new PurchaseOrderRowMapper());
    }

    @Override
    public List<PurchaseOrderEntity> findTransactionBetweenDays(String bizNameId, String from, String until) {
        return jdbcTemplate.query(transactionOnDay, new Object[]{bizNameId, from, until}, new PurchaseOrderRowMapper());
    }

    @Override
    public List<PurchaseOrderEntity> findByQidAndBizNameId(String qid, String bizNameId) {
        return jdbcTemplate.query(qidAndBizNameId, new Object[]{qid, bizNameId}, new PurchaseOrderRowMapper());
    }
}
