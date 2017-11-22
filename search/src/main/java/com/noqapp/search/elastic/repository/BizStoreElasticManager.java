package com.noqapp.search.elastic.repository;

import java.util.List;

/**
 * User: hitender
 * Date: 11/193/16 1:49 AM
 */
public interface BizStoreElasticManager<BizStoreElastic> {
    /**
     * Save single object.
     *
     * @param bizStoreElastic
     */
    void save(BizStoreElastic bizStoreElastic);

    /**
     * Bulk save operation.
     *
     * @param bizStoreElastics
     */
    void save(List<BizStoreElastic> bizStoreElastics);

    /**
     * Delete by id.
     *
     * @param id
     */
    void delete(String id);

    /**
     * Search by business name.
     *
     * @param businessName
     * @return
     */
    List<BizStoreElastic> searchByBusinessName(String businessName, int limitRecords);
}
