package com.token.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.token.domain.QueueEntity;
import com.token.domain.mapper.QueueRowMapper;

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
    private static final String insert =
            "INSERT INTO QUEUE (ID, QR, DID, RID, TN, DN, QS, NS, V, U, C, A, D)" +
                    " VALUES " +
                    "(:id,:qr,:did,:rid,:tn,:dn,:qs,:ns,:v,:u,:c,:a,:d)";

    private static final String findByRid =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE RID = ?";

    private static final String findByDid =
            "SELECT ID, QR, DID, RID, TN, DN, QS, NS, V, U, C, A, D" +
                    " FROM " +
                    "QUEUE WHERE DID = ?";

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

    public List<QueueEntity> findByDid(String did) {
        return jdbcTemplate.query(findByDid, new Object[]{did}, new QueueRowMapper());
    }

    public List<QueueEntity> findByRid(String rid) {
        return jdbcTemplate.query(findByRid, new Object[]{rid}, new QueueRowMapper());
    }
}
