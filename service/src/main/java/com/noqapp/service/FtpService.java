package com.noqapp.service;

import com.noqapp.common.utils.FileUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;

/**
 * hitender
 * 5/26/18 2:35 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class FtpService {
    private static final Logger LOG = LoggerFactory.getLogger(FtpService.class);

    /** AWS needs without file separator. */
    static String PROFILE_AWS = "profile";
    static String SERVICE_AWS = "service";
    public static String MEDICAL_AWS = "medical";
    static String ARTICLE_AWS = "article";
    static String VIGYAPAN_AWS = "vigyapan";

    /** FTP needs with file separator. */
    public static String PROFILE = FileUtil.getFileSeparator() + PROFILE_AWS;
    public static String SERVICE = FileUtil.getFileSeparator() + SERVICE_AWS;
    public static String ARTICLE = FileUtil.getFileSeparator() + ARTICLE_AWS;
    public static String VIGYAPAN = FileUtil.getFileSeparator() + VIGYAPAN_AWS;

    /** TODO(hth) Medical stores all medical record related images. */
    public static String MEDICAL = FileUtil.getFileSeparator() + MEDICAL_AWS;
    public static String[] directories = new String[]{FtpService.PROFILE, FtpService.SERVICE, FtpService.MEDICAL, FtpService.ARTICLE, FtpService.VIGYAPAN};

    public static String PREFERRED_STORE = FileUtil.getFileSeparator() + "preferredStore";
    public static String MASTER_MEDICAL = FileUtil.getFileSeparator() + "masterMedical";

    @Value("${fileserver.ftp.host}")
    private String host;

    @Value("${ftp.location}")
    private String ftpLocation;

    @Value("${fileserver.ftp.username}")
    private String ftpUser;

    @Value("${fileserver.ftp.password}")
    private String ftpPassword;

    private FileSystemOptions fileSystemOptions;

    @Autowired
    public FtpService(FileSystemOptions fileSystemOptions) {
        this.fileSystemOptions = fileSystemOptions;
    }

    public InputStream getFileAsInputStream(String filename, String directory, String parentDirectory) {
        try (DefaultFileSystemManager manager = new StandardFileSystemManager()) {
            manager.init();
            FileContent fileContent = getFileContent(filename, directory, parentDirectory, manager);
            if (fileContent != null) {
                return fileContent.getInputStream();
            }

            return null;
        } catch (FileSystemException e) {
            LOG.error("Failed to get file={} reason={}", filename, e.getLocalizedMessage(), e);
            return null;
        }
    }

    /** Manager is already initialized. */
    public FileContent getFileContent(String filename, String codeQR, String parentDirectory, DefaultFileSystemManager manager) {
        try {
            String filePath;
            if (StringUtils.isBlank(codeQR)) {
                filePath = ftpLocation + parentDirectory + FileUtil.getFileSeparator() + filename;
            } else {
                filePath = ftpLocation + parentDirectory + FileUtil.getFileSeparator() + codeQR + FileUtil.getFileSeparator() + filename;
            }

            FileObject remoteFile = manager.resolveFile(createConnectionString(filePath), fileSystemOptions);
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

    public FileObject[] getAllFilesInDirectory(String directory, DefaultFileSystemManager manager) {
        Assert.isTrue(directory.startsWith(FileUtil.getFileSeparator()), "should start with file path");

        try {
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation + directory), fileSystemOptions);
            LOG.info("Found directory={} status={}", directory, remoteFile.exists());
            return remoteFile.getChildren();
        } catch (FileSystemException e) {
            LOG.error("Failed to get directory={} reason={}", directory, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public boolean deleteAllFilesInDirectory(String directory) {
        Assert.isTrue(directory.startsWith(FileUtil.getFileSeparator()), "should start with file path");

        try (DefaultFileSystemManager manager = new StandardFileSystemManager()) {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation + directory), fileSystemOptions);
            LOG.info("Found directory={} status={}", directory, remoteFile.exists());
            FileObject[] fileObjects = remoteFile.getChildren();
            for (FileObject fileObject : fileObjects) {
                fileObject.delete();
            }
            return true;
        } catch (FileSystemException e) {
            LOG.error("Failed to get directory={} reason={}", directory, e.getLocalizedMessage(), e);
            return false;
        }
    }

    public void upload(String filename, String directory, String parent) {
        File file = new File(FileUtils.getTempDirectoryPath() + File.separator + filename);
        if (!file.exists()) {
            throw new RuntimeException("Error. Local file not found");
        }

        try (DefaultFileSystemManager manager = new StandardFileSystemManager()) {
            manager.init();

            /* Create local file object. */
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            /* Create remote file object. */
            FileObject remoteFile;
            if (StringUtils.isBlank(directory)) {
                remoteFile = manager.resolveFile(
                    createConnectionString(
                        ftpLocation
                            + parent
                            + FileUtil.getFileSeparator()
                            + filename),
                    fileSystemOptions);
            } else {
                String directoryWithPathSeparator = directory.startsWith(FileUtil.getFileSeparator()) ? directory : FileUtil.getFileSeparator() + directory;
                remoteFile = manager.resolveFile(
                    createConnectionString(
                        ftpLocation
                            + parent
                            + directoryWithPathSeparator
                            + FileUtil.getFileSeparator()
                            + filename),
                    fileSystemOptions);
            }

            /* Copy local file to sftp server. */
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

            LOG.info("File ftp to remote successfully");
        } catch (FileSystemException e) {
            LOG.error("ftp upload remote {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String filename, String codeQR, String parentDirectory) {
        try (DefaultFileSystemManager manager = new StandardFileSystemManager()) {
            manager.init();

            /* Create remote object. */
            FileObject remoteFile;
            if (StringUtils.isBlank(codeQR)) {
                remoteFile = manager.resolveFile(
                    createConnectionString(ftpLocation + parentDirectory + FileUtil.getFileSeparator() + filename),
                    fileSystemOptions);
            } else {
                remoteFile = manager.resolveFile(
                    createConnectionString(ftpLocation + parentDirectory + FileUtil.getFileSeparator() + codeQR + FileUtil.getFileSeparator() + filename),
                    fileSystemOptions);
            }

            boolean deletedFile = false;
            if (remoteFile.exists()) {
                deletedFile = remoteFile.delete();
                LOG.info("Deleted file={}", filename);
            }

            if (StringUtils.isNotBlank(codeQR)) {
                if (remoteFile.getParent().isFolder() && 0 == remoteFile.getParent().getChildren().length) {
                    remoteFile.getParent().delete();
                    LOG.info("Deleted folder={} or codeQR={}", remoteFile.getParent().getPublicURIString(), codeQR);
                }
            }

            return deletedFile;
        } catch (FileSystemException e) {
            LOG.error("ftp delete remote {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean exist() {

        try (DefaultFileSystemManager manager = new StandardFileSystemManager()) {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation), fileSystemOptions);
            return remoteFile.isFolder() && remoteFile.isWriteable() && remoteFile.isReadable();
        } catch (FileSystemException e) {
            /* Check access set correctly for user and remote location exists. Base directory above needs access by user. */
            LOG.error("Could not find remote file={} reason={}", ftpLocation, e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean existFolder(String folderName) {

        try (DefaultFileSystemManager manager = new StandardFileSystemManager()) {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(ftpLocation + folderName), fileSystemOptions);
            return remoteFile.isFolder() && remoteFile.isWriteable() && remoteFile.isReadable();
        } catch (FileSystemException e) {
            /* Check access set correctly for user and remote location exists. Base directory above needs access by user. */
            LOG.error("Could not find remote file={} reason={}", ftpLocation, e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean createFolder(String folderName) {

        try (DefaultFileSystemManager manager = new StandardFileSystemManager()) {
            manager.init();
            String folderLocation;
            if (folderName.startsWith("/")) {
                folderLocation = ftpLocation + folderName;
            } else {
                folderLocation = ftpLocation + "/" + folderName;
            }
            FileObject remoteFile = manager.resolveFile(createConnectionString(folderLocation), fileSystemOptions);
            if (!remoteFile.exists()) {
                remoteFile.createFolder();
            }
            return remoteFile.isFolder() && remoteFile.isWriteable() && remoteFile.isReadable();
        } catch (FileSystemException e) {
            /* Check access set correctly for user and remote location exists. Base directory above needs access by user. */
            LOG.error("Could not find remote file={} reason={}", ftpLocation, e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String createConnectionString(String filePath) {
        LOG.info("FTP Filepath {}", filePath);
        return "sftp://" + ftpUser + ":" + ftpPassword + "@" + host + "/" + filePath;
    }
}
