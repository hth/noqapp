package com.noqapp.search.elastic.repository;

import java.util.List;

/**
 * User: hitender
 * Date: 11/193/16 1:49 AM
 */
public interface BizStoreElasticManager<BizStoreElastic> {
    /**
     * Save single object.
     */
    void save(BizStoreElastic bizStoreElastic);

    /**
     * Bulk save operation.
     */
    void save(List<BizStoreElastic> bizStoreElastics);

    /**
     * Delete by id.
     */
    void delete(String id);

    /**
     * Search by business name.
     */
    List<BizStoreElastic> searchByBusinessName(String businessName, int limitRecords);
    List<BizStoreElastic> searchByScrollId(String scrollId);
}
