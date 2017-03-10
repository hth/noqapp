package com.token.loader.repository;

import com.token.domain.QueueEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 3/9/17 9:09 AM
 */
public interface QueueManagerJDBC {

    void batchQueue(List<QueueEntity> queues);
}
