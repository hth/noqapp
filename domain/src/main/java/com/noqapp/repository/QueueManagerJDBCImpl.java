package com.noqapp.repository;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.noqapp.domain.QueueEntity;
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
            "INSERT INTO QUEUE (ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, ST, V, U, C, A, D)" +
                    " VALUES " +
                    "(:id,:qr,:did,:rid,:tn,:dn,:qs,:ns,:ra,:hr,:st,:v,:u,:c,:a,:d)";

    private static final String findByRid =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, ST, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE RID = ?";

    private static final String findByRidAndByLastUpdated =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, ST, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE RID = ? AND U >= ?";

    private static final String findByDid =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, ST, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE DID = ?";

    private static final String findByDidAndByLastUpdated =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, RA, HR, ST, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE DID = ? AND U >= ?";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public QueueManagerJDBCImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void batchQueue(List<QueueEntity> queues) {
        SqlParameterSource[] maps = new SqlParameterSource[queues.size()];

        int i = 0;
        for (QueueEntity queue : queues) {
            MapSqlParameterSource namedParameters = new MapSqlParameterSource();
            namedParameters.addValue("id", queue.getId());
            namedParameters.addValue("qr", queue.getCodeQR());
            namedParameters.addValue("did", queue.getDid());
            namedParameters.addValue("rid", queue.getRid());
            namedParameters.addValue("tn", queue.getTokenNumber());
            namedParameters.addValue("dn", queue.getDisplayName());
            namedParameters.addValue("qs", queue.getQueueUserState().getName());
            namedParameters.addValue("ns", queue.isNotifiedOnService() ? 1 : 0);
            namedParameters.addValue("ra", queue.getRatingCount());
            namedParameters.addValue("hr", queue.getHoursSaved());
            namedParameters.addValue("st", queue.getServicedTime());

            namedParameters.addValue("v", queue.getVersion());
            namedParameters.addValue("u", queue.getUpdated());
            namedParameters.addValue("c", queue.getCreated());
            namedParameters.addValue("a", queue.isActive() ? 1 : 0);
            namedParameters.addValue("d", queue.isDeleted() ? 1 : 0);

            maps[i] = namedParameters;
            i++;
        }

        namedParameterJdbcTemplate.batchUpdate(insert, maps);
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
        return jdbcTemplate.query(findByRidAndByLastUpdated, new Object[]{rid, lastAccessed}, new QueueRowMapper());
    }

    @Override
    public boolean reviewService(String codeQR, String did, String rid, int ratingCount, int hoursSaved) {
        if (StringUtils.isNotBlank(rid)) {
            return this.jdbcTemplate.update(
                    "UPDATE QUEUE set RA = ?, HA = ? where QR = ?, DID = ?, RID = ? AND RA <> 0",
                    ratingCount, hoursSaved, codeQR, did, rid) > 0;
        } else {
            return this.jdbcTemplate.update(
                    "UPDATE QUEUE set RA = ?, HA = ? where QR = ?, DID = ? AND RA <> 0",
                    ratingCount, hoursSaved, codeQR, did) > 0;
        }
    }
}
