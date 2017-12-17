package com.noqapp.loader.scheduledtasks;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.service.ShowHTMLService;
import com.noqapp.service.StatsCronService;

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

    private static final Charset CHAR_SET_UTF8 = Charset.forName("UTF-8");
    private String parentHost;

    private BizStoreManager bizStoreManager;
    private ShowHTMLService showHTMLService;
    private StatsCronService statsCronService;
    private String staticHTMLSwitch;
    private String baseDirectory;

    private StatsCronEntity statsCron;

    @Autowired
    public GenerateStoreQueueHTML(
            @Value("${parentHost}")
            String parentHost,

            @Value ("${GenerateStoreQueueHTML.staticHTMLSwitch:ON}")
            String staticHTMLSwitch,

            @Value ("${GenerateStoreQueueHTML.base.directory:/tmp/biz}")
            String baseDirectory,

            BizStoreManager bizStoreManager,
            ShowHTMLService showHTMLService,
            StatsCronService statsCronService
    ) {
        this.parentHost = parentHost;
        this.staticHTMLSwitch = staticHTMLSwitch;
        this.baseDirectory = baseDirectory;

        this.bizStoreManager = bizStoreManager;
        this.showHTMLService = showHTMLService;
        this.statsCronService = statsCronService;
    }

    @Scheduled (cron = "${loader.GenerateStoreQueueHTML.generateHTMLPages}")
    public void generateHTMLPages() {
        statsCron = new StatsCronEntity(
                GenerateStoreQueueHTML.class.getName(),
                "Generate_Store_Queue_HTML",
                staticHTMLSwitch);

        int found = 0, failure = 0, generated = 0, skipped = 0;
        if ("OFF".equalsIgnoreCase(staticHTMLSwitch)) {
            LOG.debug("feature is {}", staticHTMLSwitch);
        }

        try {
            Path pathToTxtFile = Paths.get(baseDirectory + System.getProperty("file.separator") + "all.txt");
            Files.deleteIfExists(pathToTxtFile);
            Files.createDirectories(pathToTxtFile.getParent());
            Files.createFile(pathToTxtFile);

            int i = 1;
            do {
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

                            FileUtils.writeStringToFile(
                                    pathToFile.toFile(),
                                    htmlData,
                                    Charset.forName("UTF-8"));

                            FileUtils.writeStringToFile(
                                    pathToTxtFile.toFile(),
                                    filePath.replace("/tmp", parentHost) + System.lineSeparator(),
                                    CHAR_SET_UTF8,
                                    true);

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
                statsCron.addStats("found", found);
                statsCron.addStats("failure", failure);
                statsCron.addStats("skipped", skipped);
                statsCron.addStats("generateHTMLPages", generated);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete found={} failure={} generateHTMLPages={}", found, failure, generated);
            }
        }
    }
}
