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
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.stereotype.Component;

import org.neo4j.driver.exceptions.ClientException;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
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
    private Neo4jTransactionManager neo4jTransactionManager;

    @Autowired
    public LoaderInitializationBean(
        @Value("${ftp.location}")
        String ftpLocation,

        MongoTemplate mongoTemplate,
        MongoMappingContext mongoMappingContext,
        FtpService ftpService,
        MasterLabService masterLabService,
        Neo4jTransactionManager neo4jTransactionManager
    ) {
        this.ftpLocation = ftpLocation;

        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
        this.ftpService = ftpService;
        this.masterLabService = masterLabService;
        this.neo4jTransactionManager = neo4jTransactionManager;
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

    @PostConstruct
    public void checkNeo4j() {
        try {
            Session session = neo4jTransactionManager.getSessionFactory().openSession();
            Result resultOnConstraints = session.query("CALL db.constraints", Collections.EMPTY_MAP);

            Set<String> constraintIds = new HashSet<>();
            resultOnConstraints.queryResults().forEach(x -> constraintIds.add((String) x.get("name")));

            if (!constraintIds.contains("person_unique_qid")) {
                Result result = session.query("CREATE CONSTRAINT person_unique_qid ON (p:Person) ASSERT p.qid IS UNIQUE;", Collections.EMPTY_MAP);
                LOG.info("Constraint on QID in Person added={}", result.queryStatistics().containsUpdates());
            } else {
                LOG.info("Constraint found person_unique_qid");
            }

            if (!constraintIds.contains("store_unique_codeQR")) {
                Result result = session.query("CREATE CONSTRAINT store_unique_codeQR ON (s:Store) ASSERT s.codeQR IS UNIQUE;", Collections.EMPTY_MAP);
                LOG.info("Constraint on CodeQR in Store added={}", result.queryStatistics().containsUpdates());
            } else {
                LOG.info("Constraint found store_unique_codeQR");
            }

            if (!constraintIds.contains("business_customer_unique_id")) {
                Result result = session.query("CREATE CONSTRAINT business_customer_unique_id ON (b:BusinessCustomer) ASSERT b.businessCustomerId IS UNIQUE;", Collections.EMPTY_MAP);
                LOG.info("Constraint on BusinessCustomerId in BusinessCustomer added={}", result.queryStatistics().containsUpdates());
            } else {
                LOG.info("Constraint found business_customer_unique_id");
            }

            if (!constraintIds.contains("biz_name_unique_id")) {
                Result result = session.query("CREATE CONSTRAINT biz_name_unique_id ON (b:BizName) ASSERT b.id IS UNIQUE;", Collections.EMPTY_MAP);
                LOG.info("Constraint on id in BizName added={}", result.queryStatistics().containsUpdates());
            } else {
                LOG.info("Constraint found biz_name_unique_id");
            }
        } catch (ClientException ex) {
            LOG.error("Failed creating constraint reason={}", ex.getLocalizedMessage(), ex);
        }
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
