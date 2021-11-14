package com.noqapp.search.elastic.repository;

import java.util.List;

/**
 * hitender
 * 11/14/21 6:19 PM
 */
public interface PurchaseOrderElasticManager<PurchaseOrderElastic> {
    /** Save single object. */
    void save(PurchaseOrderElastic purchaseOrderElastic);

    /** Bulk save operation. */
    void save(List<PurchaseOrderElastic> purchaseOrderElastics);

    /** Delete by id. */
    void delete(String id);

    boolean exists(String id);
}
