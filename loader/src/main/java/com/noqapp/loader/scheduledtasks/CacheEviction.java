package com.noqapp.loader.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * User: hitender
 * Date: 12/19/19 12:16 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class CacheEviction {
    private static final Logger LOG = LoggerFactory.getLogger(CacheEviction.class);

    private CacheManager cacheManager;

    @Autowired
    public CacheEviction(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedDelayString = "${loader.CacheEviction.evictAllCachesAtIntervals}")
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
    }

    public void evictAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            LOG.info("Cache evicted cacheName={}", cacheName);
        }
        cacheManager.getCacheNames().forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }
}
