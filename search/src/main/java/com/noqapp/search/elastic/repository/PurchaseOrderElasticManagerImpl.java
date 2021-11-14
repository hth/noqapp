package com.noqapp.search.elastic.repository;

import com.noqapp.search.elastic.domain.PurchaseOrderElastic;

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
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import java.io.IOException;
import java.util.List;

/**
 * hitender
 * 11/14/21 6:19 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class PurchaseOrderElasticManagerImpl implements PurchaseOrderElasticManager<PurchaseOrderElastic> {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderElasticManagerImpl.class);

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    public PurchaseOrderElasticManagerImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public void save(PurchaseOrderElastic purchaseOrderElastic) {
        try {
            IndexRequest request = new IndexRequest(PurchaseOrderElastic.INDEX)
                .id(purchaseOrderElastic.getId())
                .source(purchaseOrderElastic.asJson(), XContentType.JSON);

            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            if (DocWriteResponse.Result.CREATED == indexResponse.getResult()) {
                LOG.info("Created elastic document successfully id={} {}", purchaseOrderElastic.getId(), indexResponse);
            } else if (DocWriteResponse.Result.UPDATED == indexResponse.getResult()) {
                LOG.info("Updated elastic document id={} {}", purchaseOrderElastic.getId(), indexResponse);
            }

            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                LOG.warn("Less number of shards available id={}", purchaseOrderElastic.getId());
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    LOG.warn("Failed on Shard id={} reason={}", purchaseOrderElastic.getId(), failure.reason());
                }
            }
        } catch (IOException e) {
            LOG.error("Failed saving id={} reason={}", purchaseOrderElastic.getId(), e.getLocalizedMessage(), e);
        } catch (ElasticsearchException e) {
            if (RestStatus.CONFLICT == e.status()) {
                LOG.error("Failed on version conflict id={} reason={}", purchaseOrderElastic.getId(), e.getDetailedMessage(), e);
            } else {
                LOG.error("Failed saving id={} reason={}", purchaseOrderElastic.getId(), e.getDetailedMessage(), e);
            }
        }
    }

    @Override
    public void save(List<PurchaseOrderElastic> purchaseOrderElastics) {
        BulkRequest request = new BulkRequest();
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        for (PurchaseOrderElastic purchaseOrderElastic : purchaseOrderElastics) {
            request.add(
                new IndexRequest(PurchaseOrderElastic.INDEX)
                    .id(purchaseOrderElastic.getId())
                    .source(purchaseOrderElastic.asJson(), XContentType.JSON));
        }

        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (bulkItemResponse.isFailed()) {
                        BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                        LOG.error("Failed during saving id={} message={} cause={} status={}",
                            failure.getId(), failure.getMessage(), failure.getCause(), failure.getStatus());
                    }
                }
            } else {
                long created = 0, updated = 0, deleted = 0;
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (DocWriteRequest.OpType.INDEX == bulkItemResponse.getOpType() || DocWriteRequest.OpType.CREATE == bulkItemResponse.getOpType()) {
                        created++;
                    } else if (DocWriteRequest.OpType.UPDATE == bulkItemResponse.getOpType()) {
                        updated++;
                    } else if (DocWriteRequest.OpType.DELETE == bulkItemResponse.getOpType()) {
                        deleted++;
                    }
                }
                LOG.info("Total saved PurchaseOrder create={} update={} delete={}", created, updated, deleted);
            }
        } catch (IOException e) {
            LOG.error("Failed bulk save reason={}", e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        DeleteRequest request = new DeleteRequest(PurchaseOrderElastic.INDEX).id(id);

        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            if (DocWriteResponse.Result.DELETED == deleteResponse.getResult()) {
                LOG.info("Deleted elastic document successfully id={} response={}", id, deleteResponse);
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
            if (RestStatus.CONFLICT == e.status()) {
                LOG.error("Failed on version conflict id={} reason={}", id, e.getDetailedMessage(), e);
            } else {
                LOG.error("Failed saving id={} reason={}", id, e.getDetailedMessage(), e);
            }
        }
    }

    @Override
    public boolean exists(String id) {
        GetRequest request = new GetRequest(PurchaseOrderElastic.INDEX).id(id);
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");

        try {
            return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOG.error("Failed finding record={} reason={}", id, e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed find record");
        }
    }
}
