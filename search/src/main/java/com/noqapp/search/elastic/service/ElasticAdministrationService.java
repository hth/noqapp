package com.noqapp.search.elastic.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * hitender
 * 11/17/17 6:49 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ElasticAdministrationService {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticAdministrationService.class);

    private OkHttpClient okHttpClient;
    private BizStoreManager bizStoreManager;
    private BizStoreElasticService bizStoreElasticService;
    private ApiHealthService apiHealthService;

    @Autowired
    public ElasticAdministrationService(
            OkHttpClient okHttpClient,
            BizStoreManager bizStoreManager,
            BizStoreElasticService bizStoreElasticService,
            ApiHealthService apiHealthService
    ) {
        this.okHttpClient = okHttpClient;
        this.bizStoreManager = bizStoreManager;
        this.bizStoreElasticService = bizStoreElasticService;
        this.apiHealthService = apiHealthService;
    }

    /**
     * Checks if a specific index exists.
     *
     * @param index
     * @return
     */
    public boolean doesIndexExists(String index) {
        Request request = new Request.Builder()
                .url("http://localhost:9200/" + index)
                .head()
                .build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to Elastic host reason={}", e.getLocalizedMessage(), e);
            return false;
        } catch (IOException e) {
            LOG.error("Failed making Elastic request reason={}", e.getLocalizedMessage(), e);
            return false;
        }

        return response.code() == 200;
    }

    /**
     * Delete existing index.
     *
     * @param index
     * @return
     */
    public boolean deleteIndex(String index) {
        Request request = new Request.Builder()
                .url("http://localhost:9200/" + index)
                .delete()
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            return response.code() == 200;
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to Elastic host reason={}", e.getLocalizedMessage(), e);
            return false;
        } catch (IOException e) {
            LOG.error("Failed deleting index from Elastic request reason={}", e.getLocalizedMessage(), e);
            return false;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
    }

    private void save(List<BizStoreElastic> bizStoreElastics) {
        bizStoreElasticService.save(bizStoreElastics);
    }

    public void addAllBizStoreToElastic() {
        Instant start = Instant.now();
        long count = 0;
        try (Stream<BizStoreEntity> stream = bizStoreManager.findAll()) {
            List<BizStoreElastic> bizStoreElastics = stream.map(DomainConversion::getAsBizStoreElastic).collect(Collectors.toList());
            save(bizStoreElastics);
            count += bizStoreElastics.size();
        }

        apiHealthService.insert(
                "/addAllBizStoreToElastic",
                "addAllBizStoreToElastic",
                ElasticAdministrationService.class.getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        LOG.info("Added total={} BizStore to Elastic in duration={}", count, Duration.between(start, Instant.now()).toMillis());
    }
}
