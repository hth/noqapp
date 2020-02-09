package com.noqapp.loader.listener;

import com.noqapp.medical.service.MasterLabService;
import com.noqapp.service.FtpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;

/**
 * hitender
 * 5/27/18 4:25 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class LoaderInitializationBean {
    private static final Logger LOG = LoggerFactory.getLogger(LoaderInitializationBean.class);

    private String ftpLocation;

    private MongoTemplate mongoTemplate;
    private MongoMappingContext mongoMappingContext;
    private FtpService ftpService;
    private MasterLabService masterLabService;

    @Autowired
    public LoaderInitializationBean(
        @Value("${ftp.location}")
        String ftpLocation,

        MongoTemplate mongoTemplate,
        MongoMappingContext mongoMappingContext,
        FtpService ftpService,
        MasterLabService masterLabService
    ) {
        this.ftpLocation = ftpLocation;

        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
        this.ftpService = ftpService;
        this.masterLabService = masterLabService;
    }

    @PostConstruct
    public void checkIfDirectoryExists() {
        for (String directoryName : FtpService.directories) {
            File directory = new File(ftpLocation + directoryName);
            if (directory.exists()) {
                LOG.info("Directory found={}", directory.toURI());
            } else {
                boolean status = ftpService.createFolder(directoryName);
                LOG.info("Directory created={} status={}", directory.toURI(), status);
                if (!status) {
                    LOG.error("Failed creating directory={}", directoryName);
                    throw new RuntimeException("Failed creating directory " + directoryName);
                }
            }
        }
    }

    @PostConstruct
    public void createMasterFiles() {
        try {
            masterLabService.createMasterFiles();
        } catch (IOException e) {
            LOG.error("Failed creating masterFiles reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed creating masterFiles");
        }
    }

    /** Turning on the index auto creation during launch. Auto creation of index is turned off by default. */
    @PostConstruct
    public void initIndicesAfterStartup() {
        LOG.info("Mongo InitIndicesAfterStartup init");
        int domainCount = ensureIndexForPackage("com.noqapp.domain");
        int medicalCount = ensureIndexForPackage("com.noqapp.medical.domain");
        LOG.info("Mongo InitIndicesAfterStartup initialization complete domainCount={} medicalCount={}", domainCount, medicalCount);
    }

    private int ensureIndexForPackage(String packageName) {
        int count = 0;
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(Document.class);
        for (Class clazz : allClasses) {
            if (clazz.isAnnotationPresent(Document.class)) {
                count++;
                IndexOperations indexOps = mongoTemplate.indexOps(clazz);
                IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
                resolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
                LOG.info("Index initialized for {}", clazz.getName());
            }
        }
        return count;
    }
}
