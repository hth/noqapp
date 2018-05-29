package com.noqapp.loader.scheduledtasks;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.service.FtpService;
import com.noqapp.service.StatsCronService;
import org.apache.commons.vfs2.FileObject;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMimeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.noqapp.service.FtpService.PROFILE;

/**
 * hitender
 * 5/27/18 4:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class FileUploadToS3 {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadToS3.class);

    private static final NumberFormat TWO_DIGIT_FORMAT = new DecimalFormat("00");

    private final String bucketName;
    private final String profileUploadSwitch;

    private StatsCronService statsCronService;
    private FtpService ftpService;
    private AmazonS3 amazonS3;

    private StatsCronEntity statsCron;

    public FileUploadToS3(
            @Value("${aws.s3.bucketName}")
            String bucketName,

            @Value ("${FilesUploadToS3.profile.switch:ON}")
            String profileUploadSwitch,

            StatsCronService statsCronService,
            FtpService ftpService,
            AmazonS3 amazonS3
    ) {
        this.bucketName = bucketName;
        this.profileUploadSwitch = profileUploadSwitch;
        this.statsCronService = statsCronService;
        this.ftpService = ftpService;
        this.amazonS3 = amazonS3;
    }

    /**
     * Upload Receipt to S3.
     * Note: Cron string blow run every 5 minutes.
     *
     * @see <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled">http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled</a>
     */
    @Scheduled(fixedDelayString = "${loader.FilesUploadToS3.profileUpload}")
    public void profileUpload() {
        statsCron = new StatsCronEntity(
                FileUploadToS3.class.getName(),
                "profileUpload",
                profileUploadSwitch);

        /**
         * TODO prevent test db connection from dev. As this moves files to 'dev' bucket in S3 and test environment fails to upload to 'test' bucket.
         * NOTE: This is one of the reason you should not connect to test database from dev environment. Or have a
         * fail safe to prevent uploading to dev bucket when connected to test database.
         */
        if ("OFF".equalsIgnoreCase(profileUploadSwitch)) {
            LOG.debug("feature is {}", profileUploadSwitch);
            return;
        }

        FileObject[] fileObjects = ftpService.getAllFilesInDirectory(PROFILE);
        if (fileObjects.length == 0) {
            /* No image to upload. */
            return;
        } else {
            LOG.info("Files to upload to cloud, count={}", fileObjects.length);
        }

        int success = 0, failure = 0;
        try {
            for (FileObject document : fileObjects) {
                Metadata metadata = FileUtil.populateFileMetadata(ftpService.getFile(document.getName().getBaseName(), PROFILE));
                try {
                    success = uploadToS3(
                            success,
                            PROFILE,
                            document.getName().getBaseName(),
                            ftpService.getFile(document.getName().getBaseName(), PROFILE),
                            Long.valueOf(metadata.get(FileUtil.FILE_LENGTH)),
                            metadata.get(TikaMimeKeys.TIKA_MIME_FILE));

                    ftpService.delete(document.getName().getBaseName(), PROFILE);
                } catch (AmazonServiceException e) {
                    LOG.error("Amazon S3 rejected request with an error response for some reason " +
                                    "document:{} " +
                                    "Error Message:{} " +
                                    "HTTP Status Code:{} " +
                                    "AWS Error Code:{} " +
                                    "Error Type:{} " +
                                    "Request ID:{}",
                            document.getName().getBaseName(),
                            e.getLocalizedMessage(),
                            e.getStatusCode(),
                            e.getErrorCode(),
                            e.getErrorType(),
                            e.getRequestId(),
                            e);

                    failure++;
                } catch (AmazonClientException e) {
                    LOG.error("Client encountered an internal error while trying to communicate with S3 " +
                                    "document:{} " +
                                    "reason={}",
                            document.getName().getBaseName(),
                            e.getLocalizedMessage(),
                            e);

                    failure++;
                } catch (Exception e) {
                    LOG.error("S3 image upload failure document={} reason={}",
                            document.getName().getBaseName(),
                            e.getLocalizedMessage(),
                            e);
                    failure++;
                }
            }
        } catch (Exception e) {
            LOG.error("Error S3 uploading profile reason={}", e.getLocalizedMessage(), e);
        } finally {
            if (0 != success || 0 != failure) {
                statsCron.addStats("found", success + failure);
                statsCron.addStats("success", success);
                statsCron.addStats("failure", failure);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete found={} success={} failure={}", success + failure, success, failure);
            }
        }
    }

    private int uploadToS3(int success, String folderName, String filenameWithLocation, InputStream inputStream, long fileLength, String contentType) {
        try {
            PutObjectRequest putObject = getPutObjectRequest(folderName, filenameWithLocation, inputStream, fileLength, contentType);
            amazonS3.putObject(putObject);
            success++;
            return success;
        } catch (Exception e) {
            LOG.error("Failed to upload to S3 reason={}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.error("Failed to close stream reason={}", e.getLocalizedMessage(), e);
                }
            }
        }
    }

    /**
     * Populates PutObjectRequest.
     */
    private PutObjectRequest getPutObjectRequest(String folderName, String key, InputStream inputStream, long fileLength, String contentType) {
        return new PutObjectRequest(bucketName, folderName + "/" + key, inputStream, getObjectMetadata(fileLength, contentType));
    }

    /**
     * Adds metadata like content type and file length.
     */
    private ObjectMetadata getObjectMetadata(long fileLength, String contentType) {
        ObjectMetadata metaData = new ObjectMetadata();

        metaData.setContentLength(fileLength);
        metaData.setContentType(contentType);

        return metaData;
    }

    private String computeFileYearMonthDayLocation() {
        ZonedDateTime zonedDateTime = getUTCZonedDateTime();
        return String.valueOf(zonedDateTime.getYear()) +
                "-" +
                TWO_DIGIT_FORMAT.format(zonedDateTime.getMonthValue()) +
                "/" +
                TWO_DIGIT_FORMAT.format(zonedDateTime.getDayOfMonth()) +
                "/";
    }

    /**
     * Why convert to UTC when the date is already saved as UTC time in Database?.
     *
     * This is to protect when some smarty forgets to set the time to UTC on server since JVM time is used with local
     * timezone. In worst case scenario, if Mongo DB date format is changed from UTC to something new. Second scenario
     * is highly unlikely.
     */
    private ZonedDateTime getUTCZonedDateTime() {
        return Instant.now().atZone(ZoneId.of("UTC"));
    }
}
