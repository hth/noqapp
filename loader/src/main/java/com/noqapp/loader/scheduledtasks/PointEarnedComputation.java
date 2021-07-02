package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.PointEarnedEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.repository.PointEarnedManager;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.service.StatsCronService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * hitender
 * 6/24/21 9:39 AM
 */
@Component
public class PointEarnedComputation {
    private static final Logger LOG = LoggerFactory.getLogger(PointEarnedComputation.class);

    private String dailyPointComputeSwitch;

    private PointEarnedManager pointEarnedManager;
    private UserPreferenceManager userPreferenceManager;
    private StatsCronService statsCronService;

    @Autowired
    public PointEarnedComputation(
        @Value("${PointEarnedComputation.dailyPointComputeSwitch}")
        String dailyPointComputeSwitch,

        PointEarnedManager pointEarnedManager,
        UserPreferenceManager userPreferenceManager,
        StatsCronService statsCronService
    ) {
        this.dailyPointComputeSwitch = dailyPointComputeSwitch;

        this.pointEarnedManager = pointEarnedManager;
        this.userPreferenceManager = userPreferenceManager;
        this.statsCronService = statsCronService;
    }

    @Scheduled(cron = "${loader.PointEarnedComputation.dailyPointCompute}")
    public void dailyPointCompute() {
        StatsCronEntity statsCron = new StatsCronEntity(
            PointEarnedComputation.class.getName(),
            "dailyPointCompute",
            dailyPointComputeSwitch);

        if ("OFF".equalsIgnoreCase(dailyPointComputeSwitch)) {
            return;
        }

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();
        try {
            LOG.info("Creating preferred business product tar file");

            try (Stream<String> qids = pointEarnedManager.findUniqueAllNotMarkedComputed()) {
                qids.iterator().forEachRemaining(qid -> {
                    try {
                        UserPreferenceEntity userPreference = userPreferenceManager.findByQueueUserId(qid);
                        userPreferenceManager.updatePointHistorical(userPreference.getQueueUserId(), userPreference.getEarnedPoint());
                    } catch (Exception e) {
                        LOG.error("Failed to update point earned previously in userPreference qid={} reason={}",
                            qid,
                            e.getLocalizedMessage(),
                            e);
                    }
                });
            }

            try (Stream<PointEarnedEntity> stream =  pointEarnedManager.findAllNotMarkedComputed()) {
                stream.iterator().forEachRemaining(pointEarned -> {
                    try {
                        pointEarnedManager.markComputedById(pointEarned.getId());
                        userPreferenceManager.updatePoint(pointEarned.getQueueUserId(), pointEarned.getPoint());
                        success.getAndIncrement();
                    } catch (Exception e) {
                        failure.getAndIncrement();
                        LOG.error("Failed to update point earned in userPreference id={} qid={} reason={}",
                            pointEarned.getId(),
                            pointEarned.getQueueUserId(),
                            e.getLocalizedMessage(),
                            e);
                    }
                });
            }

        } catch (Exception e) {
            LOG.error("Error computing daily point reason={}", e.getLocalizedMessage(), e);
            failure.getAndIncrement();
        } finally {
            if (0 != success.get() || 0 != failure.get()) {
                statsCron.addStats("success", success.get());
                statsCron.addStats("failure", failure.get());
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete success={} failure={}", success, failure);
            }
        }
    }
}
