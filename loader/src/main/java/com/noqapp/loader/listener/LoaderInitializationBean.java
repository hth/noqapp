package com.noqapp.loader.listener;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.service.FtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

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

    private String[] directories = new String[] {
            File.separator + FtpService.PROFILE,
            File.separator + FtpService.SERVICE
    };

    private String ftpLocation;

    private FtpService ftpService;

    @Autowired
    public LoaderInitializationBean(
            @Value("${ftp.location}")
            String ftpLocation,

            FtpService ftpService
    ) {
        this.ftpLocation = ftpLocation;
        this.ftpService = ftpService;
    }

    @PostConstruct
    public void checkIfDirectoryExists() {
        for (String directoryName : directories) {
            File directory = new File(ftpLocation + FileUtil.getFileSeparator() + directoryName);
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
}
