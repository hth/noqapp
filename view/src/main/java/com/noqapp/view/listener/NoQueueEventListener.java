package com.noqapp.view.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 11/5/19 9:19 AM
 */
@Component
public class NoQueueEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(NoQueueEventListener.class);

    private LettuceConnectionFactory lettuceConnectionFactory;

    @Autowired
    public NoQueueEventListener(LettuceConnectionFactory lettuceConnectionFactory) {
        this.lettuceConnectionFactory = lettuceConnectionFactory;
    }

    @EventListener
    public void initRedis(ContextRefreshedEvent event) {
        if (lettuceConnectionFactory.getConnection().isClosed()) {
            LOG.error("Redis connection failed");
            throw new RuntimeException("Redis connection failed");
        }
        LOG.info("Redis pinged on port={} response={}", lettuceConnectionFactory.getPort(), lettuceConnectionFactory.getConnection().ping());
    }
}
