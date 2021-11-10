package com.noqapp.loader.scheduledtasks;

import static com.noqapp.common.utils.DateUtil.DTF_YYYY_MM_DD;

import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.loader.domain.SiteMap;
import com.noqapp.loader.domain.SiteMapIndex;
import com.noqapp.loader.domain.SiteUrl;
import com.noqapp.loader.domain.SiteUrlMap;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.service.ShowHTMLService;
import com.noqapp.service.ShowProfessionalProfileHTMLService;
import com.noqapp.service.StatsCronService;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * User: hitender
 * Date: 6/14/17 6:29 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class GenerateStoreQueueHTML {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateStoreQueueHTML.class);

    private String parentHost;

    private BizStoreManager bizStoreManager;
    private ShowHTMLService showHTMLService;
    private ShowProfessionalProfileHTMLService showProfessionalProfileHTMLService;
    private StatsCronService statsCronService;
    private String staticHTMLSwitch;
    private String storeBaseDirectory;

    private StatsCronEntity statsCron;

    @Autowired
    public GenerateStoreQueueHTML(
        @Value("${parentHost}")
        String parentHost,

        @Value("${GenerateStoreQueueHTML.staticHTMLSwitch:ON}")
        String staticHTMLSwitch,

        @Value("${GenerateStoreQueueHTML.store.base.directory:/tmp/business/store}")
        String storeBaseDirectory,

        BizStoreManager bizStoreManager,
        ShowHTMLService showHTMLService,
        ShowProfessionalProfileHTMLService showProfessionalProfileHTMLService,
        StatsCronService statsCronService
    ) {
        this.parentHost = parentHost;
        this.staticHTMLSwitch = staticHTMLSwitch;
        this.storeBaseDirectory = storeBaseDirectory;

        this.bizStoreManager = bizStoreManager;
        this.showHTMLService = showHTMLService;
        this.showProfessionalProfileHTMLService = showProfessionalProfileHTMLService;
        this.statsCronService = statsCronService;
    }

    @Scheduled(cron = "${loader.GenerateStoreQueueHTML.generateHTMLPages}")
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
            /* Stores all the URLs for business. This is for just validating the content */
            Path pathToTxtFile = Paths.get(storeBaseDirectory + Constants.FILE_SEPARATOR + "_all.txt");
            Files.deleteIfExists(pathToTxtFile);
            Files.createDirectories(pathToTxtFile.getParent());
            Files.createFile(pathToTxtFile);
            String modifiedDate = DateUtil.getZonedDateTimeAtUTC().format(DTF_YYYY_MM_DD);
            SiteMapIndex siteMapIndex = new SiteMapIndex();

            /* Max URL supported is 50_000 per site map. */
            int MAX_LIMIT_PER_INSTANCE = 50_000;
            int i = 0;
            do {
                List<BizStoreEntity> bizStores = bizStoreManager.getAllActive(i, MAX_LIMIT_PER_INSTANCE);
                SiteUrlMap siteUrlMap = new SiteUrlMap();
                for (BizStoreEntity bizStore : bizStores) {
                    try {
                        String htmlData = BusinessTypeEnum.DO == bizStore.getBusinessType()
                            ? showProfessionalProfileHTMLService.showStoreByWebLocation(bizStore)
                            : showHTMLService.showStoreByWebLocation(bizStore);

                        String filePath = storeBaseDirectory + bizStore.getWebLocation() + ".html";
                        Path pathToFile = Paths.get(filePath);
                        try {
                            Files.deleteIfExists(pathToFile);
                            Files.createDirectories(pathToFile.getParent());
                            Files.createFile(pathToFile);

                            FileUtils.writeStringToFile(
                                pathToFile.toFile(),
                                htmlData,
                                Constants.CHAR_SET_UTF8);

                            String location = filePath.replace("/tmp", parentHost);
                            FileUtils.writeStringToFile(
                                pathToTxtFile.toFile(),
                                location + System.lineSeparator(),
                                Constants.CHAR_SET_UTF8,
                                true);

                            populateSiteMapURL(siteUrlMap, location, modifiedDate);
                            generated++;
                        } catch (IOException e) {
                            failure++;
                            LOG.error("Failed file operation failed for storeId={} reason={}", bizStore.getId(), e.getLocalizedMessage());
                        }
                    } catch (Exception e) {
                        LOG.error("Failed HTML generation for storeId={} reason={}", bizStore.getId(), e.getLocalizedMessage(), e);
                    }
                }

                if (!siteUrlMap.getSiteUrls().isEmpty()) {
                    Path xmlFilePath = Paths.get(storeBaseDirectory + Constants.FILE_SEPARATOR + i + ".xml");
                    createSiteMapFile(xmlFilePath, siteUrlMap);
                    populateSiteMapIndex(modifiedDate, siteMapIndex, xmlFilePath);
                }

                found += bizStores.size();

                i += MAX_LIMIT_PER_INSTANCE;
                if (bizStores.size() == 0) {
                    i = 0;
                }
            } while (i > 0);

            /* Create site index file upon exit. */
            createSiteMapIndexFile(
                /* Since store file is in two levels down at /business/store hence getParent() & getParent(). */
                Paths.get(Paths.get(storeBaseDirectory).getParent().getParent() + Constants.FILE_SEPARATOR + "queue-store-sitemap.xml"),
                siteMapIndex);

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

    /**
     * This links to all the HTML for business that were created.
     */
    private void populateSiteMapURL(SiteUrlMap siteUrlMap, String location, String modifiedDate) {
        siteUrlMap.addSiteUrl(
            new SiteUrl()
                .setLocation(location)
                .setLastModified(modifiedDate)
                /* other changeFrequency options are hourly or always means (changes each time the page is accessed). */
                .setChangeFrequency("daily")
                .setPriority("0.8")
        );
    }

    /**
     * This links to all the site map that have been created.
     */
    private void populateSiteMapIndex(String modifiedDate, SiteMapIndex siteMapIndex, Path xmlFilePath) {
        siteMapIndex.addSiteMaps(new SiteMap()
            /* Since store file is in two levels down at /b/s hence getParent() & getParent(). */
            .setLocation(xmlFilePath.toString().replace(Paths.get(storeBaseDirectory).getParent().getParent().toString(), parentHost))
            .setLastModified(modifiedDate));
    }

    /**
     * Generate Site Map File for all the HTML files that were created.
     */
    private void createSiteMapFile(Path xmlFilePath, SiteUrlMap siteUrlMap) throws IOException {
        Files.deleteIfExists(xmlFilePath);
        FileUtils.writeStringToFile(xmlFilePath.toFile(), siteUrlMap.asXML(), Constants.CHAR_SET_UTF8, false);
    }

    /**
     * Generate Site Map Index File for all the Site Map that were created for Queue Businesses.
     */
    private void createSiteMapIndexFile(Path xmlFilePath, SiteMapIndex siteMapIndex) throws IOException {
        Files.deleteIfExists(xmlFilePath);
        FileUtils.writeStringToFile(xmlFilePath.toFile(), siteMapIndex.asXML(), Constants.CHAR_SET_UTF8, false);
    }
}
