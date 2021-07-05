package com.noqapp.common.config;

import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * User: hitender
 * Date: 11/19/16 6:57 PM
 */
@Configuration
@EnableCaching
public class RedisConfiguration extends CachingConfigurerSupport {
    private static final Logger LOG = LoggerFactory.getLogger(RedisConfiguration.class);

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.cache.duration}")
    private int redisCacheDuration;

    @Value("${redis.clusters}")
    private String redisCluster;

    @Value("#{'${redis.cacheNames}'.split(',')}")
    private List<String> cacheNames;

    private Environment environment;

    @Autowired
    public RedisConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        return redisTemplate;
    }

    @Bean
    StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(lettuceConnectionFactory());
    }

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .readFrom(REPLICA_PREFERRED)
            .build();

        if (Objects.requireNonNull(environment.getProperty("build.env")).equalsIgnoreCase("dev")) {
            return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort), clientConfig);
        } else {
            return new LettuceConnectionFactory(redisClusterConfiguration(), clientConfig);
        }
    }

    private RedisClusterConfiguration redisClusterConfiguration() {
        String[] splitServers = redisCluster.split(",");
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        for (String redisServer : splitServers) {
            clusterConfiguration.clusterNode(redisServer, redisPort);
        }
        return clusterConfiguration;
    }

    @Bean
    RedisCacheWriter redisCacheWriter(LettuceConnectionFactory lettuceConnectionFactory) {
        return RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory);
    }

    @Bean
    RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();

        /* Number of seconds before expiration. Defaults to unlimited (0) */
        redisCacheConfiguration.entryTtl(Duration.ofMinutes(redisCacheDuration));
        redisCacheConfiguration.usePrefix();
        redisCacheConfiguration.disableCachingNullValues();

        return redisCacheConfiguration;
    }

    @Bean
    RedisCacheManager cacheManager(RedisCacheWriter redisCacheWriter, RedisCacheConfiguration redisCacheConfiguration) {
        Map<String, RedisCacheConfiguration> cacheNamesConfigurationMap = new HashMap<>();
        for (String cacheName : cacheNames) {
            switch (cacheName) {
                case "access-codeQR":
                    cacheNamesConfigurationMap.put("access-codeQR", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisCacheDuration)));
                    LOG.info("Setting time for cacheName={} duration={} minutes", cacheName, redisCacheDuration);
                    break;
                case "access-store":
                    cacheNamesConfigurationMap.put("access-store", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisCacheDuration)));
                    LOG.info("Setting time for cacheName={} duration={} minutes", cacheName, redisCacheDuration);
                    break;
                case "bizName-valid-codeQR":
                    cacheNamesConfigurationMap.put("bizName-valid-codeQR", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisCacheDuration)));
                    LOG.info("Setting time for cacheName={} duration={} minutes", cacheName, redisCacheDuration);
                    break;
                case "bizStore-codeQR":
                    cacheNamesConfigurationMap.put("bizStore-codeQR", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisCacheDuration)));
                    LOG.info("Setting time for cacheName={} duration={} minutes", cacheName, redisCacheDuration);
                    break;
                case "bizStore-valid-codeQR":
                    cacheNamesConfigurationMap.put("bizStore-valid-codeQR", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisCacheDuration)));
                    LOG.info("Setting time for cacheName={} duration={} minutes", cacheName, redisCacheDuration);
                    break;
                case "flexAppointment":
                    cacheNamesConfigurationMap.put("flexAppointment", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
                    LOG.info("Setting time for cacheName={} duration={} hour", cacheName, 1);
                    break;
                case "mail-auth":
                    cacheNamesConfigurationMap.put("mail-auth", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisCacheDuration)));
                    LOG.info("Setting time for cacheName={} duration={} minutes", cacheName, redisCacheDuration);
                    break;
                default:
                    LOG.error("Reached unreachable condition {}", cacheName);
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
        }

        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration, cacheNamesConfigurationMap);
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
