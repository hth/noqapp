package com.noqapp.search.elastic.repository;

import static com.noqapp.search.elastic.repository.BizStoreElasticManagerImpl.replaceCategoryIdWithCategoryName;

import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.BizStoreSpatialElastic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.Set;

/**
 * User: hitender
 * Date: 11/27/19 9:18 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class BizStoreSpatialElasticManagerImpl implements BizStoreSpatialElasticManager<BizStoreElastic> {

    private static final Logger LOG = LoggerFactory.getLogger(BizStoreElasticManagerImpl.class);

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    public BizStoreSpatialElasticManagerImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public void save(BizStoreElastic bizStoreElastic) {
        try {
            replaceCategoryIdWithCategoryName(bizStoreElastic);
            IndexRequest request = new IndexRequest(BizStoreSpatialElastic.INDEX)
                .id(bizStoreElastic.getId())
                .source(bizStoreElastic.asJson(), XContentType.JSON);

            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                LOG.info("Created elastic document successfully id={} bizStoreElastic={}", bizStoreElastic.getId(), bizStoreElastic);
            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                LOG.info("Updated elastic document id={} bizStoreElastic={}", bizStoreElastic.getId(), bizStoreElastic);
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
        DeleteRequest request = new DeleteRequest(BizStoreSpatialElastic.INDEX).id(id);

        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
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
    public void save(Set<BizStoreElastic> bizStoreElastics) {
        BulkRequest request = new BulkRequest();
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        for (BizStoreElastic bizStoreElastic : bizStoreElastics) {
            replaceCategoryIdWithCategoryName(bizStoreElastic);
            request.add(
                new IndexRequest(BizStoreSpatialElastic.INDEX)
                    .id(bizStoreElastic.getId())
                    .source(bizStoreElastic.asJson(), XContentType.JSON));
        }

        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
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
                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
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
}
