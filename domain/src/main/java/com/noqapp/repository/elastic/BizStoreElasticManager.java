package com.noqapp.repository.elastic;

import java.util.List;

/**
 * User: hitender
 * Date: 11/193/16 1:49 AM
 */
public interface BizStoreElasticManager<BizStoreElasticEntity> {
    /**
     * Save single object.
     *
     * @param bizStoreElastic
     */
    void save(BizStoreElasticEntity bizStoreElastic);

    /**
     * Bulk save operation.
     *
     * @param bizStoreElastics
     */
    void save(List<BizStoreElasticEntity> bizStoreElastics);

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
    List<BizStoreElasticEntity> searchByBusinessName(String businessName, int limitRecords);
}
