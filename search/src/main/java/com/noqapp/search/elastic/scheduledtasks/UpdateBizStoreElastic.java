package com.noqapp.search.elastic.scheduledtasks;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreSpatialElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.repository.BizStoreElasticManager;
import com.noqapp.search.elastic.repository.BizStoreSpatialElasticManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * hitender
 * 4/5/20 1:36 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class UpdateBizStoreElastic {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateBizStoreElastic.class);

    private BizStoreManager bizStoreManager;
    private StoreHourManager storeHourManager;
    private BizStoreElasticManager bizStoreElasticManager;
    private BizStoreSpatialElasticManager bizStoreSpatialElasticManager;
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    public UpdateBizStoreElastic(
        BizStoreManager bizStoreManager,
        StoreHourManager storeHourManager,
        BizStoreElasticManager bizStoreElasticManager,
        BizStoreSpatialElasticManager bizStoreSpatialElasticManager,
        RestHighLevelClient restHighLevelClient
    ) {
        this.bizStoreManager = bizStoreManager;
        this.storeHourManager = storeHourManager;
        this.bizStoreElasticManager = bizStoreElasticManager;
        this.bizStoreSpatialElasticManager = bizStoreSpatialElasticManager;
        this.restHighLevelClient = restHighLevelClient;
    }

    /** Update store elastic when pending request due. */
    @Scheduled(fixedDelayString = "${elastic.updatePendingBizStoreElastic}")
    public void updatePendingBizStoreElastic() {
        AtomicInteger count = new AtomicInteger();
        try (Stream<BizStoreEntity> stream = bizStoreManager.findAllPendingElasticUpdateStream()) {
            stream.iterator().forEachRemaining(bizStore -> {
                List<StoreHourEntity> storeHours = storeHourManager.findAll(bizStore.getId());
                BizStoreElastic bizStoreElastic = DomainConversion.getAsBizStoreElastic(bizStore, storeHours);
                bizStoreElasticManager.save(bizStoreElastic);
                if (bizStoreSpatialElasticManager.exists(bizStoreElastic.getId())) {
                    bizStoreSpatialElasticManager.save(bizStoreElastic);
                }
                bizStoreManager.removePendingElastic(bizStore.getId());
                count.getAndIncrement();
            });
        }

        if (count.intValue() > 0) {
            try {
                RefreshRequest refreshRequest = new RefreshRequest(BizStoreElastic.INDEX);
                RefreshResponse refreshResponse = restHighLevelClient.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
                LOG.info("Elastic index {} updated status={}", BizStoreElastic.INDEX, refreshResponse.getStatus());

                refreshRequest = new RefreshRequest(BizStoreSpatialElastic.INDEX);
                refreshResponse = restHighLevelClient.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
                LOG.info("Elastic index {} updated status={}", BizStoreSpatialElastic.INDEX, refreshResponse.getStatus());
            } catch (IOException e) {
                LOG.error("Error refreshing elastic reason={}", e.getLocalizedMessage(), e);
            }
            LOG.info("Updated elastic count={}", count.intValue());
        }
    }
}
