package com.noqapp.repository.analytic;

import com.noqapp.domain.analytic.BizDimensionEntity;
import com.noqapp.repository.RepositoryManager;

/**
 * User: hitender
 * Date: 12/8/16 12:07 AM
 */
public interface BizDimensionManager extends RepositoryManager<BizDimensionEntity> {

    BizDimensionEntity findBy(String bizId);
}
