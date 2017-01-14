package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.BusinessUserStoreEntity;
import com.token.domain.TokenQueueEntity;
import com.token.domain.json.JsonToken;
import com.token.repository.BusinessUserStoreManager;

import java.util.ArrayList;
import java.util.List;

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
    private TokenQueueService tokenQueueService;

    @Autowired
    public BusinessUserStoreService(BusinessUserStoreManager businessUserStoreManager, TokenQueueService tokenQueueService) {
        this.businessUserStoreManager = businessUserStoreManager;
        this.tokenQueueService = tokenQueueService;
    }

    public void save(BusinessUserStoreEntity businessUserStore) {
        businessUserStoreManager.save(businessUserStore);
    }

    public boolean hasAccess(String rid, String codeQR) {
        return businessUserStoreManager.hasAccess(rid, codeQR);
    }

    public List<JsonToken> getQueues(String rid) {
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.getQueues(rid, 10);

        String[] codes = new String[10];
        int i = 0;
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            codes[i] = businessUserStore.getCodeQR();
        }

        List<TokenQueueEntity> tokenQueues = tokenQueueService.getTokenQueue(codes);
        List<JsonToken> jsonTokens = new ArrayList<>();
        for(TokenQueueEntity tokenQueue : tokenQueues) {
            jsonTokens.add(new JsonToken(tokenQueue));
        }

        return jsonTokens;
    }
}
