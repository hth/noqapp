package com.token.repository;

import com.token.domain.QueueEntity;
import com.token.domain.annotation.Mobile;

import java.util.List;

/**
 * User: hitender
 * Date: 3/9/17 9:09 AM
 */
public interface QueueManagerJDBC {

    void batchQueue(List<QueueEntity> queues);

    @Mobile
    List<QueueEntity> findByDid(String did);

    @Mobile
    List<QueueEntity> findByRid(String rid);

}
