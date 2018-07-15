package com.noqapp.view.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.pieroxy.ua.detection.UserAgentDetectionResult;
import net.pieroxy.ua.detection.UserAgentDetector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * User: hitender
 * Date: 11/19/16 7:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class CachedUserAgentStringParser {
    private static final Logger LOG = LoggerFactory.getLogger(CachedUserAgentStringParser.class);

    //Set cache parameters
    private final Cache<String, UserAgentDetectionResult> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    private CachedUserAgentStringParser() {
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        static final CachedUserAgentStringParser INSTANCE = new CachedUserAgentStringParser();

        private SingletonHolder() {
        }
    }

    public static CachedUserAgentStringParser getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public UserAgentDetectionResult parse(String userAgentString) {
        UserAgentDetectionResult result = cache.getIfPresent(userAgentString);
        if (null == result) {
            LOG.info("Cache : No : UserAgentString: {}", userAgentString);
            result = new UserAgentDetector().parseUserAgent(userAgentString);
            cache.put(userAgentString, result);
        }
        return result;
    }
}
