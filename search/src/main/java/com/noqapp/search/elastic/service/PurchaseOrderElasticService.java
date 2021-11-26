package com.noqapp.search.elastic.service;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.UserAddressManager;
import com.noqapp.search.elastic.domain.PurchaseOrderElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.repository.PurchaseOrderElasticManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * hitender
 * 11/14/21 5:36 PM
 */
@Service
public class PurchaseOrderElasticService {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderElasticService.class);

    /** Include field are the fields to be included upon completing the search. */
    static String[] includeFields = new String[]{"AR", "QID", "BS", "BT", "COR", "GH", "OP", "TO", "DT", "ST"};
    static String[] excludeFields = new String[]{"_type"};

    private PurchaseOrderElasticManager<PurchaseOrderElastic> purchaseOrderElasticManager;
    private PurchaseOrderManager purchaseOrderManager;
    private UserAddressManager userAddressManager;
    private ApiHealthService apiHealthService;

    @Autowired
    public PurchaseOrderElasticService(
        PurchaseOrderElasticManager<PurchaseOrderElastic> purchaseOrderElasticManager,
        PurchaseOrderManager purchaseOrderManager,
        UserAddressManager userAddressManager,
        ApiHealthService apiHealthService
    ) {
        this.purchaseOrderElasticManager = purchaseOrderElasticManager;
        this.purchaseOrderManager = purchaseOrderManager;
        this.userAddressManager = userAddressManager;
        this.apiHealthService = apiHealthService;
    }

    @Async
    public void save(PurchaseOrderElastic purchaseOrderElastic) {
        purchaseOrderElasticManager.save(purchaseOrderElastic);
    }

    @Async
    void save(List<PurchaseOrderElastic> purchaseOrderElastics) {
        LOG.info("Bulk save size={}", purchaseOrderElastics.size());
        if (!purchaseOrderElastics.isEmpty()) {
            purchaseOrderElasticManager.save(purchaseOrderElastics);
        }
    }

    @Async
    public void save(JsonPurchaseOrder jsonPurchaseOrder) {
        LOG.info("Saving to elastic {} {}", jsonPurchaseOrder.getTransactionId(), jsonPurchaseOrder.getQueueUserId());
        if (null != jsonPurchaseOrder.getUserAddressId()) {
            UserAddressEntity userAddress = userAddressManager.findById(jsonPurchaseOrder.getUserAddressId());
            save(DomainConversion.getAsPurchaseOrderElastic(jsonPurchaseOrder, userAddress));
        } else {
            LOG.warn("Missing addressId for transactionId={}", jsonPurchaseOrder.getTransactionId());
        }
    }

    @Async
    public void remove(JsonPurchaseOrder jsonPurchaseOrder) {
        purchaseOrderElasticManager.delete(jsonPurchaseOrder.getTransactionId());
    }

    public void addAllPurchaseOrderToElastic() {
        Instant start = Instant.now();
        long countPurchaseOrderElastic = 0;
        try (Stream<PurchaseOrderEntity> stream = purchaseOrderManager.findAllWithStream()) {
            List<PurchaseOrderElastic> purchaseOrderElastics = new ArrayList<>();
            stream.iterator().forEachRemaining(purchaseOrder -> {
                PurchaseOrderElastic purchaseOrderElastic = null;
                try {
                    if (null != purchaseOrder.getUserAddressId()) {
                        UserAddressEntity userAddress = userAddressManager.findById(purchaseOrder.getUserAddressId());
                        purchaseOrderElastic = DomainConversion.getAsPurchaseOrderElastic(purchaseOrder, userAddress);
                        purchaseOrderElastics.add(purchaseOrderElastic);
                    } else {
                        LOG.warn("Missing addressId for transactionId={}", purchaseOrder.getTransactionId());
                    }
                } catch (Exception e) {
                    LOG.error("Failed to insert purchaseOrder in elastic data={} reason={}",
                        purchaseOrderElastic,
                        e.getLocalizedMessage(),
                        e);
                }
            });
            save(purchaseOrderElastics);
            countPurchaseOrderElastic += purchaseOrderElastics.size();
        }

        apiHealthService.insert(
            "/addAllPurchaseOrderToElastic",
            "addAllPurchaseOrderToElastic",
            this.getClass().getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        LOG.info("Added countPurchaseOrderElastic={} to Elastic in duration={}",
            countPurchaseOrderElastic,
            Duration.between(start, Instant.now()).toMillis());
    }
}
