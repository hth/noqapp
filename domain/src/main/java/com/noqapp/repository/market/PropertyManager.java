package com.noqapp.repository.market;

import com.noqapp.domain.market.PropertyEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 1/11/21 12:51 AM
 */
public interface PropertyManager extends RepositoryManager<PropertyEntity> {
    List<PropertyEntity> findByQid(String queueUserId);

    PropertyEntity findOneById(String id);
}
