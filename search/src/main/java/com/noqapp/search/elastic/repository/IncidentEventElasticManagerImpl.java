package com.noqapp.search.elastic.repository;

import com.noqapp.search.elastic.domain.IncidentEventElastic;

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
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.List;

/**
 * hitender
 * 5/30/21 9:19 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Repository
public class IncidentEventElasticManagerImpl implements IncidentEventElasticManager<IncidentEventElastic> {
    private static final Logger LOG = LoggerFactory.getLogger(IncidentEventElasticManagerImpl.class);

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    public IncidentEventElasticManagerImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public void save(IncidentEventElastic incidentEventElastic) {
        try {
            IndexRequest request = new IndexRequest(IncidentEventElastic.INDEX)
                .id(incidentEventElastic.getId())
                .source(incidentEventElastic.asJson(), XContentType.JSON);

            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                LOG.info("Created elastic document successfully id={} incidentEventElastic={}", incidentEventElastic.getId(), incidentEventElastic);
            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                LOG.info("Updated elastic document id={} incidentEventElastic={}", incidentEventElastic.getId(), incidentEventElastic);
            }

            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                LOG.warn("Less number of shards available id={}", incidentEventElastic.getId());
            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    LOG.warn("Failed on Shard id={} reason={}", incidentEventElastic.getId(), failure.reason());
                }
            }
        } catch (IOException e) {
            LOG.error("Failed saving id={} reason={}", incidentEventElastic.getId(), e.getLocalizedMessage(), e);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
                LOG.error("Failed on version conflict id={} reason={}", incidentEventElastic.getId(), e.getDetailedMessage(), e);
            } else {
                LOG.error("Failed saving id={} reason={}", incidentEventElastic.getId(), e.getDetailedMessage(), e);
            }
        }
    }

    @Override
    public void save(List<IncidentEventElastic> incidentEventElastics) {
        BulkRequest request = new BulkRequest();
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        for (IncidentEventElastic incidentEventElastic : incidentEventElastics) {
            request.add(
                new IndexRequest(IncidentEventElastic.INDEX)
                    .id(incidentEventElastic.getId())
                    .source(incidentEventElastic.asJson(), XContentType.JSON));
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
                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                        created++;
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                        updated++;
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        deleted++;
                    }
                }
                LOG.info("Total saved IncidentEvent create={} update={} delete={}", created, updated, deleted);
            }
        } catch (IOException e) {
            LOG.error("Failed bulk save reason={}", e.getLocalizedMessage(), e);
        }
    }
}
