package com.token.service.analytic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.analytic.BizDimensionEntity;
import com.token.repository.analytic.BizDimensionManager;

/**
 * User: hitender
 * Date: 12/8/16 12:05 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BizDimensionService {

    private BizDimensionManager bizDimensionManager;

    @Autowired
    public BizDimensionService(BizDimensionManager bizDimensionManager) {
        this.bizDimensionManager = bizDimensionManager;
    }

    public BizDimensionEntity findBy(String bizId) {
        return bizDimensionManager.findBy(bizId);
    }
}