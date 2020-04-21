package com.noqapp.search.elastic.repository;

import java.util.Set;

/**
 * User: hitender
 * Date: 11/27/19 9:17 AM
 */
public interface BizStoreSpatialElasticManager<BizStoreElastic> {
    /**
     * Save single object.
     */
    void save(BizStoreElastic bizStoreElastic);

    /**
     * Bulk save operation.
     */
    void save(Set<BizStoreElastic> bizStoreElastics);

    /**
     * Delete by id.
     */
    void delete(String id);

    boolean exists(String id);
}
