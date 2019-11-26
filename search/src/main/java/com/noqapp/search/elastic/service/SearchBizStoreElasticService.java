package com.noqapp.search.elastic.service;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.dsl.Conditions;
import com.noqapp.search.elastic.dsl.Filter;
import com.noqapp.search.elastic.dsl.GeoDistance;
import com.noqapp.search.elastic.dsl.Options;
import com.noqapp.search.elastic.dsl.Query;
import com.noqapp.search.elastic.dsl.QueryString;
import com.noqapp.search.elastic.dsl.Search;
import com.noqapp.search.elastic.json.ElasticResult;
import com.noqapp.search.elastic.json.SearchElasticBizStoreSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2019-01-24 21:00
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class SearchBizStoreElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(SearchBizStoreElasticService.class);

    private ElasticAdministrationService elasticAdministrationService;

    private ObjectMapper objectMapper;

    @Autowired
    public SearchBizStoreElasticService(ElasticAdministrationService elasticAdministrationService) {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.elasticAdministrationService = elasticAdministrationService;
    }

    /** Search executed through website or mobile. */
    public List<SearchElasticBizStoreSource> createBizStoreSearchDSLQuery(String searchParameter, String geoHash) {
        String result = searchResultAsString(searchParameter, geoHash);
        if (StringUtils.isNotBlank(result)) {
            try {
                ElasticResult elasticResult = objectMapper.readValue(result, new TypeReference<ElasticResult<SearchElasticBizStoreSource>>(){});
                return elasticResult.getHits() == null ? new ArrayList<>() : elasticResult.getHits().getElasticSources();
            } catch (IOException e) {
                LOG.error("Failed parsing elastic result query={} reason={}", searchParameter, e.getLocalizedMessage(), e);
                return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }

    private String searchResultAsString(String searchParameter, String geoHash) {
        LOG.info("User search query=\"{}\" geoHash={}", searchParameter, geoHash);

        Query q = new Query();
        if (StringUtils.isNotBlank(searchParameter)) {
            /* Search across all the specified fields. */
            q.setConditions(new Conditions()
                .setOptions(new Options()
                    .setQueryStringMultiMatch(new QueryString()
                        .setQuery(searchParameter)
                    )
                )
            );
        } else {
            /* When blank then do a match all. Should be avoided as its little too vague and set Fields as null. */
            q.setConditions(new Conditions().setOptions(new Options().setQueryStringMatchAll(new QueryString().setFields(null))));
        }

        if (StringUtils.isNotBlank(geoHash)) {
            q.getConditions().setFilter(new Filter()
                .setGeoDistance(new GeoDistance()
                    .setDistance(Constants.MAX_Q_SEARCH_DISTANCE_WITH_UNITS)
                    .setGeoHash(geoHash)
                ));
        }

        LOG.info("Elastic query q={}", q.asJson());
        Search search = new Search()
            .setFrom(0)
            .setSize(PaginationEnum.TEN.getLimit())
            .setQuery(q);

        return executeSearchOnBizStoreUsingDSLFilteredData(search.asJson());
    }

    /**
     * Performs search on the index with provided DSL with filtered set of data sent in response. The fields below
     * are fetched when searched. Fetched fields are populated in mapped object.
     */
    private String executeSearchOnBizStoreUsingDSLFilteredData(String dslQuery) {
        LOG.info("DSL dslQuery={}", dslQuery);
        String result = elasticAdministrationService.executeDSLQuerySearch(
            BizStoreElastic.INDEX
                + "/"
                + BizStoreElastic.TYPE
                + "/_search?pretty&filter_path=hits.hits._source&_source=N,BT,BC,BCI,BID,AD,AR,TO,DT,SH,EP,PP,PS,PD,PF,ST,SS,CC,CS,PH,PI,RA,RC,DN,QR,GH,WL,FF,DI",
            dslQuery
        );

        LOG.debug("DSL Query result={}", result);
        return result;
    }
}
