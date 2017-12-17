package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.Constants;
import com.noqapp.loader.domain.SiteMap;
import com.noqapp.loader.domain.SiteMapIndex;
import com.noqapp.loader.domain.SiteUrl;
import com.noqapp.loader.domain.SiteUrlMap;
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
import java.util.Date;
import java.util.List;

import static com.noqapp.common.utils.DateUtil.DF_YYYY_MM_DD;

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
    private StatsCronService statsCronService;
    private String staticHTMLSwitch;
    private String baseDirectory;

    private StatsCronEntity statsCron;

    @Autowired
    public GenerateStoreQueueHTML(
            @Value("${parentHost}")
            String parentHost,

            @Value("${GenerateStoreQueueHTML.staticHTMLSwitch:ON}")
            String staticHTMLSwitch,

            @Value("${GenerateStoreQueueHTML.base.directory:/tmp/biz}")
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
            Path pathToTxtFile = Paths.get(baseDirectory + Constants.FILE_SEPARATOR + "_all.txt");
            Files.deleteIfExists(pathToTxtFile);
            Files.createDirectories(pathToTxtFile.getParent());
            Files.createFile(pathToTxtFile);
            String modifiedDate = DF_YYYY_MM_DD.format(new Date());
            SiteMapIndex siteMapIndex = new SiteMapIndex();

            int MAX_LIMIT_PER_INSTANCE = 50_000;
            int i = 1;
            do {
                /* Max URL supported is 50_000 per site map. */
                List<BizStoreEntity> bizStores = bizStoreManager.getAll(i, MAX_LIMIT_PER_INSTANCE);
                SiteUrlMap siteUrlMap = new SiteUrlMap();
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
                    Path xmlFilePath = Paths.get(baseDirectory + Constants.FILE_SEPARATOR + i + ".xml");
                    createSiteMapFile(Constants.CHAR_SET_UTF8, xmlFilePath, siteUrlMap);
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
                    Constants.CHAR_SET_UTF8,
                    Paths.get(Paths.get(baseDirectory).getParent() + Constants.FILE_SEPARATOR + "q-biz-sitemap.xml"),
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
     *
     * @param siteUrlMap
     * @param location
     * @param modifiedDate
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
     *
     * @param modifiedDate
     * @param siteMapIndex
     * @param xmlFilePath
     */
    private void populateSiteMapIndex(String modifiedDate, SiteMapIndex siteMapIndex, Path xmlFilePath) {
        siteMapIndex.addSiteMaps(new SiteMap()
                .setLocation(xmlFilePath.toString().replace(Paths.get(baseDirectory).getParent().toString(), parentHost))
                .setLastModified(modifiedDate));
    }

    /**
     * Generate Site Map File for all the HTML files that were created.
     *
     * @param charset
     * @param xmlFilePath
     * @param siteUrlMap
     * @throws IOException
     */
    private void createSiteMapFile(Charset charset, Path xmlFilePath, SiteUrlMap siteUrlMap) throws IOException {
        Files.deleteIfExists(xmlFilePath);
        FileUtils.writeStringToFile(xmlFilePath.toFile(), siteUrlMap.asXML(), charset, false);
    }

    /**
     * Generate Site Map Index File for all the Site Map that were created for Queue Businesses.
     *
     * @param charset
     * @param xmlFilePath
     * @param siteMapIndex
     * @throws IOException
     */
    private void createSiteMapIndexFile(Charset charset, Path xmlFilePath, SiteMapIndex siteMapIndex) throws IOException {
        Files.deleteIfExists(xmlFilePath);
        FileUtils.writeStringToFile(xmlFilePath.toFile(), siteMapIndex.asXML(), charset, false);
    }
}
