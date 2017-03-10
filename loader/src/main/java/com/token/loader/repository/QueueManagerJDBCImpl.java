package com.token.loader.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.token.domain.QueueEntity;

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
    private static final String query =
            "INSERT INTO QUEUE (QR, DID, TN, DN, QS, NS, V, U, C, A, D)" +
                    " VALUES " +
                    "(:qr,:did,:tn,:dn,:qs,:ns,:v,:u,:c,:a,:d)";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QueueManagerJDBCImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void batchQueue(List<QueueEntity> queues) {
        SqlParameterSource[] maps = new SqlParameterSource[queues.size()];

        int i = 0;
        for(QueueEntity queue : queues) {
            MapSqlParameterSource namedParameters = new MapSqlParameterSource();
            namedParameters.addValue("qr", queue.getCodeQR());
            namedParameters.addValue("did", queue.getDid());
            namedParameters.addValue("tn", queue.getTokenNumber());
            namedParameters.addValue("dn", queue.getDisplayName());
            namedParameters.addValue("qs", queue.getQueueUserState().getName());
            namedParameters.addValue("ns", queue.isNotifiedOnService());
            namedParameters.addValue("v", queue.getVersion());
            namedParameters.addValue("u", queue.getUpdated());
            namedParameters.addValue("c", queue.getCreated());
            namedParameters.addValue("a", queue.isActive());
            namedParameters.addValue("d", queue.isDeleted());

            maps[i] = namedParameters;
            i ++;
        }

        namedParameterJdbcTemplate.batchUpdate(query, maps);
    }
}
