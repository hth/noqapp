package com.noqapp.view.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 11/5/19 9:19 AM
 */
@Component
public class NoQueueEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(NoQueueEventListener.class);

    private MongoTemplate mongoTemplate;
    private MongoMappingContext mongoMappingContext;

    @Autowired
    public NoQueueEventListener(MongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
    }

    /**
     * Turning on the index auto creation during launch. Auto creation of index is turned off by default.
     *
     * @param event
     */
    @EventListener
    public void initIndicesAfterStartup(ContextRefreshedEvent event) {
        LOG.info("Mongo InitIndicesAfterStartup init");
        int count = 0;
        for (BasicMongoPersistentEntity<?> persistentEntity : mongoMappingContext.getPersistentEntities()) {
            Class clazz = persistentEntity.getType();
            if (clazz.isAnnotationPresent(Document.class)) {
                count++;
                IndexOperations indexOps = mongoTemplate.indexOps(clazz);
                IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
                resolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
                LOG.info("Index initialized for {}", clazz.getName());
            }
        }
        LOG.info("Mongo InitIndicesAfterStartup initialization complete count={}", count);
    }
}
