package com.noqapp.loader.listener;

import com.noqapp.medical.service.MasterLabService;
import com.noqapp.service.FtpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

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

    private FtpService ftpService;
    private MasterLabService masterLabService;

    @Autowired
    public LoaderInitializationBean(
        @Value("${ftp.location}")
        String ftpLocation,

        FtpService ftpService,
        MasterLabService masterLabService
    ) {
        this.ftpLocation = ftpLocation;

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
}
