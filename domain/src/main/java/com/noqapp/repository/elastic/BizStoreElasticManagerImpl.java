package com.noqapp.repository.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noqapp.domain.elastic.BizStoreElasticEntity;
import com.noqapp.utils.CommonUtil;
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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
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
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * curl -XGET http://localhost:9200/noqapp_biz_store/biz_store/_search?q=country:India
 * curl http://localhost:9200/noqapp/_search/?pretty=true
 * curl -X GET http://localhost:9200/
 * curl http://localhost:9200/x/_search/?pretty=true
 * curl http://localhost:9200/noqapp/x/_search/?pretty=true
 * <p>
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
public class BizStoreElasticManagerImpl implements BizStoreElasticManager<BizStoreElasticEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(BizStoreElasticManagerImpl.class);

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    public BizStoreElasticManagerImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public void save(BizStoreElasticEntity bizStoreElastic) {
        try {
            IndexRequest request = new IndexRequest(
                    BizStoreElasticEntity.INDEX,
                    BizStoreElasticEntity.TYPE,
                    bizStoreElastic.getId())
                    .source(bizStoreElastic.asJson(), XContentType.JSON);

            IndexResponse indexResponse = restHighLevelClient.index(request, CommonUtil.getMeSomeHeader());
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                LOG.warn("Created successfully id={}", bizStoreElastic.getId());
            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                LOG.warn("Updated document id={}", bizStoreElastic.getId());
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
                BizStoreElasticEntity.INDEX,
                BizStoreElasticEntity.TYPE,
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
    public void save(List<BizStoreElasticEntity> bizStoreElastics) {
        BulkRequest request = new BulkRequest();
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        for (BizStoreElasticEntity bizStoreElastic : bizStoreElastics) {
            request.add(new IndexRequest(
                    BizStoreElasticEntity.INDEX,
                    BizStoreElasticEntity.TYPE,
                    bizStoreElastic.getId())
                    .source(bizStoreElastic.asJson(), XContentType.JSON));
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
                        created ++;
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                        updated ++;
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        deleted ++;
                    }
                }
                LOG.info("Total saved BizStore create={} update={} delete={}", created, updated, deleted);
            }
        } catch (IOException e) {
            LOG.error("Failed bulk save reason={}", e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<BizStoreElasticEntity> searchByBusinessName(String businessName) {
        List<BizStoreElasticEntity> results = new ArrayList<>();

        /* Size limits to fetching X data. Defaults to 10. */
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(matchQuery("N", "+" + businessName))
                .size(30);

        SearchRequest searchRequest = new SearchRequest(BizStoreElasticEntity.INDEX)
                .source(searchSourceBuilder)
                .scroll(TimeValue.timeValueMinutes(1L));
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            String scrollId = searchResponse.getScrollId();
            SearchHits hits = searchResponse.getHits();

            ObjectMapper objectMapper = new ObjectMapper();
            for (SearchHit searchHit : hits) {
                BizStoreElasticEntity value = objectMapper.readValue(searchHit.getSourceAsString(), BizStoreElasticEntity.class);
                value.setId(searchHit.getId());
                results.add(value);
            }
        } catch (IOException e) {
            LOG.error("Failed searching for {} reason={}", businessName, e.getLocalizedMessage(), e);
        }

        return results;
    }
}
