package com.noqapp.repository;

import com.mongodb.client.DistinctIterable;
import com.noqapp.domain.StatsCronEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 12/10/16 8:01 AM
 */
public interface StatsCronManager extends RepositoryManager<StatsCronEntity> {

    DistinctIterable<String> getUniqueCronTasks();

    List<StatsCronEntity> getHistoricalData(String task, int limit);
}

