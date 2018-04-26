package com.noqapp.search.elastic.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizCategoryEntity;
import com.noqapp.repository.BizCategoryManager;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * User: hitender
 * Date: 11/193/16 1:49 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BizStoreElasticManagerImpl implements BizStoreElasticManager<BizStoreElastic> {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreElasticManagerImpl.class);

    private RestHighLevelClient restHighLevelClient;
    private BizCategoryManager bizCategoryManager;

    //Set cache parameters
    private final Cache<String, Map<String, BizCategoryEntity>> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    @Autowired
    public BizStoreElasticManagerImpl(
            RestHighLevelClient restHighLevelClient,
            BizCategoryManager bizCategoryManager
    ) {
        this.restHighLevelClient = restHighLevelClient;
        this.bizCategoryManager = bizCategoryManager;
    }

    @Override
    public void save(BizStoreElastic bizStoreElastic) {
        try {
            replaceCategoryIdWithCategoryName(bizStoreElastic);
            IndexRequest request = new IndexRequest(
                    BizStoreElastic.INDEX,
                    BizStoreElastic.TYPE,
                    bizStoreElastic.getId())
                    .source(bizStoreElastic.asJson(), XContentType.JSON);

            IndexResponse indexResponse = restHighLevelClient.index(request, CommonUtil.getMeSomeHeader());
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                LOG.info("Created elastic document successfully id={}", bizStoreElastic.getId());
            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                LOG.info("Updated elastic document id={}", bizStoreElastic.getId());
            }

            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                LOG.warn("Less number of shards available id={}", bizStoreElastic.getId());
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    LOG.warn("Failed on Shard id={} reason={}", bizStoreElastic.getId(), failure.reason());
                }
            }
        } catch (IOException e) {
            LOG.error("Failed saving id={} reason={}", bizStoreElastic.getId(), e.getLocalizedMessage(), e);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
                LOG.error("Failed on version conflict id={} reason={}", bizStoreElastic.getId(), e.getDetailedMessage(), e);
            } else {
                LOG.error("Failed saving id={} reason={}", bizStoreElastic.getId(), e.getDetailedMessage(), e);
            }
        }
    }

    @Override
    public void delete(String id) {
        DeleteRequest request = new DeleteRequest(
                BizStoreElastic.INDEX,
                BizStoreElastic.TYPE,
                id);

        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(request, CommonUtil.getMeSomeHeader());
            if (deleteResponse.getResult() == DocWriteResponse.Result.CREATED) {
                LOG.warn("Created successfully id={}", id);
            } else if (deleteResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                LOG.warn("Updated document id={}", id);
            }

            ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                LOG.warn("Less number of shards available id={}", id);
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    LOG.warn("Failed on Shard id={} reason={}", id, failure.reason());
                }
            }
        } catch (IOException e) {
            LOG.error("Failed deleting id={} reason={}", id, e.getLocalizedMessage(), e);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
                LOG.error("Failed on version conflict id={} reason={}", id, e.getDetailedMessage(), e);
            } else {
                LOG.error("Failed saving id={} reason={}", id, e.getDetailedMessage(), e);
            }
        }
    }

    @Override
    public void save(List<BizStoreElastic> bizStoreElastics) {
        BulkRequest request = new BulkRequest();
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        for (BizStoreElastic bizStoreElastic : bizStoreElastics) {
            replaceCategoryIdWithCategoryName(bizStoreElastic);
            request.add(
                    new IndexRequest(
                            BizStoreElastic.INDEX,
                            BizStoreElastic.TYPE,
                            /*
                             *  Recommend to remove your id for indexing as it checks before insert,
                             *  that slows the process of insert. But having your existing index
                             *  helps shorten data, and easy lookup by id.
                             */
                            bizStoreElastic.getId()
                    ).source(bizStoreElastic.asJson(), XContentType.JSON));
        }

        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, CommonUtil.getMeSomeHeader());
            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (bulkItemResponse.isFailed()) {
                        BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                        LOG.info("Failed during saving id={} message={} cause={} status={}",
                                failure.getId(), failure.getMessage(), failure.getCause(), failure.getStatus());
                    }
                }
            } else {
                long created = 0, updated = 0, deleted = 0;
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                            || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                        created++;
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                        updated++;
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        deleted++;
                    }
                }
                LOG.info("Total saved BizStore create={} update={} delete={}", created, updated, deleted);
            }
        } catch (IOException e) {
            LOG.error("Failed bulk save reason={}", e.getLocalizedMessage(), e);
        }
    }

    private void replaceCategoryIdWithCategoryName(BizStoreElastic bizStoreElastic) {
        Map<String, BizCategoryEntity> categories;
        categories = cache.getIfPresent(bizStoreElastic.getBizNameId());
        if (null == categories) {
            List<BizCategoryEntity> bizCategories = bizCategoryManager.getByBizNameId(bizStoreElastic.getBizNameId());
            if (!bizCategories.isEmpty()) {
                categories = bizCategories.stream().collect(Collectors.toMap(BizCategoryEntity::getId, s1 -> s1));
                cache.put(bizStoreElastic.getBizNameId(), categories);
            } else {
                categories = new HashMap<>();
                cache.put(bizStoreElastic.getBizNameId(), categories);
            }
        }

        BizCategoryEntity cacheOfBizCategory = categories.getOrDefault(bizStoreElastic.getBizCategoryId(), null);
        if (null != cacheOfBizCategory) {
            bizStoreElastic.setBizCategoryName(cacheOfBizCategory.getCategoryName());
            bizStoreElastic.setBizCategoryDisplayImage(cacheOfBizCategory.getDisplayImage());
        } else {
            bizStoreElastic.setBizCategoryName("");
            bizStoreElastic.setBizCategoryDisplayImage("");
        }
    }

    @Override
    public List<BizStoreElastic> searchByBusinessName(String businessName, int limitRecords) {
        List<BizStoreElastic> results = new ArrayList<>();

        /* Size limits to fetching X data. Defaults to 10. */
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(matchQuery("N", "+" + businessName))
                .size(limitRecords);

        SearchRequest searchRequest = new SearchRequest(BizStoreElastic.INDEX)
                .source(searchSourceBuilder)
                .scroll(TimeValue.timeValueSeconds(10L));
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            String scrollId = searchResponse.getScrollId();
            SearchHits hits = searchResponse.getHits();

            ObjectMapper objectMapper = new ObjectMapper();
            for (SearchHit searchHit : hits) {
                BizStoreElastic value = objectMapper.readValue(searchHit.getSourceAsString(), BizStoreElastic.class);
                value.setId(searchHit.getId());
                value.setScrollId(scrollId);
                results.add(value);
            }
        } catch (IOException e) {
            LOG.error("Failed searching for {} reason={}", businessName, e.getLocalizedMessage(), e);
        }

        return results;
    }

    @Override
    public List<BizStoreElastic> searchByScrollId(String scrollId) {
        List<BizStoreElastic> results = new ArrayList<>();

        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueSeconds(10L));

        try {
            SearchResponse searchResponse = restHighLevelClient.searchScroll(scrollRequest);
            scrollId = searchResponse.getScrollId();
            SearchHits hits = searchResponse.getHits();

            if (0 == hits.getHits().length) {
                ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest);
                boolean succeeded = clearScrollResponse.isSucceeded();
                int released = clearScrollResponse.getNumFreed();
                LOG.info("Removed scrollId status={} released={}", succeeded, released);
                return results;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            for (SearchHit searchHit : hits) {
                BizStoreElastic value = objectMapper.readValue(searchHit.getSourceAsString(), BizStoreElastic.class);
                value.setId(searchHit.getId());
                value.setScrollId(scrollId);
                results.add(value);
            }
        } catch (IOException e) {
            LOG.error("Failed searching for {} reason={}", scrollId, e.getLocalizedMessage(), e);
        }

        return results;
    }
}
