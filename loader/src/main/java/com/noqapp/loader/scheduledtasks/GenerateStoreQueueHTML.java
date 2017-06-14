package com.noqapp.loader.scheduledtasks;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.CronStatsEntity;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.service.CronStatsService;
import com.noqapp.service.ShowHTMLService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * User: hitender
 * Date: 6/14/17 6:29 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class GenerateStoreQueueHTML {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateStoreQueueHTML.class);

    private BizStoreManager bizStoreManager;
    private ShowHTMLService showHTMLService;
    private CronStatsService cronStatsService;
    private String staticHTMLSwitch;
    private String baseDirectory;

    private CronStatsEntity cronStats;

    @Autowired
    public GenerateStoreQueueHTML(
            @Value ("${GenerateStoreQueueHTML.staticHTMLSwitch:ON}")
            String staticHTMLSwitch,

            @Value ("${GenerateStoreQueueHTML.base.directory:/tmp/biz}")
            String baseDirectory,

            BizStoreManager bizStoreManager,
            ShowHTMLService showHTMLService,
            CronStatsService cronStatsService
    ) {
        this.staticHTMLSwitch = staticHTMLSwitch;
        this.baseDirectory = baseDirectory;

        this.bizStoreManager = bizStoreManager;
        this.showHTMLService = showHTMLService;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (cron = "${loader.GenerateStoreQueueHTML.generateHTMLPages}")
    public void generateHTMLPages() {
        cronStats = new CronStatsEntity(
                GenerateStoreQueueHTML.class.getName(),
                "Generate_Store_Queue_HTML",
                staticHTMLSwitch);

        int found = 0, failure = 0, generated = 0, skipped = 0;
        if ("OFF".equalsIgnoreCase(staticHTMLSwitch)) {
            LOG.debug("feature is {}", staticHTMLSwitch);
        }

        try {
            int i = 1;
            do {
                Path pathToTxtFile = Paths.get(baseDirectory + System.getProperty("file.separator") + "all.txt");
                Files.deleteIfExists(pathToTxtFile);
                Files.createDirectories(pathToTxtFile.getParent());
                Files.createFile(pathToTxtFile);

                List<BizStoreEntity> bizStores = bizStoreManager.getAll(i, 1000);
                for (BizStoreEntity bizStore : bizStores) {
                    try {
                        String htmlData = showHTMLService.showStoreByWebLocation(bizStore);
                        String filePath = baseDirectory + bizStore.getWebLocation() + ".html";
                        Path pathToFile = Paths.get(filePath);
                        try {
                            Files.deleteIfExists(pathToFile);
                            Files.createDirectories(pathToFile.getParent());
                            Files.createFile(pathToFile);

                            FileUtils.writeStringToFile(pathToFile.toFile(), htmlData, Charset.forName("UTF-8"));

                            LOG.info("Write to File={} filePath={}", pathToFile.toFile().getAbsolutePath(), filePath);
                            FileUtils.writeStringToFile(pathToTxtFile.toFile(), filePath, Charset.forName("UTF-8"), true);

                            generated++;
                        } catch (IOException e) {
                            failure++;
                            LOG.error("Failed file operation failed for storeId={} reason={}", bizStore.getId(), e.getLocalizedMessage());
                        }
                    } catch (Exception e) {
                        LOG.error("Failed HTML generation for storeId={} reason={}", bizStore.getId(), e.getLocalizedMessage(), e);
                    }
                }

                found += bizStores.size();

                i += 1000;
                if (bizStores.size() == 0) {
                    i = 0;
                }
            } while (i > 0);
        } catch (Exception e) {
            LOG.error("Failed HTML generation, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            if (0 != found || 0 != failure || 0 != generated) {
                cronStats.addStats("found", found);
                cronStats.addStats("failure", failure);
                cronStats.addStats("skipped", skipped);
                cronStats.addStats("generateHTMLPages", generated);
                cronStatsService.save(cronStats);

                /* Without if condition its too noisy. */
                LOG.info("complete found={} failure={} generateHTMLPages={}", found, failure, generated);
            }
        }
    }
}
