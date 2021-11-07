package com.noqapp.search.elastic.service;

import static com.noqapp.common.utils.Constants.MAX_Q_SEARCH_DISTANCE;
import static com.noqapp.search.elastic.service.BizStoreSearchElasticService.MINUTES_FOR_SEARCH;
import static com.noqapp.search.elastic.service.MarketplaceElasticService.excludeFields;
import static com.noqapp.search.elastic.service.MarketplaceElasticService.includeFields;
import static com.noqapp.search.elastic.service.MarketplaceElasticService.populateSearchData;
import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.search.elastic.domain.MarketplaceElastic;
import com.noqapp.search.elastic.domain.MarketplaceElasticList;
import com.noqapp.search.elastic.dsl.Conditions;
import com.noqapp.search.elastic.dsl.Filter;
import com.noqapp.search.elastic.dsl.GeoDistance;
import com.noqapp.search.elastic.dsl.MarketplaceQueryString;
import com.noqapp.search.elastic.dsl.Options;
import com.noqapp.search.elastic.dsl.Query;
import com.noqapp.search.elastic.dsl.Search;
import com.noqapp.search.elastic.json.ElasticMarketplaceSearchSource;
import com.noqapp.search.elastic.json.ElasticResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 3/2/21 9:59 PM
 */
@Service
public class MarketplaceSearchElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceSearchElasticService.class);

    private ElasticAdministrationService elasticAdministrationService;
    private RestHighLevelClient restHighLevelClient;

    private int paginationSize;
    private ObjectMapper objectMapper;

    @Autowired
    public MarketplaceSearchElasticService(
        @Value("${MarketplaceSearch.paginationSize}")
        int paginationSize,

        ElasticAdministrationService elasticAdministrationService,
        RestHighLevelClient restHighLevelClient
    ) {
        this.paginationSize = paginationSize;

        this.elasticAdministrationService = elasticAdministrationService;
        this.restHighLevelClient = restHighLevelClient;

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /** Search executed through website or mobile. */
    public List<ElasticMarketplaceSearchSource> createMarketplaceSearchDSLQuery(String searchParameter, String geoHash) {
        String result = searchResultAsString(searchParameter, geoHash);
        if (StringUtils.isNotBlank(result)) {
            try {
                ElasticResult<ElasticMarketplaceSearchSource> elasticResult = objectMapper.readValue(result, new TypeReference<>() {});
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
            q.setConditions(
                new Conditions().setOptions(
                    new Options().setQueryStringMultiMatch(
                        new MarketplaceQueryString().setQuery(searchParameter))));
        } else {
            /* When blank then do a match all. Should be avoided as its little too vague and set Fields as null. */
            q.setConditions(
                new Conditions().setOptions(
                    new Options().setQueryStringMatchAll(
                        new MarketplaceQueryString().setFields(null))));
        }

        if (StringUtils.isNotBlank(geoHash)) {
            q.getConditions().setFilter(
                new Filter().setGeoDistance(
                    new GeoDistance()
                        .setDistance(Constants.MAX_Q_SEARCH_DISTANCE_WITH_UNITS)
                        .setGeoHash(geoHash)));
        }

        LOG.info("Elastic query q={}", q.asJson());
        Search search = new Search()
            .setFrom(0)
            .setSize(paginationSize)
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
            MarketplaceElastic.INDEX
                + "/_search?pretty&filter_path=hits.hits._source&_source=BT,COR,CS,DS,EC,EI,GH,LC,MC,PI,PP,TG,TI,TO,TS",
            dslQuery
        );

        LOG.debug("DSL Query result={}", result);
        return result;
    }

    @Mobile
    public MarketplaceElasticList nearMeExcludedMarketTypes(
        List<BusinessTypeEnum> filterMustNotBusinessTypes,
        List<BusinessTypeEnum> filteringFor,
        BusinessTypeEnum searchedOnBusinessType,
        String geoHash,
        String scrollId,
        int from
    ) {
        MarketplaceElasticList marketplaceElastics = new MarketplaceElasticList();
        try {
            SearchResponse searchResponse;
            if (StringUtils.isNotBlank(scrollId)) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueMinutes(MINUTES_FOR_SEARCH));
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            } else {
                SearchRequest searchRequest = new SearchRequest(MarketplaceElastic.INDEX);
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.must(QueryBuilders.matchAllQuery());
                for (BusinessTypeEnum businessType : filterMustNotBusinessTypes) {
                    boolQueryBuilder.mustNot(QueryBuilders.matchQuery("BT", businessType.name()));
                }

                boolQueryBuilder.filter(geoDistanceQuery("GH")
                    .geohash(geoHash)
                    .distance(MAX_Q_SEARCH_DISTANCE, DistanceUnit.KILOMETERS));
                searchSourceBuilder.query(boolQueryBuilder)
                    .sort(new GeoDistanceSortBuilder("GH", geoHash).order(SortOrder.ASC))
                    .size(paginationSize);
                searchRequest.source(searchSourceBuilder);
                if (from > 0) {
                    searchSourceBuilder.from(from);
                } else {
                    searchRequest.scroll(TimeValue.timeValueMinutes(MINUTES_FOR_SEARCH));
                }

                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            }

            populateSearchData(marketplaceElastics, searchResponse.getHits().getHits());
            return marketplaceElastics.setScrollId(searchResponse.getScrollId())
                .setFrom(from + paginationSize)
                .setSize(paginationSize)
                .setSearchedOnBusinessType(searchedOnBusinessType);
        } catch (IOException e) {
            LOG.error("Failed getting marketplace data reason={}", e.getLocalizedMessage(), e);
            return marketplaceElastics;
        }
    }
}
