package com.noqapp.repository;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.CustomTransactional;
import com.noqapp.domain.mapper.QueueRowMapper;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;

import org.apache.commons.lang3.StringUtils;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

/**
 * User: hitender
 * Date: 3/9/17 9:57 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class QueueManagerJDBCImpl implements QueueManagerJDBC {
    private static final Logger LOG = LoggerFactory.getLogger(QueueManagerJDBCImpl.class);

    private static final String insert =
        "INSERT INTO QUEUE (ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D)" +
            " VALUES " +
            "(:id,:qr,:did,:ts,:qid,:tn,:dn,:bt,:qs,:ti,:ns,:ra,:hr,:rv,:sn,:sb,:se,:bn,:rr,:st,:v,:u,:c,:a,:d)";

    private static final String delete = "DELETE FROM QUEUE WHERE ID = :id";

    private static final String findByQid_Simple =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE QID = ? " +
            "ORDER BY C DESC";

    /* Inner condition removed. */
    @Deprecated
    private static final String findByQid =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE QID = ? " +
            "AND " +
            "C IN (SELECT max(C) " +
            "FROM QUEUE " +
            "WHERE QID = ? " +
            "GROUP BY QR) ORDER BY C DESC";

    /* Inner condition removed. */
    @Deprecated
    private static final String findByQidAndByLastUpdated =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE QID = ? AND U >= ? " +
            "AND " +
            "C IN (SELECT max(C) " +
            "FROM QUEUE " +
            "WHERE QID = ? " +
            "GROUP BY QR) ORDER BY C DESC";

    private static final String findHistoricalRecordByCodeQRDateRange =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE QR = ? " +
            "AND " +
            "QID IS NOT NULL " +
            "AND " +
            "C BETWEEN ? AND ? " +
            "ORDER BY C DESC";

    private static final String findByCodeQR =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE QR = ? " +
            "AND " +
            "C BETWEEN NOW() - INTERVAL ? DAY AND NOW() " +
            "ORDER BY C DESC";

    private static final String findReviewsByCodeQR =
        "SELECT RA, HR, RV, QID, C" +
            " FROM " +
            "QUEUE WHERE QR = ? " +
            "AND RA <> 0 " +
            "AND " +
            "C BETWEEN NOW() - INTERVAL ? DAY AND NOW() " +
            "ORDER BY C DESC";

    private static final String findReviewsByBizNameId =
        "SELECT RA, HR, RV, QID, C" +
            " FROM " +
            "QUEUE WHERE BN = ? " +
            "AND RA <> 0 " +
            "AND " +
            "C BETWEEN NOW() - INTERVAL ? DAY AND NOW() " +
            "ORDER BY C DESC";

    private static final String findByDid =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE DID = ? " +
            "AND " +
            "C IN (SELECT max(C) " +
            "FROM QUEUE " +
            "WHERE DID = ? " +
            "GROUP BY QR) ORDER BY C DESC";

    private static final String findByDidAndByLastUpdated =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE DID = ? AND U >= ? " +
            "AND " +
            "C IN (SELECT max(C) " +
            "FROM QUEUE " +
            "WHERE DID = ? " +
            "GROUP BY QR) ORDER BY C DESC";

    private static final String checkIfClientVisitedStore =
        "SELECT EXISTS(SELECT 1 FROM QUEUE WHERE QR = ? AND QID = ? AND QS = ? LIMIT 1)";

    private static final String clientVisitedStoreDate =
        "SELECT C FROM QUEUE WHERE QR = ? AND QID = ? AND QS = ? ORDER BY C DESC LIMIT 1";

    private static final String checkIfClientVisitedBusiness =
        "SELECT EXISTS(SELECT 1 FROM QUEUE WHERE BN = ? AND QID = ? LIMIT 1)";

    private static final String findByCodeQR_Qid_Token =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE QID = ? AND QR = ? AND TN = ? AND TI IS NOT NULL";

    private static final String findAfterCreateDate = "SELECT * FROM QUEUE WHERE C >= ? ORDER BY C DESC";

    private static final String findByCodeQR_RecordReferenceId =
        "SELECT ID, QR, DID, TS, QID, TN, DN, BT, QS, TI, NS, RA, HR, RV, SN, SB, SE, BN, RR, ST, V, U, C, A, D" +
            " FROM " +
            "QUEUE WHERE QR = ? AND RR = ?";

    private static final String servicedInPastXDays =
        "SELECT EXISTS(SELECT 1 FROM QUEUE WHERE QR = ? AND QID = ? AND QS = '" + QueueUserStateEnum.S.name() + "' AND C BETWEEN NOW() - INTERVAL ? DAY AND NOW() LIMIT 1)";

    private static final String countDistinctQIDInPastXDays =
        "SELECT DISTINCT COUNT(QID)" +
            " FROM " +
            "QUEUE WHERE BN = ? AND C BETWEEN NOW() - INTERVAL ? DAY AND NOW()";

    private static final String distinctQIDsInBiz =
        "SELECT DISTINCT QID" +
            " FROM " +
            "QUEUE WHERE BN = ? AND C BETWEEN NOW() - INTERVAL ? DAY AND NOW()";

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
                LOG.debug("Added queue id={}", queue.getId());

                MapSqlParameterSource namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("id", queue.getId());
                namedParameters.addValue("qr", queue.getCodeQR());
                namedParameters.addValue("did", queue.getDid());
                namedParameters.addValue("ts", queue.getTokenService().getName());
                namedParameters.addValue("qid", queue.getQueueUserId());
                namedParameters.addValue("tn", queue.getTokenNumber());
                namedParameters.addValue("dn", queue.getDisplayName());
                namedParameters.addValue("bt", queue.getBusinessType().getName());
                namedParameters.addValue("qs", queue.getQueueUserState().getName());
                namedParameters.addValue("ti", queue.getTransactionId());
                namedParameters.addValue("ns", queue.isNotifiedOnService() ? 1 : 0);
                namedParameters.addValue("ra", queue.getRatingCount());
                namedParameters.addValue("hr", queue.getHoursSaved());
                namedParameters.addValue("rv", queue.getReview());
                namedParameters.addValue("sn", queue.getServerName());
                namedParameters.addValue("sb", queue.getServiceBeginTime());
                namedParameters.addValue("se", queue.getServiceEndTime());
                namedParameters.addValue("bn", queue.getBizNameId());
                namedParameters.addValue("rr", queue.getRecordReferenceId());
                namedParameters.addValue("st", null == queue.getSentimentType() ? null : queue.getSentimentType().getName());

                namedParameters.addValue("v", queue.getVersion());
                namedParameters.addValue("u", queue.getUpdated());
                namedParameters.addValue("c", queue.getCreated());
                namedParameters.addValue("a", queue.isActive() ? 1 : 0);
                namedParameters.addValue("d", queue.isDeleted() ? 1 : 0);

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
            LOG.info("Insert count={} rowUpdated={}", maps.length, rowUpdated.length);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed batch delete reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public List<QueueEntity> getByDid(String did) {
        LOG.info("Fetch history by did={}", did);
        try {
            return jdbcTemplate.query(findByDid, new Object[]{did, did}, new QueueRowMapper());
        } catch (Exception e) {
            LOG.error("Error did={} reason={}", did, e.getLocalizedMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<QueueEntity> getByDid(String did, Date lastAccessed) {
        LOG.info("Fetch history by did={} lastAccessed={}", did, lastAccessed);
        try {
            return jdbcTemplate.query(findByDidAndByLastUpdated, new Object[]{did, lastAccessed, did}, new QueueRowMapper());
        } catch (Exception e) {
            LOG.error("Error did={} lastAccessed={} reason={}", did, lastAccessed, e.getLocalizedMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<QueueEntity> getByQidSimple(String qid) {
        return jdbcTemplate.query(findByQid_Simple, new Object[]{qid}, new QueueRowMapper());
    }

    @Override
    public List<QueueEntity> getByQid(String qid) {
        return jdbcTemplate.query(findByQid, new Object[]{qid, qid}, new QueueRowMapper());
    }

    @Override
    public List<QueueEntity> getByQid(String qid, Date lastAccessed) {
        LOG.info("Fetch history by qid={} lastAccessed={}", qid, lastAccessed);
        return jdbcTemplate.query(findByQidAndByLastUpdated, new Object[]{qid, lastAccessed, qid}, new QueueRowMapper());
    }

    @Override
    public List<QueueEntity> getByCodeQRDateRangeAndNotNullQID(String codeQR, Date start, Date until) {
        LOG.info("Fetch history by codeQR={} and not null QID range from start={} until={}", codeQR, start, until);
        return jdbcTemplate.query(findHistoricalRecordByCodeQRDateRange, new Object[]{codeQR, start, until}, new QueueRowMapper());
    }

    @Override
    public List<QueueEntity> getByCodeQR(String codeQR, int limitedToDays) {
        LOG.info("Fetch history by codeQR={} limitedToDays={}", codeQR, limitedToDays);
        return jdbcTemplate.query(findByCodeQR, new Object[]{codeQR, limitedToDays}, new QueueRowMapper());
    }

    @Override
    @CustomTransactional
    public boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, int hoursSaved, String review, SentimentTypeEnum sentimentType) {
        try {
            if (StringUtils.isNotBlank(qid)) {
                return this.jdbcTemplate.update(
                    "UPDATE QUEUE SET RA = ?, HR = ?, RV = ?, ST = ? WHERE QR = ? AND QID = ? AND TN = ?",
                    ratingCount, hoursSaved, review, sentimentType == null ? null : sentimentType.name(), codeQR, qid, token) > 0;
            } else {
                return this.jdbcTemplate.update(
                    "UPDATE QUEUE SET RA = ?, HR = ?, RV = ?, ST = ? WHERE QR = ? AND DID = ? AND TN = ?",
                    ratingCount, hoursSaved, review, sentimentType == null ? null : sentimentType.name(), codeQR, did, token) > 0;
            }
        } catch (Exception e) {
            LOG.error("Failed review update codeQR={} token={} did={} qid={} ratingCount={} hoursSaved={} review={} reason={}",
                codeQR, token, did, qid, ratingCount, hoursSaved, review, e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public Date clientVisitedStoreAndServicedDate(String codeQR, String qid) {
        LOG.info("Fetch history by codeQR={} qid={}", codeQR, qid);
        try {
            return jdbcTemplate.queryForObject(clientVisitedStoreDate, new Object[]{codeQR, qid, QueueUserStateEnum.S.getName()}, Date.class);
        } catch (EmptyResultDataAccessException e) {
            //TODO fix this error or query
            return null;
        }
    }

    @Override
    public boolean hasClientVisitedThisStoreAndServiced(String codeQR, String qid) {
        LOG.info("Fetch history by codeQR={} qid={}", codeQR, qid);
        return jdbcTemplate.queryForObject(checkIfClientVisitedStore, new Object[]{codeQR, qid, QueueUserStateEnum.S.getName()}, Boolean.class);
    }

    @Override
    public boolean hasClientVisitedThisBusiness(String bizNameId, String qid) {
        LOG.info("Fetch history by bizNameId={} qid={}", bizNameId, qid);
        return jdbcTemplate.queryForObject(checkIfClientVisitedBusiness, new Object[]{bizNameId, qid}, Boolean.class);
    }

    @Override
    public List<QueueEntity> findReviews(String codeQR, int reviewLimitedToDays) {
        LOG.info("Fetch queue review by codeQR={} limitedToDays={}", codeQR, reviewLimitedToDays);
        return jdbcTemplate.query(
            findReviewsByCodeQR,
            new Object[]{codeQR, reviewLimitedToDays},
            (rs, rowNum) -> {
                QueueEntity queue = new QueueEntity(null, null, null, rs.getString(4), 0, null, null)
                    .setRatingCount(rs.getInt(1))
                    .setHoursSaved(rs.getInt(2))
                    .setReview(rs.getString(3));
                queue.setCreated(rs.getDate(5));
                return queue;
            }
        );
    }

    @Override
    public List<QueueEntity> findLevelUpReviews(String bizNameId, int reviewLimitedToDays) {
        LOG.info("Fetch queue review by bizNameId={} limitedToDays={}", bizNameId, reviewLimitedToDays);
        return jdbcTemplate.query(
            findReviewsByBizNameId,
            new Object[]{bizNameId, reviewLimitedToDays},
            (rs, rowNum) -> {
                QueueEntity queue = new QueueEntity(null, null, null, rs.getString(4), 0, null, null)
                    .setRatingCount(rs.getInt(1))
                    .setHoursSaved(rs.getInt(2))
                    .setReview(rs.getString(3));
                queue.setCreated(rs.getDate(5));
                return queue;
            }
        );
    }

    @Override
    public QueueEntity findQueueThatHasTransaction(String codeQR, String qid, int token) {
        return jdbcTemplate.queryForObject(
            findByCodeQR_Qid_Token,
            new Object[]{codeQR, qid, token},
            new QueueRowMapper()
        );
    }

    @Override
    public boolean isDBAlive() {
        return jdbcTemplate.queryForMap("SELECT 1").size() == 0;
    }

    @Override
    public List<QueueEntity> findAfterCreateDate(Date since) {
        return jdbcTemplate.query(
            findAfterCreateDate,
            new Object[]{since},
            new QueueRowMapper());
    }

    @Override
    public QueueEntity findOneHistoricalByRecordReferenceId(String codeQR, String recordReferenceId) {
        return jdbcTemplate.queryForObject(
            findByCodeQR_RecordReferenceId,
            new Object[]{codeQR, recordReferenceId},
            new QueueRowMapper()
        );
    }

    @Override
    public boolean hasServicedInPastXDays(String codeQR, String qid, int days) {
        return jdbcTemplate.queryForObject(
            servicedInPastXDays,
            new Object[]{codeQR, qid, days},
            Boolean.class
        );
    }

    @Override
    public long countDistinctQIDsInBiz(String bizNameId, int days) {
        return jdbcTemplate.queryForObject(
            countDistinctQIDInPastXDays,
            new Object[]{bizNameId, days},
            Long.class
        );
    }

    @Override
    public List<String> distinctQIDsInBiz(String bizNameId, int days) {
        return jdbcTemplate.queryForList(
            distinctQIDsInBiz,
            new Object[]{bizNameId, days},
            String.class
        );
    }
}
