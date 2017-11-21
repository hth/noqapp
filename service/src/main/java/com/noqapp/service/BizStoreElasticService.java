package com.noqapp.service;

import com.noqapp.domain.elastic.BizStoreElasticEntity;
import com.noqapp.repository.elastic.BizStoreElasticManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 11/14/17 2:55 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BizStoreElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreElasticService.class);

    private BizStoreElasticManager<BizStoreElasticEntity> bizStoreElasticManager;

    private int limitRecords;

    @Autowired
    public BizStoreElasticService(
            @Value("${limitRecords:10}")
            int limitRecords,
            
            BizStoreElasticManager<BizStoreElasticEntity> bizStoreElasticManager
    ) {
        this.limitRecords = limitRecords;
        this.bizStoreElasticManager = bizStoreElasticManager;
    }

    @Async
    void save(BizStoreElasticEntity bizStoreElastic) {
        bizStoreElasticManager.save(bizStoreElastic);
    }

    @Async
    void save(List<BizStoreElasticEntity> bizStoreElastics) {
        LOG.info("Bulk save size={}", bizStoreElastics.size());
        bizStoreElasticManager.save(bizStoreElastics);
    }

    @Async
    void delete(String id) {
        bizStoreElasticManager.delete(id);
    }

    public List<BizStoreElasticEntity> searchByBusinessName(String businessName) {
        LOG.info("Searching for {}", businessName);
        return bizStoreElasticManager.searchByBusinessName(businessName, limitRecords);
    }
}
