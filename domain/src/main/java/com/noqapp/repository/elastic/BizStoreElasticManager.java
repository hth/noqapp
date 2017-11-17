package com.noqapp.repository.elastic;

import java.util.List;

/**
 * User: hitender
 * Date: 11/193/16 1:49 AM
 */
public interface BizStoreElasticManager<BizStoreElasticEntity> {
    void save(BizStoreElasticEntity bizStoreElastic);

    void delete(String id);

    List<BizStoreElasticEntity> searchByBusinessName(String businessName);
}
