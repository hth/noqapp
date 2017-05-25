package com.noqapp.repository;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.Mobile;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 3/9/17 9:09 AM
 */
public interface QueueManagerJDBC {

    void batchQueue(List<QueueEntity> queues);

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
     * Get all for rid.
     *
     * @param rid
     * @return
     */
    @Mobile
    List<QueueEntity> getByRid(String rid);

    /**
     * Get all until lastAccessed date.
     *
     * @param rid
     * @param lastAccessed
     * @return
     */
    @Mobile
    List<QueueEntity> getByRid(String rid, Date lastAccessed);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String rid, int ratingCount, int hoursSaved);
}
