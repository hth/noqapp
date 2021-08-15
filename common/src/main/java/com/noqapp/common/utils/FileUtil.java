package com.noqapp.common.utils;

import com.noqapp.common.type.FileExtensionTypeEnum;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.vfs2.FileObject;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;

/**
 * User: hitender
 * Date: 11/21/16 9:47 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class FileUtil {
    public static final String TEMP_FILE_START_WITH = "NoQueue";
    public static final String DOT = ".";
    public static final String DASH = "-";
    public static final String FILE_LENGTH = "length";

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    private static final Detector DETECTOR = new DefaultDetector(MimeTypes.getDefaultMimeTypes());
    private static final int FILE_SIZE_IN_MB = 1024 * 1024;

    private static final char[][] pairs = {{'a', 'z'}, {'0', '9'}};
    private static final RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder().withinRange(pairs).build();

    private FileUtil() {
    }

    public static String getTmpDir() {
        return FileUtils.getTempDirectoryPath();
    }

    public static String getFileSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    public static File getFileFromTmpDir(String filename) {
        return new File(getTmpDir() + getFileSeparator() + filename);
    }

    public static File createTempFile(String name, String ext) throws IOException {
        try {
            var suffix = ext.startsWith(DOT) ? ext : DOT + ext;
            if (name.startsWith(TEMP_FILE_START_WITH)) {
                return File.createTempFile(name + DASH, suffix);
            } else {
                return File.createTempFile(TEMP_FILE_START_WITH + DASH + name + DASH, suffix);
            }
        } catch (IOException e) {
            LOG.error("Error creating temp file, reason={}", e.getLocalizedMessage(), e);
            throw e;
        }
    }

    public static String createRandomFilenameOf16Chars() {
        return createRandomFilename(16);
    }

    public static String createRandomFilenameOf24Chars() {
        return createRandomFilename(24);
    }

    public static String createRandomFilename(int size) {
        return randomStringGenerator.generate(size);
    }

    /**
     * Avoid unless required on file system to save file with file extension. User can be presented with correct file
     * extension when set with correct content type in the response header.
     *
     * @param fileExtension
     * @return
     */
    private String createRandomFilename(FileExtensionTypeEnum fileExtension) {
        return addFileExtension(createRandomFilenameOf16Chars(), fileExtension);
    }

    private String addFileExtension(String filename, FileExtensionTypeEnum fileExtension) {
        String filenameWithExtension = filename;
        if (fileExtension != null) {
            switch (fileExtension) {
                case XLS:
                case TXT:
                case JPEG:
                case JPG:
                case PNG:
                case PDF:
                case XML:
                    filenameWithExtension = filename + DOT + StringUtils.lowerCase(fileExtension.name());
                    break;
                default:
                    LOG.error("reached unsupported file extension={}", fileExtension);
                    throw new RuntimeException("reached unsupported file extension " + fileExtension.name());
            }
        }
        return filenameWithExtension;
    }

    /**
     * From filename with extension, returns extension.
     *
     * @param filename
     * @return
     */
    public static String getFileExtension(String filename) {
        Assert.isTrue(filename.contains("."), "Filename '" + filename + "' should contain .");

        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (extension.endsWith("jpeg")) {
            extension = "jpg";
        }
        return extension;
    }

    public static String getFileExtensionWithDot(String filename) {
        return DOT + getFileExtension(filename);
    }

    /**
     * Finds content type of a file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    @SuppressWarnings ("unused")
    public static String detectMimeType(final File file) throws IOException {
        return detectMimeType(FileUtils.openInputStream(file));
    }

    /**
     * Finds content type of a file.
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String detectMimeType(final InputStream inputStream) throws IOException {
        try (TikaInputStream tikaIS = TikaInputStream.get(inputStream)) {

            /*
             * You might not want to provide the file's name. If you provide an Excel
             * document with a .xls extension, it will get it correct right away; but
             * if you provide an Excel document with .doc extension, it will guess it
             * to be a Word document.
             */
            Metadata metadata = new Metadata();
            // metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());

            return DETECTOR.detect(tikaIS, metadata).toString();
        }
    }

    public static String getImageFileExtension(String originalFilename, String contentType) {
        String fileExtension;
        if (StringUtils.isEmpty(originalFilename)) {
            fileExtension = contentType.equalsIgnoreCase("image/jpg") ? ".jpg" : ".png";
        } else {
            fileExtension = FileUtil.getFileExtensionWithDot(originalFilename);
        }
        return fileExtension;
    }

    public static double fileSizeInMB(long length) {
        return length / FILE_SIZE_IN_MB;
    }

    /** Used when loading file from ftp. */
    @SuppressWarnings("unused")
    public static String getFileName(FileObject fileObject) {
        String[] a = fileObject.getName().getURI().split("/");
        return a[a.length - 1];
    }
}
