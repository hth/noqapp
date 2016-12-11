package com.token.repository.analytic;

import com.token.domain.analytic.BizDimensionEntity;
import com.token.repository.RepositoryManager;

/**
 * User: hitender
 * Date: 12/8/16 12:07 AM
 */
public interface BizDimensionManager extends RepositoryManager<BizDimensionEntity> {

    BizDimensionEntity findBy(String bizId);
}
