package com.noqapp.service;

import com.noqapp.domain.StatsCronEntity;
import com.noqapp.repository.StatsCronManager;

import com.mongodb.client.DistinctIterable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 12/10/16 8:00 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class StatsCronService {
    private StatsCronManager statsCronManager;

    @Autowired
    public StatsCronService(StatsCronManager statsCronManager) {
        this.statsCronManager = statsCronManager;
    }

    public void save(StatsCronEntity statsCron) {
        statsCron.setEnd(new Date());
        statsCronManager.save(statsCron);
    }

    public Map<String, List<StatsCronEntity>> getUniqueCronTasks(int limit) {
        Map<String, List<StatsCronEntity>> taskStats = new LinkedHashMap<>();
        DistinctIterable<String> tasks = statsCronManager.getUniqueCronTasks();
        for (String task : tasks) {
            taskStats.put(task, statsCronManager.getHistoricalData(task, limit));
        }
        return taskStats;
    }
}
