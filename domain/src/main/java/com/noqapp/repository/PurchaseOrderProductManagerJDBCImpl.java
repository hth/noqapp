package com.noqapp.repository;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.mapper.PurchaseOrderProductRowMapper;

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
public class PurchaseOrderProductManagerJDBCImpl implements PurchaseOrderProductManagerJDBC {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderProductManagerJDBCImpl.class);

    private static final String insert =
        "INSERT INTO PURCHASE_ORDER_PRODUCT (ID, PN, PP, PD, PT, UV, UM, PS, PQ, PO, QID, BS, BN, QR, BT, V, U, C, A, D)" +
            " VALUES " +
            "(:id,:pn,:pp,:pd,:pt,:uv,:um,:ps,:pq,:po,:qid,:bs,:bn,:qr,:bt,:v,:u,:c,:a,:d)";

    private static final String delete = "DELETE FROM PURCHASE_ORDER_PRODUCT WHERE ID = :id";
    private static final String delete_by_purchaseOrder = "DELETE FROM PURCHASE_ORDER_PRODUCT WHERE PO = :po";
    private static final String delete_by_purchaseOrderId = "DELETE FROM PURCHASE_ORDER_PRODUCT WHERE PO = ?";

    private static final String query_by_purchaseOrder =
            "SELECT ID, PN, PP, PD, PT, UV, UM, PS, PQ, PO, QID, BS, BN, QR, BT, V, U, C, A, D" +
                    " FROM " +
                    "PURCHASE_ORDER_PRODUCT WHERE PO = ? " +
                    "ORDER BY C DESC";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PurchaseOrderProductManagerJDBCImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void batchPurchaseOrderProducts(List<PurchaseOrderProductEntity> purchaseOrderProducts) {
        try {
            SqlParameterSource[] maps = new SqlParameterSource[purchaseOrderProducts.size()];

            int i = 0;
            for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
                LOG.info("Added purchaseOrderProducts id={}", purchaseOrderProduct.getId());

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("id", purchaseOrderProduct.getId());
                namedParameters.addValue("pn", purchaseOrderProduct.getProductName());
                namedParameters.addValue("pp", purchaseOrderProduct.getProductPrice());
                namedParameters.addValue("pd", purchaseOrderProduct.getProductDiscount());
                namedParameters.addValue("pt", purchaseOrderProduct.getProductType());
                namedParameters.addValue("uv", purchaseOrderProduct.getUnitValue());
                namedParameters.addValue("um", purchaseOrderProduct.getUnitOfMeasurement());
                namedParameters.addValue("ps", purchaseOrderProduct.getPackageSize());
                namedParameters.addValue("pq", purchaseOrderProduct.getProductQuantity());
                namedParameters.addValue("po", purchaseOrderProduct.getPurchaseOrderId());
                namedParameters.addValue("qid", purchaseOrderProduct.getQueueUserId());
                namedParameters.addValue("bs", purchaseOrderProduct.getBizStoreId());
                namedParameters.addValue("bn", purchaseOrderProduct.getBizNameId());
                namedParameters.addValue("qr", purchaseOrderProduct.getCodeQR());
                namedParameters.addValue("bt", purchaseOrderProduct.getBusinessType().name());

                namedParameters.addValue("v", purchaseOrderProduct.getVersion());
                namedParameters.addValue("u", purchaseOrderProduct.getUpdated());
                namedParameters.addValue("c", purchaseOrderProduct.getCreated());
                namedParameters.addValue("a", purchaseOrderProduct.isActive() ? 1 : 0);
                namedParameters.addValue("d", purchaseOrderProduct.isDeleted() ? 1 : 0);

                maps[i] = namedParameters;
                i++;
            }

            int[] rowUpdated = namedParameterJdbcTemplate.batchUpdate(insert, maps);
            LOG.info("Insert count={} rowUpdated={}", maps.length, rowUpdated.length);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed batch update count={} reason={}", purchaseOrderProducts.size(), e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public void rollbackPurchaseOrderProducts(List<PurchaseOrderProductEntity> purchaseOrderProducts) {
        try {
            SqlParameterSource[] maps = new SqlParameterSource[purchaseOrderProducts.size()];

            int i = 0;
            for (PurchaseOrderProductEntity purchaseOrderProduct : purchaseOrderProducts) {
                LOG.info("Delete purchaseOrderProduct id={}", purchaseOrderProduct.getId());

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("id", purchaseOrderProduct.getId());

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
    public void rollbackPurchaseOrders(List<PurchaseOrderEntity> purchaseOrders) {
        try {
            SqlParameterSource[] maps = new SqlParameterSource[purchaseOrders.size()];

            int i = 0;
            for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
                LOG.info("Delete purchaseOrder id={}", purchaseOrder.getId());

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("po", purchaseOrder.getId());

                maps[i] = namedParameters;
                i++;
            }

            int[] rowUpdated = namedParameterJdbcTemplate.batchUpdate(delete_by_purchaseOrder, maps);
            LOG.info("Insert count={} rowUpdated={}", maps.length, rowUpdated.length);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed batch delete reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseOrderProductEntity> getByPurchaseOrderId(String purchaseOrderId) {
        LOG.info("Fetch historical order by qid={}", purchaseOrderId);
        return jdbcTemplate.query(query_by_purchaseOrder, new Object[]{purchaseOrderId}, new PurchaseOrderProductRowMapper());
    }

    @Override
    public void deleteByPurchaseOrderId(String purchaseOrderId) {
        jdbcTemplate.update(delete_by_purchaseOrderId, purchaseOrderId);
    }
}
