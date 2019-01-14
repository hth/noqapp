package com.noqapp.repository;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.SentimentTypeEnum;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 3/9/17 9:09 AM
 */
public interface QueueManagerJDBC {

    void batchQueues(List<QueueEntity> queues);

    void rollbackQueues(List<QueueEntity> queues);

    /** Get all for did. */
    @Mobile
    List<QueueEntity> getByDid(String did);

    /** Get all until lastAccessed date. */
    @Mobile
    List<QueueEntity> getByDid(String did, Date lastAccessed);

    /** Simple search query. */
    List<QueueEntity> getByQidSimple(String qid);

    /** Get all for qid. */
    @Mobile
    @Deprecated
    List<QueueEntity> getByQid(String qid);

    /** Get all until lastAccessed date. */
    @Mobile
    @Deprecated
    List<QueueEntity> getByQid(String qid, Date lastAccessed);

    @Mobile
    List<QueueEntity> getByCodeQRAndNotNullQID(String codeQR, int limitedToDays);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, int hoursSaved, String review, SentimentTypeEnum sentimentType);

    @Mobile
    boolean hasClientVisitedThisStore(String codeQR, String qid);

    @Mobile
    boolean hasClientVisitedThisBusiness(String bizNameId, String qid);

    @Mobile
    List<QueueEntity> findReviews(String codeQR, int reviewLimitedToDays);

    @Mobile
    List<QueueEntity> findLevelUpReviews(String bizNameId, int reviewLimitedToDays);

    @Mobile
    boolean isDBAlive();
}
