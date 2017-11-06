package com.noqapp.repository;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.Mobile;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 3/9/17 9:09 AM
 */
public interface QueueManagerJDBC {

    void batchQueues(List<QueueEntity> queues);

    void rollbackQueues(List<QueueEntity> queues);

    /**
     * Get all for did.
     *
     * @param did
     * @return
     */
    @Mobile
    List<QueueEntity> getByDid(String did);

    /**
     * Get all until lastAccessed date.
     *
     * @param did
     * @param lastAccessed
     * @return
     */
    @Mobile
    List<QueueEntity> getByDid(String did, Date lastAccessed);

    /**
     * Get all for qid.
     *
     * @param qid
     * @return
     */
    @Mobile
    List<QueueEntity> getByQid(String qid);

    /**
     * Get all until lastAccessed date.
     *
     * @param qid
     * @param lastAccessed
     * @return
     */
    @Mobile
    List<QueueEntity> getByQid(String qid, Date lastAccessed);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, int hoursSaved);

    @Mobile
    boolean isDBAlive() throws SQLException;
}
