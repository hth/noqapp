package com.noqapp.search.elastic.service;

import static com.noqapp.search.elastic.service.BizStoreElasticService.MINUTES;
import static com.noqapp.search.elastic.service.BizStoreElasticService.includeFields;
import static com.noqapp.search.elastic.service.BizStoreElasticService.excludeFields;
import static com.noqapp.search.elastic.service.BizStoreElasticService.populateSearchData;
import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreElasticList;
import com.noqapp.search.elastic.dsl.Conditions;
import com.noqapp.search.elastic.dsl.Filter;
import com.noqapp.search.elastic.dsl.GeoDistance;
import com.noqapp.search.elastic.dsl.Options;
import com.noqapp.search.elastic.dsl.Query;
import com.noqapp.search.elastic.dsl.QueryString;
import com.noqapp.search.elastic.dsl.Search;
import com.noqapp.search.elastic.json.ElasticBizStoreSearchSource;
import com.noqapp.search.elastic.json.ElasticResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

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
public class BizStoreSearchElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreSearchElasticService.class);

    private ElasticAdministrationService elasticAdministrationService;
    private ElasticsearchClientConfiguration elasticsearchClientConfiguration;

    private ObjectMapper objectMapper;

    @Autowired
    public BizStoreSearchElasticService(
        ElasticAdministrationService elasticAdministrationService,
        ElasticsearchClientConfiguration elasticsearchClientConfiguration
    ) {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.elasticAdministrationService = elasticAdministrationService;
        this.elasticsearchClientConfiguration = elasticsearchClientConfiguration;
    }

    /** Search executed through website or mobile. */
    public List<ElasticBizStoreSearchSource> createBizStoreSearchDSLQuery(String searchParameter, String geoHash) {
        String result = searchResultAsString(searchParameter, geoHash);
        if (StringUtils.isNotBlank(result)) {
            try {
                ElasticResult elasticResult = objectMapper.readValue(result, new TypeReference<ElasticResult<ElasticBizStoreSearchSource>>(){});
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
            .setSize(PaginationEnum.TWENTY.getLimit())
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
                + "/_search?pretty&filter_path=hits.hits._source&_source=N,BT,BC,BCI,BID,AD,AR,TO,DT,SH,EP,PP,PS,PD,PF,ST,SS,CC,CS,PH,PI,RA,RC,DN,QR,GH,WL,FF,DI",
            dslQuery
        );

        LOG.debug("DSL Query result={}", result);
        return result;
    }

    @Mobile
    public BizStoreElasticList executeNearMeSearchOnBizStoreUsingRestClient(
        String query,
        String cityName,
        String geoHash,
        String filters,
        String scrollId
    ) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueMinutes(MINUTES));
                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                /* Choose field match or matchAllQuery. */
                searchSourceBuilder.query(QueryBuilders.multiMatchQuery(query, "N", "DN", "BC"));
//                searchSourceBuilder.query(QueryBuilders
//                        .matchPhrasePrefixQuery("N", query)
//                        /* to limit the number of wildcard matches that can possibly match. */
//                        .maxExpansions(1));

                /* Term for exact query. */
                //searchSourceBuilder.query(QueryBuilders.termQuery(query, "N"));

                searchSourceBuilder.query(geoDistanceQuery("GH")
                    .geohash(geoHash)
                    .distance(Constants.MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
                searchSourceBuilder.sort(new GeoDistanceSortBuilder("GH", geoHash).order(SortOrder.DESC));
                searchSourceBuilder.size(PaginationEnum.TEN.getLimit());
                searchRequest.source(searchSourceBuilder);
                searchRequest.scroll(TimeValue.timeValueMinutes(MINUTES));

                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
                LOG.info("Search query={} geoHash={} searchSourceBuilder={}", query, geoHash, searchSourceBuilder);
            }

            bizStoreElastics.setScrollId(searchResponse.getScrollId());
            populateSearchData(bizStoreElastics, searchResponse.getHits().getHits());
            return bizStoreElastics;
        } catch (IOException e) {
            LOG.error("Failed getting data reason={}", e.getLocalizedMessage(), e);
            return bizStoreElastics;
        }
    }

    @Mobile
    public BizStoreElasticList kioskSearchUsingRestClient(
        String query,
        String bizNameId,
        String scrollId
    ) {
        BizStoreElasticList bizStoreElastics = new BizStoreElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueMinutes(MINUTES));
                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(BizStoreElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                /* Search just DO Query. */
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.matchQuery("BID", bizNameId));
                boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(query, "N", "DN", "BC"));
                searchSourceBuilder.query(boolQueryBuilder);
                searchSourceBuilder.sort(new FieldSortBuilder("DN").order(SortOrder.ASC));
                searchSourceBuilder.size(PaginationEnum.TEN.getLimit());
                searchRequest.source(searchSourceBuilder);
                searchRequest.scroll(TimeValue.timeValueMinutes(MINUTES));

                searchResponse = elasticsearchClientConfiguration.createRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
                LOG.info("Search query={} searchSourceBuilder={}", query, searchSourceBuilder);
            }

            bizStoreElastics.setScrollId(searchResponse.getScrollId());
            populateSearchData(bizStoreElastics, searchResponse.getHits().getHits());
            return bizStoreElastics;
        } catch (IOException e) {
            LOG.error("Failed getting data reason={}", e.getLocalizedMessage(), e);
            return bizStoreElastics;
        }
    }
}
