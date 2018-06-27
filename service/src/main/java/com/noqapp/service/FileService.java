package com.noqapp.service;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.S3FileEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.repository.S3FileManager;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import static com.noqapp.common.utils.FileUtil.createRandomFilenameOf24Chars;
import static com.noqapp.common.utils.FileUtil.createTempFile;
import static com.noqapp.common.utils.FileUtil.getFileExtension;
import static com.noqapp.common.utils.FileUtil.getFileExtensionWithDot;
import static com.noqapp.common.utils.FileUtil.getFileSeparator;
import static com.noqapp.common.utils.FileUtil.getTmpDir;

/**
 * hitender
 * 5/24/18 11:12 AM
 */
@Service
public class FileService {
    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);
    private static final String PNG_FORMAT = "png";
    private static final String SCALED_IMAGE_POST_FIX = "_s";

    private int imageProfileWidth;
    private int imageProfileHeight;
    private int imageServiceWidth;
    private int imageServiceHeight;


    private AccountService accountService;
    private FtpService ftpService;
    private S3FileManager s3FileManager;
    private BizService bizService;

    @Autowired
    public FileService(
            @Value ("${image.profile.width:192}")
            int imageProfileWidth,

            @Value ("${image.profile.height:192}")
            int imageProfileHeight,

            @Value ("${image.service.width:300}")
            int imageServiceWidth,

            @Value ("${image.service.height:150}")
            int imageServiceHeight,

            AccountService accountService,
            FtpService ftpService,
            S3FileManager s3FileManager,
            BizService bizService
    ) {
        this.imageProfileWidth = imageProfileWidth;
        this.imageProfileHeight = imageProfileHeight;
        this.imageServiceWidth = imageServiceWidth;
        this.imageServiceHeight = imageServiceHeight;

        this.accountService = accountService;
        this.ftpService = ftpService;
        this.s3FileManager = s3FileManager;
        this.bizService = bizService;
    }

    @Async
    public void addProfileImage(String qid, String filename, BufferedImage bufferedImage) {
        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;

        try {
            UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
            String existingProfileImage = userProfile.getProfileImage();

            /* Delete existing file if user changed profile image before the upload process began. */
            ftpService.delete(existingProfileImage, null, FtpService.PROFILE);
            s3FileManager.save(new S3FileEntity(qid, existingProfileImage, FtpService.PROFILE));

            toFile = writeToFile(
                    createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename),
                    bufferedImage);
            decreaseResolution = decreaseResolution(toFile, imageProfileWidth, imageProfileHeight);

            String toFileAbsolutePath = getTmpDir()                         // /java/temp/directory
                    + getFileSeparator()                                    // FileSeparator /
                    + filename;                                             // filename.extension

            tempFile = new File(toFileAbsolutePath);
            writeToFile(tempFile, ImageIO.read(decreaseResolution));
            ftpService.upload(filename, null, FtpService.PROFILE);
            accountService.addUserProfileImage(qid, filename);

            LOG.debug("Uploaded profile file={}", toFileAbsolutePath);
        } catch (IOException e) {
            LOG.error("Failed adding profile image={} reason={}", filename, e.getLocalizedMessage(), e);
        } finally {
            if (null != toFile) {
                toFile.delete();
            }

            if (null != decreaseResolution) {
                decreaseResolution.delete();
            }

            if (null != tempFile) {
                tempFile.delete();
            }
        }
    }

    @Async
    public void addBizImage(String qid, String bizNameId, String filename, BufferedImage bufferedImage) {
        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;

        try {
            BizNameEntity bizName = bizService.getByBizNameId(bizNameId);
            Set<String> businessServiceImages = bizName.getBusinessServiceImages();

            while (businessServiceImages.size() >= 10) {
                String lastImage = businessServiceImages.stream().findFirst().get();

                /* Delete existing file business service image before the upload process began. */
                ftpService.delete(lastImage, bizName.getCodeQR(), FtpService.SERVICE);
                S3FileEntity s3File = new S3FileEntity(qid, lastImage, FtpService.SERVICE)
                        .setCodeQR(bizName.getCodeQR());
                s3FileManager.save(s3File);
                businessServiceImages.remove(lastImage);
            }

            toFile = writeToFile(
                    createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename),
                    bufferedImage);
            decreaseResolution = decreaseResolution(toFile, imageServiceWidth, imageServiceHeight);

            String toFileAbsolutePath = getTmpDir()                         // /java/temp/directory
                    + getFileSeparator()                                    // FileSeparator /
                    + filename;                                             // filename.extension

            tempFile = new File(toFileAbsolutePath);
            writeToFile(tempFile, ImageIO.read(decreaseResolution));
            ftpService.upload(filename, bizName.getCodeQR(), FtpService.SERVICE);

            businessServiceImages.add(filename);
            bizService.saveName(bizName);

            LOG.debug("Uploaded bizName service file={}", toFileAbsolutePath);
        } catch (IOException e) {
            LOG.error("Failed adding profile image={} reason={}", filename, e.getLocalizedMessage(), e);
        } finally {
            if (null != toFile) {
                toFile.delete();
            }

            if (null != decreaseResolution) {
                decreaseResolution.delete();
            }

            if (null != tempFile) {
                tempFile.delete();
            }
        }
    }

    /**
     * Decrease the resolution of image with PNG file format for better resolution.
     *
     * @param file
     * @return
     * @throws IOException
     */
    private File decreaseResolution(File file, int width, int height) throws IOException {
        BufferedImage image = bufferedImage(file);

        LOG.debug("W={} H={}", image.getWidth(), image.getHeight());
        double aspectRatio = (double) image.getWidth(null) / (double) image.getHeight(null);

        BufferedImage bufferedImage = resizeImage(image, width, (int) (height / aspectRatio));
        File scaledFile = createTempFile(
                FilenameUtils.getBaseName(file.getName()) + SCALED_IMAGE_POST_FIX,
                FilenameUtils.getExtension(file.getName()));

        ImageIO.write(bufferedImage, PNG_FORMAT, scaledFile);
        return scaledFile;
    }

    /**
     * Decrease the resolution of the image with PNG file format for better resolution.
     *
     * @return
     * @throws IOException
     */
    public void decreaseResolution(InputStream inputStream, OutputStream outputStream, int width, int height) throws IOException {
        BufferedImage image = bufferedImage(inputStream);

        LOG.debug("W={} H={}", image.getWidth(), image.getHeight());
        double aspectRatio = (double) image.getWidth(null) / (double) image.getHeight(null);

        BufferedImage bufferedImage = resizeImage(image, width, (int) (height / aspectRatio));
        ImageIO.write(bufferedImage, PNG_FORMAT, outputStream);
    }

    /**
     * Can be used for calculating height and width of an image.
     *
     * @param file
     * @return
     * @throws IOException
     */
    private BufferedImage bufferedImage(File file) throws IOException {
        return bufferedImage(new FileInputStream(file));
    }

    public BufferedImage bufferedImage(InputStream is) throws IOException {
        return ImageIO.read(is);
    }

    private File writeToFile(String filename, BufferedImage bufferedImage) throws IOException {
        File toFile = createTempFile(FilenameUtils.getBaseName(filename), getFileExtension(filename));
        writeToFile(toFile, bufferedImage);
        return toFile;
    }

    private void writeToFile(File file, BufferedImage bufferedImage) throws IOException {
        ImageIO.write(bufferedImage, PNG_FORMAT, file);
    }

    /**
     * This function resize the image file and returns the BufferedImage object that can be saved to file system.
     *
     * @param image
     * @param width
     * @param height
     * @return
     */
    private static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}
