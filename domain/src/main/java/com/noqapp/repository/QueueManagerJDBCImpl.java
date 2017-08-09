package com.noqapp.repository;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.CustomTransactional;
import com.noqapp.domain.mapper.QueueRowMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

/**
 * User: hitender
 * Date: 3/9/17 9:57 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class QueueManagerJDBCImpl implements QueueManagerJDBC {
    private static final Logger LOG = LoggerFactory.getLogger(QueueManagerJDBCImpl.class);

    private static final String insert =
            "INSERT INTO QUEUE (ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, SN, SB, SE, V, U, C, A, D)" +
                    " VALUES " +
                    "(:id,:qr,:did,:rid,:tn,:dn,:qs,:ns,:ra,:hr,:sn,:sb,:se,:v,:u,:c,:a,:d)";

    private static final String delete = "DELETE FROM QUEUE WHERE ID = :id";

    private static final String findByRid =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, SN, SB, SE, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE RID = ?";

    private static final String findByRidAndByLastUpdated =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, SN, SB, SE, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE RID = ? AND U >= ?";

    private static final String findByDid =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, SN, SB, SE, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE DID = ?";

    private static final String findByDidAndByLastUpdated =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, SN, SB, SE, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE DID = ? AND U >= ?";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public QueueManagerJDBCImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Any RuntimeException triggers rollback, and any checked Exception does not. Hence added @CustomTransactional */
    @Override
    @CustomTransactional
    public void batchQueues(List<QueueEntity> queues) {
        try {
            SqlParameterSource[] maps = new SqlParameterSource[queues.size()];

            int i = 0;
            for (QueueEntity queue : queues) {
                LOG.info("Added queue id={}", queue.getId());

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("id", queue.getId());
                namedParameters.addValue("qr", queue.getCodeQR());
                namedParameters.addValue("did", queue.getDid());
                namedParameters.addValue("rid", queue.getQueueUserId());
                namedParameters.addValue("tn", queue.getTokenNumber());
                namedParameters.addValue("dn", queue.getDisplayName());
                namedParameters.addValue("qs", queue.getQueueUserState().getName());
                namedParameters.addValue("ns", queue.isNotifiedOnService() ? 1 : 0);
                namedParameters.addValue("ra", queue.getRatingCount());
                namedParameters.addValue("hr", queue.getHoursSaved());
                namedParameters.addValue("sn", queue.getServerName());
                namedParameters.addValue("sb", queue.getServiceBeginTime());
                namedParameters.addValue("se", queue.getServiceEndTime());

                namedParameters.addValue("v", queue.getVersion());
                namedParameters.addValue("u", queue.getUpdated());
                namedParameters.addValue("c", queue.getCreated());
                namedParameters.addValue("a", queue.isActive() ? 1 : 0);
                namedParameters.addValue("d", queue.isDeleted() ? 1 : 0);

                maps[i] = namedParameters;
                i++;
            }

            int[] rowUpdated = namedParameterJdbcTemplate.batchUpdate(insert, maps);
            LOG.info("Insert count={} rowUpdated={}", maps.length, rowUpdated);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed batch update reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public void rollbackQueues(List<QueueEntity> queues) {
        try {
            SqlParameterSource[] maps = new SqlParameterSource[queues.size()];

            int i = 0;
            for (QueueEntity queue : queues) {
                LOG.info("Delete queue id={}", queue.getId());

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("id", queue.getId());

                maps[i] = namedParameters;
                i++;
            }

            int[] rowUpdated = namedParameterJdbcTemplate.batchUpdate(delete, maps);
            LOG.info("Deleted count={} rowUpdated={}", maps.length, rowUpdated);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed batch delete reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public List<QueueEntity> getByDid(String did) {
        LOG.info("Fetch history by did={}", did);
        try {
            return jdbcTemplate.query(findByDid, new Object[]{did}, new QueueRowMapper());
        } catch(Exception e) {
            LOG.error("Error did={} reason={}", did, e.getLocalizedMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<QueueEntity> getByDid(String did, Date lastAccessed) {
        LOG.info("Fetch history by did={} lastAccessed={}", did, lastAccessed);
        try {
            return jdbcTemplate.query(findByDidAndByLastUpdated, new Object[]{did, lastAccessed}, new QueueRowMapper());
        } catch(Exception e) {
            LOG.error("Error did={} lastAccessed={} reason={}", did, lastAccessed, e.getLocalizedMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<QueueEntity> getByRid(String rid) {
        return jdbcTemplate.query(findByRid, new Object[]{rid}, new QueueRowMapper());
    }

    @Override
    public List<QueueEntity> getByRid(String rid, Date lastAccessed) {
        LOG.info("Fetch history by rid={} lastAccessed={}", rid, lastAccessed);
        return jdbcTemplate.query(findByRidAndByLastUpdated, new Object[]{rid, lastAccessed}, new QueueRowMapper());
    }

    @Override
    @CustomTransactional
    public boolean reviewService(String codeQR, int token, String did, String rid, int ratingCount, int hoursSaved) {
        try {
            if (StringUtils.isNotBlank(rid)) {
                return this.jdbcTemplate.update(
                        "UPDATE QUEUE SET RA = ?, HA = ? WHERE QR = ? AND DID = ? AND RID = ? AND TN = ? AND RA <> 0",
                        ratingCount, hoursSaved, codeQR, did, rid, token) > 0;
            } else {
                return this.jdbcTemplate.update(
                        "UPDATE QUEUE SET RA = ?, HA = ? WHERE QR = ? AND DID = ? AND TN = ? AND RA <> 0",
                        ratingCount, hoursSaved, codeQR, did, token) > 0;
            }
        } catch (Exception e) {
            LOG.error("Failed review update codeQR={} token={} did={} rid={} ratingCount={} hoursSaved={} reason={}",
                    codeQR, token, did, rid, ratingCount, hoursSaved, e.getLocalizedMessage(), e);
            throw e;
        }
    }
}
