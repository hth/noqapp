package com.noqapp.service;

import java.util.Set;

import com.noqapp.repository.BizNameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 12/9/16 1:56 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class FetcherService {
    private static final Logger LOG = LoggerFactory.getLogger(FetcherService.class);

    private final BizNameManager bizNameManager;

    @Autowired
    public FetcherService(BizNameManager bizNameManager) {
        this.bizNameManager = bizNameManager;
    }

    /**
     * This method is called from AJAX to get the matching list of users in the system.
     *
     * @param bizName
     * @return
     */
    public Set<String> findAllDistinctBizName(String bizName) {
        LOG.debug("Search for Biz Name={}", bizName);
        Set<String> titles = bizNameManager.findAllDistinctBizStr(bizName);
        LOG.debug("found business count={}", titles.size());
        return titles;
    }
}
