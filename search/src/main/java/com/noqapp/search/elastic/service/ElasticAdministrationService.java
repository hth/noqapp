package com.noqapp.search.elastic.service;

import static com.noqapp.common.utils.Constants.JSON;

import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.search.elastic.utils.LoadMappingFiles;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;

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
    private ApiHealthService apiHealthService;
    private ElasticsearchClientConfiguration elasticsearchClientConfiguration;
    private String elasticURI;

    @Autowired
    public ElasticAdministrationService(
        @Value("${elastic.host}")
        String elasticHost,

        @Value("${elastic.port}")
        int elasticPort,

        OkHttpClient okHttpClient,
        ApiHealthService apiHealthService,
        ElasticsearchClientConfiguration elasticsearchClientConfiguration
    ) {
        this.okHttpClient = okHttpClient;
        this.apiHealthService = apiHealthService;
        this.elasticsearchClientConfiguration = elasticsearchClientConfiguration;

        this.elasticURI = "http://" + elasticHost + ":" + elasticPort + "/";
    }

    /**
     * Adds mapping to Elastic. This method creates Index and Types. Types are associated inside mapping JSON file.
     * Note: Not to create Index before running this method.
     *
     * @param index name of the index
     * @param type  select a specific mapping file name
     * @return
     */
    public boolean addMapping(String index, String type, String buildNUmber) {
        Instant start = Instant.now();
        try {
            String json = LoadMappingFiles.loadMapping(type, buildNUmber);
            if (StringUtils.isBlank(json)) {
                LOG.error("Failed to load file mapping for Elastic type={}", type);
                throw new RuntimeException("Failed to load file mapping for " + type);
            }

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                .url(elasticURI + index)
                .put(body)
                .build();
            boolean result = parseResponse(request);
            if (!result) {
                LOG.error("Failed to insert mapping in index={} type={} data={}", index, type, json);
            }
            return result;
        } finally {
            apiHealthService.insert(
                "/addMapping",
                "addMapping",
                this.getClass().getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        }
    }

    /**
     * Created index.
     *
     * @param index
     * @return
     */
    public boolean createIndex(String index) {
        Instant start = Instant.now();
        try {
            if (!doesIndexExists(index)) {
                Request request = new Request.Builder()
                    .url(elasticURI + index)
                    .put(RequestBody.create(JSON, "{}"))
                    .build();
                boolean result = parseResponse(request);
                if (!result) {
                    LOG.error("Failed to created index");
                }
                return result;
            }

            return true;
        } finally {
            apiHealthService.insert(
                "/createIndex",
                "createIndex",
                this.getClass().getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        }
    }

    /** Delete all indices. */
    public boolean deleteAllIndices() {
        return deleteIndex("*");
    }

    public void deleteAllPreviousIndices() {
        for (String index : elasticsearchClientConfiguration.previousIndices()) {
            LOG.info("Deleting Elastic Index *{}* deleteStatus={}", index, deleteIndex("*" + index + "*"));
        }
    }

    /**
     * Delete existing index.
     *
     * @param index Name of index to be deleted
     * @return
     */
    private boolean deleteIndex(String index) {
        Instant start = Instant.now();
        try {
            Request request = new Request.Builder()
                .url(elasticURI + index)
                .delete()
                .build();
            boolean result = parseResponse(request);
            if (!result) {
                LOG.error("Failed to delete");
            }
            return result;
        } finally {
            apiHealthService.insert(
                "/deleteIndex",
                "deleteIndex",
                this.getClass().getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        }
    }

    /**
     * Checks if a specific index exists.
     *
     * @param index Name of index
     * @return
     */
    public boolean doesIndexExists(String index) {
        Instant start = Instant.now();
        Request request = new Request.Builder()
            .url(elasticURI + index)
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
        } finally {
            apiHealthService.insert(
                "/doesIndexExists",
                "doesIndexExists",
                this.getClass().getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        }

        return response.code() == 200;
    }

    /**
     * Common DSL Search Query.
     *
     * @param indexPattern
     * @param query
     * @return
     */
    //@Async //For now skipping on @Async and missing source
    String executeDSLQuerySearch(String indexPattern, String query) {
        Instant start = Instant.now();
        RequestBody body = RequestBody.create(JSON, query);
        Request request = new Request.Builder()
            .url(elasticURI + indexPattern)
            .post(body)
            .build();

        Response response = null;
        try {
            String result = null;
            response = okHttpClient.newCall(request).execute();
            if (200 == response.code() && null != response.body()) {
                result = response.body().string();
                LOG.debug("Search found data={}", result);
            } else {
                LOG.error("Failed to find query={} body={}", query, response.body());
            }

            return result;
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to Elastic host reason={}", e.getLocalizedMessage(), e);
            return null;
        } catch (IOException e) {
            LOG.error("Failed deleting index from Elastic request reason={}", e.getLocalizedMessage(), e);
            return null;
        } finally {
            if (response != null) {
                response.body().close();
            }

            apiHealthService.insert(
                "/executeDSLQuerySearch",
                "executeDSLQuerySearch",
                this.getClass().getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        }
    }

    private boolean parseResponse(Request request) {
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                return true;
            } else {
                LOG.error("Request={} Response={}", request, response);
                return false;
            }
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
}
