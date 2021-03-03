package com.noqapp.search.elastic.repository;

import java.util.List;

/**
 * hitender
 * 3/1/21 11:33 AM
 */
public interface MarketplaceElasticManager<MarketplaceElastic> {
    /**
     * Save single object.
     */
    void save(MarketplaceElastic marketplaceElastic);

    /**
     * Bulk save operation.
     */
    void save(List<MarketplaceElastic> marketplaceElastics);

    /**
     * Delete by id.
     */
    void delete(String id);

    boolean exists(String id);
}
