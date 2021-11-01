package com.noqapp.search.elastic.service;

import com.noqapp.domain.IncidentEventEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.IncidentEventManager;
import com.noqapp.search.elastic.domain.IncidentEventElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.repository.IncidentEventElasticManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * hitender
 * 5/30/21 9:16 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class IncidentEventElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(IncidentEventElasticService.class);

    private IncidentEventElasticManager<IncidentEventElastic> incidentEventElasticManager;
    private IncidentEventManager incidentEventManager;
    private ApiHealthService apiHealthService;

    private int fetchUntilDays;

    @Autowired
    public IncidentEventElasticService(
        @Value("${fetchUntilDays:30}")
        int fetchUntilDays,

        IncidentEventElasticManager<IncidentEventElastic> incidentEventElasticManager,
        IncidentEventManager incidentEventManager,
        ApiHealthService apiHealthService
    ) {
        this.fetchUntilDays = fetchUntilDays;

        this.incidentEventElasticManager = incidentEventElasticManager;
        this.incidentEventManager = incidentEventManager;
        this.apiHealthService = apiHealthService;
    }

    @Async
    public void save(IncidentEventElastic incidentEventElastic) {
        incidentEventElasticManager.save(incidentEventElastic);
    }

    @Async
    void save(List<IncidentEventElastic> incidentEventElastics) {
        LOG.info("Bulk save size={}", incidentEventElastics.size());
        if (!incidentEventElastics.isEmpty()) {
            incidentEventElasticManager.save(incidentEventElastics);
        }
    }

    public void addAllIncidentEventToElastic() {
        Instant start = Instant.now();
        long countIncidentEventElastic = 0;
        try (Stream<IncidentEventEntity> stream = incidentEventManager.findAllWithStream(fetchUntilDays)) {
            List<IncidentEventElastic> incidentEventElastics = new ArrayList<>();
            stream.iterator().forEachRemaining(incidentEvent -> {
                IncidentEventElastic incidentEventElastic = null;
                try {
                    incidentEventElastic = DomainConversion.getAsIncidentEventElastic(incidentEvent);
                    incidentEventElastics.add(incidentEventElastic);
                } catch (Exception e) {
                    LOG.error("Failed to insert incidentEvent in elastic data={} reason={}",
                        incidentEventElastic,
                        e.getLocalizedMessage(),
                        e);
                }
            });
            save(incidentEventElastics);
            countIncidentEventElastic += incidentEventElastics.size();
        }

        apiHealthService.insert(
            "/addAllIncidentEventToElastic",
            "addAllIncidentEventToElastic",
            this.getClass().getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        LOG.info("Added countIncidentEventElastic={} to Elastic in duration={}",
            countIncidentEventElastic,
            Duration.between(start, Instant.now()).toMillis());
    }
}
