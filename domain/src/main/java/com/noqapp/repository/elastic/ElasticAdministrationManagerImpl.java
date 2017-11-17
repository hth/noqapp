package com.noqapp.repository.elastic;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * hitender
 * 11/17/17 6:44 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class ElasticAdministrationManagerImpl implements ElasticAdministrationManager {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticAdministrationManagerImpl.class);

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    public ElasticAdministrationManagerImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }
}
