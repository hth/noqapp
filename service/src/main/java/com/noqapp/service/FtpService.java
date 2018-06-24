package com.noqapp.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * hitender
 * 5/26/18 2:35 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class FtpService {
    private static final Logger LOG = LoggerFactory.getLogger(FtpService.class);

    public static String PROFILE = "profile";
    public static String SERVICE = "service";

    @Value("${fileserver.ftp.host}")
    private String host;

    @Value ("${ftp.location}")
    private String ftpLocation;

    @Value ("${fileserver.ftp.username}")
    private String ftpUser;

    @Value ("${fileserver.ftp.password}")
    private String ftpPassword;

    private FileSystemOptions fileSystemOptions;

    @Autowired
    public FtpService(FileSystemOptions fileSystemOptions) {
        this.fileSystemOptions = fileSystemOptions;
    }

    public InputStream getFileAsInputStream(String filename, String directory) {
        try {
            FileContent fileContent = getFileContent(filename, directory);
            if (fileContent != null) {
                return fileContent.getInputStream();
            }

            return null;
        } catch (FileSystemException e) {
            LOG.error("Failed to get file={} reason={}", filename, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public FileContent getFileContent(String filename, String directory) {
        DefaultFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation + File.separator + directory + File.separator + filename), fileSystemOptions);
            if (remoteFile.exists() && remoteFile.isFile()) {
                return remoteFile.getContent();
            }
            LOG.error("Could not find file={}", filename);
            return null;
        } catch (FileSystemException e) {
            LOG.error("Failed to get file={} reason={}", filename, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public FileObject[] getAllFilesInDirectory(String directory) {
        DefaultFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation + File.separator + directory), fileSystemOptions);
            LOG.info("Found directory={} status={}", directory, remoteFile.exists());
            return remoteFile.getChildren();
        } catch (FileSystemException e) {
            LOG.error("Failed to get directory={} reason={}", directory, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void upload(String filename, String directory) {
        File file = new File(FileUtils.getTempDirectoryPath() + File.separator + filename);
        if (!file.exists()) {
            throw new RuntimeException("Error. Local file not found");
        }

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            /* Create local file object. */
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            /* Create remote file object. */
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation + File.separator + directory + File.separator + filename), fileSystemOptions);

            /* Copy local file to sftp server. */
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

            LOG.info("File ftp to remote successfully");
        } catch (FileSystemException e) {
            LOG.error("ftp upload remote {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public boolean delete(String filename, String directory) {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            /* Create remote object. */
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation + File.separator + directory + File.separator + filename), fileSystemOptions);

            if (remoteFile.exists()) {
                remoteFile.delete();
                LOG.info("Deleted file={}", filename);
                return true;
            }

            return false;
        } catch (FileSystemException e) {
            LOG.error("ftp delete remote {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public boolean exist() {
        DefaultFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation), fileSystemOptions);
            return remoteFile.isFolder() && remoteFile.isWriteable() && remoteFile.isReadable();
        } catch (FileSystemException e) {
            /* Check access set correctly for user and remote location exists. Base directory above needs access by user. */
            LOG.error("Could not find remote file={} reason={}", ftpLocation, e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public boolean createFolder(String folderName) {
        DefaultFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation + "/" + folderName), fileSystemOptions);
            if (!remoteFile.exists()) {
                remoteFile.createFolder();
            }
            return remoteFile.isFolder() && remoteFile.isWriteable() && remoteFile.isReadable();
        } catch (FileSystemException e) {
            /* Check access set correctly for user and remote location exists. Base directory above needs access by user. */
            LOG.error("Could not find remote file={} reason={}", ftpLocation, e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    private String createConnectionString(String filePath) {
        return "sftp://" + ftpUser + ":" + ftpPassword + "@" + host + "/" + filePath;
    }
}
