package com.noqapp.service;

import com.noqapp.repository.elastic.ElasticAdministrationManager;
import com.noqapp.service.config.NetworkService;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;

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

    private ElasticAdministrationManager elasticAdministrationManager;
    private NetworkService networkService;

    @Autowired
    public ElasticAdministrationService(
            ElasticAdministrationManager elasticAdministrationManager,
            NetworkService networkService
    ) {
        this.elasticAdministrationManager = elasticAdministrationManager;
        this.networkService = networkService;
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
            response = networkService.getOkHttpClient().newCall(request).execute();
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to Elastic host reason={}", e.getLocalizedMessage(), e);
            return false;
        } catch (IOException e) {
            LOG.error("Failed making Elastic request reason={}", e.getLocalizedMessage(), e);
            return false;
        }

        return response.code() == 200;
    }

    public void createIndex(String index) {
        //Reactive Streaming
    }

}
