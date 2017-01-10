package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.BusinessUserStoreEntity;
import com.token.repository.BusinessUserStoreManager;

/**
 * User: hitender
 * Date: 12/14/16 12:19 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BusinessUserStoreService {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserStoreService.class);

    private BusinessUserStoreManager businessUserStoreManager;

    @Autowired
    public BusinessUserStoreService(BusinessUserStoreManager businessUserStoreManager) {
        this.businessUserStoreManager = businessUserStoreManager;
    }

    public void save(BusinessUserStoreEntity businessUserStore) {
        businessUserStoreManager.save(businessUserStore);
    }

    public boolean hasAccess(String rid, String codeQR) {
        return businessUserStoreManager.hasAccess(rid, codeQR);
    }
}
