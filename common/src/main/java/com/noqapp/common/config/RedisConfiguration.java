package com.noqapp.common.config;

import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

/**
 * User: hitender
 * Date: 11/19/16 6:57 PM
 */
@Configuration
@EnableCaching
public class RedisConfiguration extends CachingConfigurerSupport {

    @Value ("${redis.host}")
    private String redisHost;

    @Value ("${redis.port}")
    private int redisPort;

    @Value("${redis.cache.duration}")
    private int redisCacheDuration;

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .readFrom(REPLICA_PREFERRED)
            .build();

        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        return redisTemplate;
    }

    @Bean
    RedisCacheWriter redisCacheWriter(LettuceConnectionFactory lettuceConnectionFactory) {
        return RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory);
    }

    @Bean
    RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();

        /* Number of seconds before expiration. Defaults to unlimited (0) */
        redisCacheConfiguration.entryTtl(Duration.ofSeconds(redisCacheDuration));
        redisCacheConfiguration.usePrefix();
        redisCacheConfiguration.disableCachingNullValues();

        return redisCacheConfiguration;
    }

    @Bean
    RedisCacheManager cacheManager(RedisCacheWriter redisCacheWriter, RedisCacheConfiguration redisCacheConfiguration) {
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

    /**
     * Custom key generator that will generate the key for each method to be unique by default.
     *
     * @return
     */
    @Bean
    KeyGenerator customKeyGenerator() {
        return (o, method, objects) -> {
            // This will generate a unique key of the class name, the method name,
            // and all method parameters appended.
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName());
            sb.append(method.getName());
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
}
