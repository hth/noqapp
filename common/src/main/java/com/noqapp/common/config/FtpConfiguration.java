package com.noqapp.common.config;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hitender
 * 5/26/18 2:02 PM
 */
@Configuration
public class FtpConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(FtpConfiguration.class);

    @Bean
    public FileSystemOptions fileSystemOptions() {
        try {
            /* Create SFTP options. */
            FileSystemOptions opts = new FileSystemOptions();

            /* SSH Key checking. */
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

            /*
             * Using the following line will cause VFS to choose File System's Root
             * as VFS's root. If I wanted to use User's home as VFS's root then set
             * 2nd method parameter to "true".
             */
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

            /* Timeout is count by Milliseconds. */
            SftpFileSystemConfigBuilder.getInstance().setSessionTimeoutMillis(opts, 10000);

            LOG.info("Created default options for VFS");
            return opts;
        } catch (FileSystemException e) {
            LOG.error("Error creating VFS filesystem {}", e.getLocalizedMessage(), e);
            return null;
        }
    }
}
