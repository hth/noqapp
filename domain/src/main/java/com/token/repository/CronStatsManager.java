package com.token.repository;

import com.token.domain.CronStatsEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 12/10/16 8:01 AM
 */
public interface CronStatsManager extends RepositoryManager<CronStatsEntity> {

    List<String> getUniqueCronTasks();

    List<CronStatsEntity> getHistoricalData(String task, int limit);
}

