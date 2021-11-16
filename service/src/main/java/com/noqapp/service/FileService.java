package com.noqapp.service;

import static com.noqapp.common.utils.FileUtil.DOT;
import static com.noqapp.common.utils.FileUtil.createRandomFilenameOf24Chars;
import static com.noqapp.common.utils.FileUtil.createTempFile;
import static com.noqapp.common.utils.FileUtil.getFileExtension;
import static com.noqapp.common.utils.FileUtil.getFileExtensionWithDot;
import static com.noqapp.common.utils.FileUtil.getFileSeparator;
import static com.noqapp.common.utils.FileUtil.getTmpDir;
import static com.noqapp.service.FtpService.PREFERRED_STORE;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.S3FileEntity;
import com.noqapp.domain.StoreCategoryEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.medical.LabCategoryEnum;
import com.noqapp.domain.types.medical.PharmacyCategoryEnum;
import com.noqapp.repository.AdvertisementManager;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.PublishArticleManager;
import com.noqapp.repository.S3FileManager;
import com.noqapp.repository.StoreProductManager;
import com.noqapp.repository.market.HouseholdItemManager;
import com.noqapp.repository.market.PropertyRentalManager;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

/**
 * hitender
 * 5/24/18 11:12 AM
 */
@Service
public class FileService {
    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);
    private static final String APPEND = "_s";          /* Append to scaled image post fix. */
    private static final String ORIGINAL = "_o";        /* Append to original image post fix. */
    private static final String PNG_FORMAT = "png";

    private static String[] STORE_PRODUCT_HEADERS = {
        "Category",
        "Name",
        "Price",
        "Discount",
        "Info",
        "Type",
        "Unit",
        "Measurement",
        "Package Size",
        "Displayed",
        "Key",
        "Reference"
    };

    private static String[] PHARMACY_PRODUCT_HEADERS = {
        "Category",
        "Name",
        "Price",
        "Discount",
        "Info",
        "Unit",
        "Measurement",
        "Package Size",
        "Key",
        "Reference"
    };

    public static String[] RADIOLOGY_PRODUCT_HEADERS = {
        "Category",
        "Name",
        "Price",
        "Discount",
        "Info",
        "Key",
    };

    private int imageProfileWidth;
    private int imageProfileHeight;
    private int imageServiceWidth;
    private int imageServiceHeight;

    private AccountService accountService;
    private FtpService ftpService;
    private S3FileManager s3FileManager;
    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;
    private StoreProductManager storeProductManager;
    private PublishArticleManager publishArticleManager;
    private AdvertisementManager advertisementManager;
    private PropertyRentalManager propertyRentalManager;
    private HouseholdItemManager householdItemManager;
    private BizService bizService;
    private StoreCategoryService storeCategoryService;
    private MailService mailService;

    @Autowired
    public FileService(
        @Value("${image.profile.width:192}")
        int imageProfileWidth,

        @Value("${image.profile.height:192}")
        int imageProfileHeight,

        @Value("${image.service.width:650}")
        int imageServiceWidth,

        @Value("${image.service.height:450}")
        int imageServiceHeight,

        AccountService accountService,
        FtpService ftpService,
        S3FileManager s3FileManager,
        BizNameManager bizNameManager,
        BizStoreManager bizStoreManager,
        StoreProductManager storeProductManager,
        PublishArticleManager publishArticleManager,
        AdvertisementManager advertisementManager,
        PropertyRentalManager propertyRentalManager,
        HouseholdItemManager householdItemManager,
        BizService bizService,
        StoreCategoryService storeCategoryService,
        MailService mailService
    ) {
        this.imageProfileWidth = imageProfileWidth;
        this.imageProfileHeight = imageProfileHeight;
        this.imageServiceWidth = imageServiceWidth;
        this.imageServiceHeight = imageServiceHeight;

        this.accountService = accountService;
        this.ftpService = ftpService;
        this.s3FileManager = s3FileManager;
        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
        this.storeProductManager = storeProductManager;
        this.publishArticleManager = publishArticleManager;
        this.advertisementManager = advertisementManager;
        this.propertyRentalManager = propertyRentalManager;
        this.householdItemManager = householdItemManager;
        this.bizService = bizService;
        this.storeCategoryService = storeCategoryService;
        this.mailService = mailService;
    }

    @Async
    public void addProfileImage(String qid, String filename, BufferedImage bufferedImage) {
        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;
        File tempFileOriginal = null;

        try {
            UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);

            /* Delete only if it existed previously. */
            if (StringUtils.isNotBlank(userProfile.getProfileImage())) {
                String existingProfileImage = userProfile.getProfileImage();

                /* Delete original image when business type is DO. */
                if (BusinessTypeEnum.DO == userProfile.getBusinessType()) {
                    String fileName_o = getFilenameWithOriginal(existingProfileImage);
                    ftpService.delete(fileName_o, null, FtpService.PROFILE);
                    s3FileManager.save(new S3FileEntity(qid, fileName_o, FtpService.PROFILE_AWS));
                }

                /* Delete existing file if user changed profile image before the upload process began. */
                ftpService.delete(existingProfileImage, null, FtpService.PROFILE);
                s3FileManager.save(new S3FileEntity(qid, existingProfileImage, FtpService.PROFILE_AWS));
            }

            toFile = writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
            decreaseResolution = decreaseResolution(toFile, imageProfileWidth, imageProfileHeight);

            // /java/temp/directory/filename.extension
            String toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
            tempFile = new File(toFileAbsolutePath);
            writeToFile(tempFile, ImageIO.read(decreaseResolution));
            ftpService.upload(filename, null, FtpService.PROFILE);
            accountService.addUserProfileImage(qid, filename);

            if (BusinessTypeEnum.DO == userProfile.getBusinessType()) {
                String fileName_o = getFilenameWithOriginal(filename);

                // /java/temp/directory/filename.extension
                String toFileAbsolutePathForOriginal = getTmpDir() + getFileSeparator() + fileName_o;
                tempFileOriginal = new File(toFileAbsolutePathForOriginal);
                writeToFile(tempFileOriginal, ImageIO.read(toFile));
                ftpService.upload(fileName_o, null, FtpService.PROFILE);
            }

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

            if (null != tempFileOriginal) {
                tempFileOriginal.delete();
            }
        }
    }

    @Mobile
    @Async
    public void removeProfileImage(String qid) {
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);

        /* Delete only if it existed previously. */
        if (StringUtils.isNotBlank(userProfile.getProfileImage())) {
            String existingProfileImage = userProfile.getProfileImage();

            /* Delete existing file if user changed profile image before the upload process began. */
            ftpService.delete(existingProfileImage, null, FtpService.PROFILE);
            s3FileManager.save(new S3FileEntity(qid, existingProfileImage, FtpService.PROFILE_AWS));
            accountService.unsetUserProfileImage(qid);
        }
    }

    @Async
    public void addArticleImage(String publishId, String filename, BufferedImage bufferedImage) {
        PublishArticleEntity publishArticle = publishArticleManager.findOne(publishId);
        deleteArticleImage(publishArticle);

        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;
        try {
            toFile = writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
            decreaseResolution = decreaseResolution(toFile, imageServiceWidth, imageServiceHeight);

            // /java/temp/directory/filename.extension
            String toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
            tempFile = new File(toFileAbsolutePath);
            writeToFile(tempFile, ImageIO.read(decreaseResolution));
            ftpService.upload(filename, publishId, FtpService.ARTICLE);

            publishArticle
                .setBannerImage(filename)
                .setValidateStatus(ValidateStatusEnum.P);
            publishArticleManager.save(publishArticle);
        } catch (IOException e) {
            LOG.error("Failed adding bizName image={} reason={}", filename, e.getLocalizedMessage(), e);
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

    public void deleteArticleImage(PublishArticleEntity publishArticle) {
        if (StringUtils.isNotBlank(publishArticle.getBannerImage())) {
            /* Delete existing file business service image before the upload process began. */
            ftpService.delete(publishArticle.getBannerImage(), null, FtpService.ARTICLE);

            /* Delete from S3. */
            s3FileManager.save(new S3FileEntity(publishArticle.getQueueUserId(), publishArticle.getId() + "/" + publishArticle.getBannerImage(), FtpService.ARTICLE_AWS));
        }
    }

    @Async
    public void addAdvertisementImage(String advertisementId, String filename, BufferedImage bufferedImage) {
        AdvertisementEntity advertisement = advertisementManager.findById(advertisementId);

        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;
        try {
            toFile = writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
            decreaseResolution = decreaseResolution(toFile, imageServiceWidth, imageServiceHeight);

            // /java/temp/directory/filename.extension
            String toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
            tempFile = new File(toFileAbsolutePath);
            writeToFile(tempFile, ImageIO.read(decreaseResolution));
            ftpService.upload(filename, advertisementId, FtpService.VIGYAPAN);

            advertisement
                .addImageUrl(filename)
                .setValidateStatus(ValidateStatusEnum.P);
            advertisementManager.save(advertisement);
        } catch (IOException e) {
            LOG.error("Failed adding bizName image={} reason={}", filename, e.getLocalizedMessage(), e);
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

    public void deleteAdvertisementImage(AdvertisementEntity advertisement, String imageUrl) {
        if (StringUtils.isNotBlank(imageUrl)) {
            /* Delete existing file business service image before the upload process began. */
            ftpService.delete(imageUrl, null, FtpService.VIGYAPAN);

            /* Delete from S3. */
            s3FileManager.save(new S3FileEntity(advertisement.getQueueUserId(), advertisement.getId() + "/" + imageUrl, FtpService.VIGYAPAN_AWS));
        }
    }

    @Async
    public void addBizImage(String qid, String bizNameId, String filename, BufferedImage bufferedImage) {
        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;

        try {
            BizNameEntity bizName = bizNameManager.getById(bizNameId);
            Set<String> businessServiceImages = bizName.getBusinessServiceImages();

            while (businessServiceImages.size() >= 10) {
                String lastImage = businessServiceImages.stream().findFirst().get();
                deleteImage(qid, lastImage, bizName.getCodeQR());
                /* Delete local reference. */
                businessServiceImages.remove(lastImage);
            }

            toFile = writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
            decreaseResolution = decreaseResolution(toFile, imageServiceWidth, imageServiceHeight);

            // /java/temp/directory/filename.extension
            String toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
            tempFile = new File(toFileAbsolutePath);
            writeToFile(tempFile, ImageIO.read(decreaseResolution));
            ftpService.upload(filename, bizName.getCodeQR(), FtpService.SERVICE);

            businessServiceImages.add(filename);
            bizNameManager.save(bizName);

            LOG.debug("Uploaded bizName service file={}", toFileAbsolutePath);
        } catch (IOException e) {
            LOG.error("Failed adding bizName image={} reason={}", filename, e.getLocalizedMessage(), e);
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
    public void addProductImage(String qid, String storeProductId, String filename, BufferedImage bufferedImage) {
        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;

        try {
            StoreProductEntity storeProduct = storeProductManager.findOne(storeProductId);
            if (StringUtils.isNotBlank(storeProduct.getProductImage())) {
                deleteProductImage(qid, storeProduct.getProductImage(), storeProduct.getBizStoreId());
                storeProduct.setProductImage(null);
                storeProductManager.save(storeProduct);
            }

            toFile = writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
            decreaseResolution = decreaseResolution(toFile, imageServiceWidth, imageServiceHeight);

            // /java/temp/directory/filename.extension
            String toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
            tempFile = new File(toFileAbsolutePath);
            writeToFile(tempFile, ImageIO.read(decreaseResolution));
            ftpService.upload(filename, storeProduct.getBizStoreId(), FtpService.PRODUCT);

            storeProduct.setProductImage(filename);
            storeProductManager.save(storeProduct);

            LOG.debug("Uploaded product image file={}", toFileAbsolutePath);
        } catch (IOException e) {
            LOG.error("Failed adding product image={} reason={}", filename, e.getLocalizedMessage(), e);
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
    public void addMarketplaceImage(String qid, String postId, String filename, BusinessTypeEnum businessType, BufferedImage bufferedImage) {
        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;

        try {
            Set<String> images;
            String toFileAbsolutePath;
            switch (businessType) {
                case PR:
                    PropertyRentalEntity propertyRental = propertyRentalManager.findOneById(postId);
                    if (!propertyRental.isPostingExpired()) {
                        images = propertyRental.getPostImages();

                        toFile = writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
                        decreaseResolution = decreaseResolution(toFile, imageServiceWidth, imageServiceHeight);

                        // /java/temp/directory/filename.extension
                        toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
                        tempFile = new File(toFileAbsolutePath);
                        writeToFile(tempFile, ImageIO.read(decreaseResolution));
                        ftpService.upload(filename, postId, FtpService.MARKETPLACE_PROPERTY_RENTAL);

                        propertyRentalManager.pushImage(propertyRental.getId(), filename);
                        if (propertyRentalManager.findAllPendingApprovalCount() > 0) {
                            mailService.pendingPostApproval(businessType, propertyRentalManager.findAllPendingApprovalCount());
                        }

                        while (images.size() >= 10) {
                            String lastImage = images.stream().findFirst().get();
                            deleteMarketImage(qid, lastImage, postId, propertyRental.getBusinessType());
                            propertyRentalManager.popImage(propertyRental.getId());
                        }

                        LOG.debug("Uploaded {} file={}", businessType, toFileAbsolutePath);
                    }
                    break;
                case HI:
                    HouseholdItemEntity householdItem = householdItemManager.findOneById(postId);
                    if (!householdItem.isPostingExpired()) {
                        images = householdItem.getPostImages();

                        toFile = writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
                        decreaseResolution = decreaseResolution(toFile, imageServiceWidth, imageServiceHeight);

                        // /java/temp/directory/filename.extension
                        toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
                        tempFile = new File(toFileAbsolutePath);
                        writeToFile(tempFile, ImageIO.read(decreaseResolution));
                        ftpService.upload(filename, postId, FtpService.MARKETPLACE_HOUSEHOLD_ITEM);

                        householdItemManager.pushImage(householdItem.getId(), filename);
                        if (householdItemManager.findAllPendingApprovalCount() > 0) {
                            mailService.pendingPostApproval(businessType, householdItemManager.findAllPendingApprovalCount());
                        }

                        while (images.size() >= 10) {
                            String lastImage = images.stream().findFirst().get();
                            deleteMarketImage(qid, lastImage, postId, householdItem.getBusinessType());
                            householdItemManager.popImage(householdItem.getId());
                        }

                        LOG.debug("Uploaded {} file={}", businessType, toFileAbsolutePath);
                    }
                    break;
                default:
                    LOG.warn("Reached un-reachable condition businessType={}", businessType);
                    throw new UnsupportedOperationException("Reached unsupported condition " + businessType);
            }
        } catch (IOException e) {
            LOG.error("Failed adding store image={} reason={}", filename, e.getLocalizedMessage(), e);
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

    public BizStoreEntity addStoreImage(String qid, String codeQR, String filename, BufferedImage bufferedImage, boolean service) {
        File toFile = null;
        File decreaseResolution = null;
        File tempFile = null;

        try {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
            Set<String> images;
            if (service) {
                images = bizStore.getStoreServiceImages();
            } else {
                images = bizStore.getStoreInteriorImages();
            }

            while (images.size() >= 10) {
                String lastImage = images.stream().findFirst().get();
                deleteImage(qid, lastImage, bizStore.getCodeQR());
                /* Delete local reference. */
                images.remove(lastImage);
            }

            toFile = writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
            decreaseResolution = decreaseResolution(toFile, imageServiceWidth, imageServiceHeight);

            // /java/temp/directory/filename.extension
            String toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
            tempFile = new File(toFileAbsolutePath);
            writeToFile(tempFile, ImageIO.read(decreaseResolution));
            ftpService.upload(filename, bizStore.getCodeQR(), FtpService.SERVICE);

            images.add(filename);
            if (StringUtils.isNotBlank(qid)) {
                bizService.saveStore(bizStore, "Added Store Image");
            } else {
                LOG.info("Store auto-modified by system codeQR={} displayName={}", bizStore.getCodeQR(), bizStore.getDisplayName());
                bizStore.setElasticUpdatePending(Constants.DIRTY);
                bizStoreManager.save(bizStore);
            }

            LOG.debug("Uploaded store service file={}", toFileAbsolutePath);
            return bizStore;
        } catch (IOException e) {
            LOG.error("Failed adding store image={} reason={}", filename, e.getLocalizedMessage(), e);
            return null;
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

    public void deleteImage(String qid, String imageName, String codeQR) {
        /* Delete existing file business service image before the upload process began. */
        ftpService.delete(imageName, codeQR, FtpService.SERVICE);

        /* Delete from S3. */
        S3FileEntity s3File = new S3FileEntity(qid, imageName, FtpService.SERVICE_AWS).setCodeQR(codeQR);
        s3FileManager.save(s3File);
    }

    public void deleteProductImage(String qid, String imageName, String bizStoreId) {
        /* Delete existing file business service image before the upload process began. */
        ftpService.delete(imageName, bizStoreId, FtpService.PRODUCT);

        /* Delete from S3. */
        S3FileEntity s3File = new S3FileEntity(qid, imageName, FtpService.PRODUCT_AWS).setCodeQR(bizStoreId);
        s3FileManager.save(s3File);
    }

    public void deleteMarketImage(String qid, String imageName, String postId, BusinessTypeEnum businessType) {
        switch (businessType) {
            case PR:
                /* Delete existing file business service image before the upload process began. */
                ftpService.delete(imageName, postId, FtpService.MARKETPLACE_PROPERTY_RENTAL);

                /* Delete from S3. */
                S3FileEntity s3File = new S3FileEntity(qid, imageName, FtpService.MARKETPLACE_PROPERTY_RENTAL_AWS).setCodeQR(postId);
                s3FileManager.save(s3File);
                break;
            case HI:
                /* Delete existing file business service image before the upload process began. */
                ftpService.delete(imageName, postId, FtpService.MARKETPLACE_HOUSEHOLD_ITEM);

                /* Delete from S3. */
                s3File = new S3FileEntity(qid, imageName, FtpService.MARKETPLACE_HOUSEHOLD_ITEM_AWS).setCodeQR(postId);
                s3FileManager.save(s3File);
                break;
            default:
                LOG.warn("Reached un-reachable condition businessType={}", businessType);
                throw new UnsupportedOperationException("Reached unsupported condition " + businessType);
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
        File scaledFile = createTempFile(FilenameUtils.getBaseName(file.getName()) + APPEND, FileUtil.getFileExtension(file.getName()));

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

    /* For Images only. */
    public File writeToFile(String filename, BufferedImage bufferedImage) throws IOException {
        File toFile = createTempFile(FilenameUtils.getBaseName(filename), getFileExtension(filename));
        writeToFile(toFile, bufferedImage);
        return toFile;
    }

    /* For Images only. */
    public void writeToFile(File file, BufferedImage bufferedImage) throws IOException {
        ImageIO.write(bufferedImage, PNG_FORMAT, file);
    }

    public File writeToFile(String filename, InputStream inputStream) throws IOException {
        File toFile = createTempFile(FilenameUtils.getBaseName(filename), getFileExtension(filename));
        FileUtils.copyInputStreamToFile(inputStream, toFile);
        return toFile;
    }

    public void writeToFile(File file, InputStream inputStream) throws IOException {
        FileUtils.copyInputStreamToFile(inputStream, file);
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

    /** Generate zip file of CSV format for all business store id. Invoked via cron job. */
    public void findAllBizStoreWithBusinessType(BusinessTypeEnum businessType) {
        AtomicLong countBusiness = new AtomicLong(), countStores = new AtomicLong();
        try (Stream<BizNameEntity> stream = bizNameManager.findByBusinessType(businessType)) {
            stream.iterator().forEachRemaining(bizName -> {
                try {
                    countBusiness.getAndIncrement();
                    List<BizStoreEntity> bizStores = bizStoreManager.getAllBizStores(bizName.getId());
                    for (BizStoreEntity bizStore : bizStores) {
                        createPreferredBusinessFiles(bizStore.getId());
                    }

                    countStores.addAndGet(bizStores.size());
                } catch (Exception e) {
                    LOG.error("Failed processing for id={} businessName={}, type={}, reason={}",
                        bizName.getId(),
                        bizName.getBusinessName(),
                        businessType,
                        e.getLocalizedMessage(),
                        e);
                }
            });
            LOG.info("Number of business={} and store={} processed for preferred business product", countBusiness, countStores);
        }
    }

    /** Create tar file of products for preferred business with store id. */
    public void createPreferredBusinessFiles(String bizStoreId) throws IOException {
        List<StoreProductEntity> storeProducts = storeProductManager.findAll(bizStoreId);

        if (!ftpService.existFolder(PREFERRED_STORE + "/" + bizStoreId)) {
            boolean status = ftpService.createFolder(PREFERRED_STORE + "/" + bizStoreId);
            LOG.debug("Folder created bizStoreId={} successfully={}", bizStoreId, status);
        }

        File csv = FileUtil.createTempFile(bizStoreId, "csv");
        csv.deleteOnExit();
        Path pathOfCSV = Paths.get(csv.toURI());
        List<String> strings = new ArrayList<>();
        for (StoreProductEntity storeProduct : storeProducts) {
            strings.add(storeProduct.toCommaSeparatedString());
        }
        Files.write(pathOfCSV, strings, Constants.CHAR_SET_UTF8);

        String fileName = bizStoreId + "_" + DateUtil.dateToString(new Date());
        File tar = new File(FileUtil.getTmpDir(), fileName + ".tar.gz");
        tar.deleteOnExit();
        createTarGZ(pathOfCSV.toFile(), tar, fileName);

        /* Clean up existing file before uploading. */
        ftpService.deleteAllFilesInDirectory(PREFERRED_STORE + "/" + bizStoreId);
        ftpService.upload(tar.getName(), bizStoreId, PREFERRED_STORE);

        tar.delete();
        csv.delete();
    }

    /** Create zip file of preferred business product list. */
    @Mobile
    public FileObject getPreferredBusinessTarGZ(String bizStoreId, DefaultFileSystemManager manager) {
        if (ftpService.existFolder(PREFERRED_STORE + "/" + bizStoreId)) {
            FileObject[] fileObjects = ftpService.getAllFilesInDirectory(PREFERRED_STORE + "/" + bizStoreId, manager);
            if (null != fileObjects && 0 < fileObjects.length) {
                return fileObjects[0];
            }
        }

        try {
            createPreferredBusinessFiles(bizStoreId);
        } catch (IOException e) {
            LOG.error("Failed to create file for bizStoreId={} reason={}", bizStoreId, e.getLocalizedMessage(), e);
        }

        return null;
    }

    public void createTarGZ(File csv, File tar, String fileName) throws IOException {
        TarArchiveOutputStream tarOut = null;
        try {
            tarOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(tar))));
            addFileToTarGz(csv, tarOut, fileName);
        } finally {
            try {
                if (null != tarOut) {
                    tarOut.finish();
                    tarOut.close();
                }
            } catch (IOException e) {
                LOG.error("Failed to close file path={} fileName={} tar={} reason={}",
                    csv.toURI(), fileName, tar.toURI(), e.getLocalizedMessage(), e);
            }
        }
    }

    private void addFileToTarGz(File csv, TarArchiveOutputStream tOut, String fileName) throws IOException {
        TarArchiveEntry tarEntry = new TarArchiveEntry(csv, fileName + ".csv");
        tOut.putArchiveEntry(tarEntry);
        IOUtils.copy(new FileInputStream(csv), tOut);
        tOut.closeArchiveEntry();
    }

    /** Process bulk upload of CSV file for a store. */
    List<StoreProductEntity> processUploadedStoreProductCSVFile(InputStream in, BizStoreEntity bizStore) {
        try {
            Iterable<CSVRecord> records;
            Map<String, String> map = new HashMap<>();
            switch (bizStore.getBusinessType()) {
                case HS:
                    switch (HealthCareServiceEnum.valueOf(bizStore.getBizCategoryId())) {
                        case XRAY:
                            records = CSVFormat.DEFAULT
                                .withHeader(RADIOLOGY_PRODUCT_HEADERS)
                                .withFirstRecordAsHeader()
                                .parse(new InputStreamReader(in));

                            map = LabCategoryEnum.asMapWithNameAsKey_Self(LabCategoryEnum.XRAY);
                            break;
                        case SONO:
                            records = CSVFormat.DEFAULT
                                .withHeader(RADIOLOGY_PRODUCT_HEADERS)
                                .withFirstRecordAsHeader()
                                .parse(new InputStreamReader(in));

                            map = LabCategoryEnum.asMapWithNameAsKey_Self(LabCategoryEnum.SONO);
                            break;
                        case SCAN:
                            records = CSVFormat.DEFAULT
                                .withHeader(RADIOLOGY_PRODUCT_HEADERS)
                                .withFirstRecordAsHeader()
                                .parse(new InputStreamReader(in));

                            map = LabCategoryEnum.asMapWithNameAsKey_Self(LabCategoryEnum.SCAN);
                            break;
                        case SPEC:
                            records = CSVFormat.DEFAULT
                                .withHeader(RADIOLOGY_PRODUCT_HEADERS)
                                .withFirstRecordAsHeader()
                                .parse(new InputStreamReader(in));

                            map = LabCategoryEnum.asMapWithNameAsKey_Self(LabCategoryEnum.SPEC);
                            break;
                        case PHYS:
                            LOG.error("Reached unsupported condition={}", bizStore.getBizCategoryId());
                            throw new UnsupportedOperationException("Reached unsupported condition " + bizStore.getBizCategoryId());
                        case PATH:
                            LOG.error("Reached unsupported condition={}", bizStore.getBizCategoryId());
                            throw new UnsupportedOperationException("Reached unsupported condition " + bizStore.getBizCategoryId());
                        default:
                            LOG.error("Reached unsupported condition={}", bizStore.getBizCategoryId());
                            throw new UnsupportedOperationException("Reached unsupported condition " + bizStore.getBizCategoryId());
                    }
                    break;
                case PH:
                    records = CSVFormat.DEFAULT
                        .withHeader(STORE_PRODUCT_HEADERS)
                        .withFirstRecordAsHeader()
                        .parse(new InputStreamReader(in));

                    map = PharmacyCategoryEnum.asMapWithDescriptionAsKey();
                    break;
                default:
                    records = CSVFormat.DEFAULT
                        .withHeader(STORE_PRODUCT_HEADERS)
                        .withFirstRecordAsHeader()
                        .parse(new InputStreamReader(in));

                    List<StoreCategoryEntity> storeCategories = storeCategoryService.findAll(bizStore.getId());
                    for (StoreCategoryEntity t : storeCategories) {
                        map.put(t.getCategoryName(), t.getId());
                    }
            }

            List<StoreProductEntity> storeProducts = new ArrayList<>();
            for (CSVRecord record : records) {
                String storeCategoryId;
                try {
                    storeCategoryId = map.get(record.get("Category"));
                } catch (IllegalArgumentException e) {
                    LOG.warn("Failed parsing lineNumber={} reason={}", record.getRecordNumber(), e.getLocalizedMessage());
                    throw new CSVProcessingException("Error at line " + record.getRecordNumber() + ", Mapping for Store Category not found");
                }

                try {
                    StoreProductEntity storeProduct = getStoreProductEntityFromCSV(bizStore, record, storeCategoryId);
                    replacePopulatedDataFromCSV(storeProduct);
                    storeProducts.add(storeProduct);
                } catch (Exception e) {
                    LOG.warn("Failed parsing lineNumber={} reason={}", record.getRecordNumber(), e.getLocalizedMessage());
                    throw new CSVProcessingException("Error at line " + record.getRecordNumber());
                }
            }
            return storeProducts;
        } catch (IOException e) {
            LOG.warn("Error reason={}", e.getLocalizedMessage());
            throw new CSVParsingException("Invalid file");
        }
    }

    /** Add reference to image link. */
    private void replacePopulatedDataFromCSV(StoreProductEntity storeProduct) {
        StoreProductEntity storeProductFromDB = storeProductManager.findOne(storeProduct.getId());
        storeProduct.setProductImage(storeProductFromDB.getProductImage());
    }

    /** Read from a CSV file. */
    private StoreProductEntity getStoreProductEntityFromCSV(BizStoreEntity bizStore, CSVRecord record, String storeCategoryId) {
        StoreProductEntity storeProduct = new StoreProductEntity();
        switch (bizStore.getBusinessType()) {
            case HS:
                switch (HealthCareServiceEnum.valueOf(bizStore.getBizCategoryId())) {
                    case XRAY:
                        storeProduct
                            .setBizStoreId(bizStore.getId())
                            .setStoreCategoryId(storeCategoryId)
                            .setProductName(record.get("Name").trim())
                            .setProductPrice(StringUtils.isBlank(record.get("Price")) ? 100 : Integer.parseInt(record.get("Price")) * 100)
                            .setProductDiscount(StringUtils.isBlank(record.get("Discount")) ? 0 : Integer.parseInt(record.get("Discount")) * 100)
                            .setProductInfo(record.get("Info"))
                            .setUnitValue(1)
                            .setUnitOfMeasurement(UnitOfMeasurementEnum.CN)
                            .setPackageSize(1)
                            .setProductType(ProductTypeEnum.HS)
                            .setProductReference(null);
                        break;
                    case SONO:
                        storeProduct
                            .setBizStoreId(bizStore.getId())
                            .setStoreCategoryId(storeCategoryId)
                            .setProductName(record.get("Name").trim())
                            .setProductPrice(StringUtils.isBlank(record.get("Price")) ? 100 : Integer.parseInt(record.get("Price")) * 100)
                            .setProductDiscount(StringUtils.isBlank(record.get("Discount")) ? 0 : Integer.parseInt(record.get("Discount")) * 100)
                            .setProductInfo(record.get("Info"))
                            .setUnitValue(1)
                            .setUnitOfMeasurement(UnitOfMeasurementEnum.CN)
                            .setPackageSize(1)
                            .setProductType(ProductTypeEnum.HS)
                            .setProductReference(null);
                        break;
                    case SCAN:
                        storeProduct
                            .setBizStoreId(bizStore.getId())
                            .setStoreCategoryId(storeCategoryId)
                            .setProductName(record.get("Name").trim())
                            .setProductPrice(StringUtils.isBlank(record.get("Price")) ? 100 : Integer.parseInt(record.get("Price")) * 100)
                            .setProductDiscount(StringUtils.isBlank(record.get("Discount")) ? 0 : Integer.parseInt(record.get("Discount")) * 100)
                            .setProductInfo(record.get("Info"))
                            .setUnitValue(1)
                            .setUnitOfMeasurement(UnitOfMeasurementEnum.CN)
                            .setPackageSize(1)
                            .setProductType(ProductTypeEnum.HS)
                            .setProductReference(null);
                        break;
                    case MRI:
                        storeProduct
                            .setBizStoreId(bizStore.getId())
                            .setStoreCategoryId(storeCategoryId)
                            .setProductName(record.get("Name").trim())
                            .setProductPrice(StringUtils.isBlank(record.get("Price")) ? 100 : Integer.parseInt(record.get("Price")) * 100)
                            .setProductDiscount(StringUtils.isBlank(record.get("Discount")) ? 0 : Integer.parseInt(record.get("Discount")) * 100)
                            .setProductInfo(record.get("Info"))
                            .setUnitValue(1)
                            .setUnitOfMeasurement(UnitOfMeasurementEnum.CN)
                            .setPackageSize(1)
                            .setProductType(ProductTypeEnum.HS)
                            .setProductReference(null);
                        break;
                    case SPEC:
                        storeProduct
                            .setBizStoreId(bizStore.getId())
                            .setStoreCategoryId(storeCategoryId)
                            .setProductName(record.get("Name").trim())
                            .setProductPrice(StringUtils.isBlank(record.get("Price")) ? 100 : Integer.parseInt(record.get("Price")) * 100)
                            .setProductDiscount(StringUtils.isBlank(record.get("Discount")) ? 0 : Integer.parseInt(record.get("Discount")) * 100)
                            .setProductInfo(record.get("Info"))
                            .setUnitValue(1)
                            .setUnitOfMeasurement(UnitOfMeasurementEnum.CN)
                            .setPackageSize(1)
                            .setProductType(ProductTypeEnum.HS)
                            .setProductReference(null);
                        break;
                    case PHYS:
                        break;
                    case PATH:
                        break;
                    default:
                        LOG.error("Reached unsupported condition={}", bizStore.getBizCategoryId());
                        throw new UnsupportedOperationException("Reached unsupported condition " + bizStore.getBizCategoryId());
                }
                break;
            case PH:
                UnitOfMeasurementEnum unitOfMeasurementEnum;
                try {
                    unitOfMeasurementEnum = StringUtils.isBlank(record.get("Measurement")) ? null : UnitOfMeasurementEnum.valueOf(record.get("Measurement"));
                } catch (IllegalArgumentException e) {
                    LOG.warn("Failed parsing lineNumber={} reason={}", record.getRecordNumber(), e.getLocalizedMessage());
                    throw new CSVProcessingException("Error at line " + record.getRecordNumber() + ". Could not understand Unit " + record.get("Measurement"));
                }

                storeProduct
                    .setBizStoreId(bizStore.getId())
                    .setStoreCategoryId(storeCategoryId)
                    .setProductName(record.get("Name"))
                    .setProductPrice(StringUtils.isBlank(record.get("Price")) ? 100 : Integer.parseInt(record.get("Price")) * 100)
                    .setProductDiscount(StringUtils.isBlank(record.get("Discount")) ? 0 : Integer.parseInt(record.get("Discount")) * 100)
                    .setProductInfo(record.get("Info"))
                    .setUnitValue(StringUtils.isBlank(record.get("Unit")) ? 0 : Integer.parseInt(record.get("Unit")))
                    .setUnitOfMeasurement(unitOfMeasurementEnum)
                    .setPackageSize(StringUtils.isBlank(record.get("Package Size")) ? 1 : Integer.parseInt(record.get("Package Size")))
                    .setProductType(ProductTypeEnum.PH)
                    .setProductReference(record.get("Reference"));
                break;
            default:
                ProductTypeEnum productTypeEnum;
                try {
                    productTypeEnum = ProductTypeEnum.valueOf(record.get("Type"));
                } catch (IllegalArgumentException e) {
                    LOG.warn("Failed parsing lineNumber={} reason={}", record.getRecordNumber(), e.getLocalizedMessage());
                    throw new CSVProcessingException("Error at line " + record.getRecordNumber() + ". Could not understand Type " + record.get("Type"));
                }

                try {
                    unitOfMeasurementEnum = UnitOfMeasurementEnum.valueOf(record.get("Measurement"));
                } catch (IllegalArgumentException e) {
                    LOG.warn("Failed parsing lineNumber={} reason={}", record.getRecordNumber(), e.getLocalizedMessage());
                    throw new CSVProcessingException("Error at line " + record.getRecordNumber() + ". Could not understand Unit " + record.get("Measurement"));
                }

                storeProduct
                    .setBizStoreId(bizStore.getId())
                    .setStoreCategoryId(storeCategoryId)
                    .setProductName(record.get("Name"))
                    .setProductPrice(StringUtils.isBlank(record.get("Price")) ? 100 : Integer.parseInt(record.get("Price")) * 100)
                    .setProductDiscount(StringUtils.isBlank(record.get("Discount")) ? 0 : Integer.parseInt(record.get("Discount")) * 100)
                    .setProductInfo(record.get("Info"))
                    .setUnitValue(Integer.parseInt(record.get("Unit")))
                    .setUnitOfMeasurement(unitOfMeasurementEnum)
                    .setDisplayCaseTurnedOn(Integer.parseInt(record.get("Displayed")) == 1)
                    .setPackageSize(Integer.parseInt(record.get("Package Size")))
                    .setProductType(productTypeEnum)
                    .setProductReference(record.get("Reference"));
        }
        if (StringUtils.isNotBlank(record.get("Key")) && Validate.isValidObjectId(record.get("Key"))) {
            storeProduct.setId(record.get("Key"));
        } else {
            storeProduct.setId(CommonUtil.generateHexFromObjectId());
        }
        return storeProduct;
    }

    /** Write to CSV file. */
    File writeStoreProductToCSVFile(List<StoreProductEntity> storeProducts, BizStoreEntity bizStore) throws IOException {
        List<StoreCategoryEntity> storeCategories = storeCategoryService.findAll(bizStore.getId());
        Map<String, String> map = new HashMap<>();
        storeCategories.forEach(t -> map.put(t.getId(), t.getCategoryName()));

        File file = FileUtil.createTempFile(bizStore.getDisplayName(), ".csv");
        FileWriter out = new FileWriter(file);
        switch (bizStore.getBusinessType()) {
            case HS:
                switch (HealthCareServiceEnum.valueOf(bizStore.getBizCategoryId())) {
                    case XRAY:
                        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(RADIOLOGY_PRODUCT_HEADERS))) {
                            storeProducts.forEach((storeProduct) -> {
                                try {
                                    printer.printRecord(
                                        LabCategoryEnum.XRAY.getDescription(),
                                        storeProduct.getProductName(),
                                        storeProduct.getProductPrice() / 100,
                                        storeProduct.getProductDiscount() / 100,
                                        storeProduct.getProductInfo(),
                                        storeProduct.getId()
                                    );
                                } catch (IOException e) {
                                    LOG.error("Failed writing to a file id={} storeName={}", storeProduct.getId(), bizStore.getDisplayName());
                                }
                            });
                        }
                        break;
                    case SONO:
                        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(RADIOLOGY_PRODUCT_HEADERS))) {
                            storeProducts.forEach((storeProduct) -> {
                                try {
                                    printer.printRecord(
                                        LabCategoryEnum.SONO.getDescription(),
                                        storeProduct.getProductName(),
                                        storeProduct.getProductPrice() / 100,
                                        storeProduct.getProductDiscount() / 100,
                                        storeProduct.getProductInfo(),
                                        storeProduct.getId()
                                    );
                                } catch (IOException e) {
                                    LOG.error("Failed writing to a file id={} storeName={}", storeProduct.getId(), bizStore.getDisplayName());
                                }
                            });
                        }
                        break;
                    case SCAN:
                        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(RADIOLOGY_PRODUCT_HEADERS))) {
                            storeProducts.forEach((storeProduct) -> {
                                try {
                                    printer.printRecord(
                                        LabCategoryEnum.SCAN.getDescription(),
                                        storeProduct.getProductName(),
                                        storeProduct.getProductPrice() / 100,
                                        storeProduct.getProductDiscount() / 100,
                                        storeProduct.getProductInfo(),
                                        storeProduct.getId()
                                    );
                                } catch (IOException e) {
                                    LOG.error("Failed writing to a file id={} storeName={}", storeProduct.getId(), bizStore.getDisplayName());
                                }
                            });
                        }
                        break;
                    case MRI:
                        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(RADIOLOGY_PRODUCT_HEADERS))) {
                            storeProducts.forEach((storeProduct) -> {
                                try {
                                    printer.printRecord(
                                        LabCategoryEnum.MRI.getDescription(),
                                        storeProduct.getProductName(),
                                        storeProduct.getProductPrice() / 100,
                                        storeProduct.getProductDiscount() / 100,
                                        storeProduct.getProductInfo(),
                                        storeProduct.getId()
                                    );
                                } catch (IOException e) {
                                    LOG.error("Failed writing to a file id={} storeName={}", storeProduct.getId(), bizStore.getDisplayName());
                                }
                            });
                        }
                        break;
                    case SPEC:
                        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(RADIOLOGY_PRODUCT_HEADERS))) {
                            storeProducts.forEach((storeProduct) -> {
                                try {
                                    printer.printRecord(
                                        LabCategoryEnum.SPEC.getDescription(),
                                        storeProduct.getProductName(),
                                        storeProduct.getProductPrice() / 100,
                                        storeProduct.getProductDiscount() / 100,
                                        storeProduct.getProductInfo(),
                                        storeProduct.getId()
                                    );
                                } catch (IOException e) {
                                    LOG.error("Failed writing to a file id={} storeName={}", storeProduct.getId(), bizStore.getDisplayName());
                                }
                            });
                        }
                        break;
                    case PHYS:
                        break;
                    case PATH:
                        break;
                    default:
                        LOG.error("Reached unsupported condition={}", bizStore.getBizCategoryId());
                        throw new UnsupportedOperationException("Reached unsupported condition " + bizStore.getBizCategoryId());
                }
                break;
            case PH:
                try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(PHARMACY_PRODUCT_HEADERS))) {
                    storeProducts.forEach((storeProduct) -> {
                        try {
                            printer.printRecord(
                                StringUtils.isBlank(map.get(storeProduct.getStoreCategoryId()))
                                    ? PharmacyCategoryEnum.valueOf(storeProduct.getStoreCategoryId()).getDescription()
                                    : map.get(storeProduct.getStoreCategoryId()),
                                storeProduct.getProductName(),
                                storeProduct.getProductPrice() / 100,
                                storeProduct.getProductDiscount() / 100,
                                storeProduct.getProductInfo(),
                                storeProduct.getUnitValue(),
                                null == storeProduct.getUnitOfMeasurement() ? "" : storeProduct.getUnitOfMeasurement().name(),
                                storeProduct.getPackageSize(),
                                storeProduct.getId(),
                                storeProduct.getProductReference()
                            );
                        } catch (IOException e) {
                            LOG.error("Failed writing to a file id={} storeName={}", storeProduct.getId(), bizStore.getDisplayName());
                        }
                    });
                }
                break;
            default:
                try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(STORE_PRODUCT_HEADERS))) {
                    storeProducts.forEach((storeProduct) -> {
                        try {
                            printer.printRecord(
                                StringUtils.isBlank(map.get(storeProduct.getStoreCategoryId())) ? storeProduct.getStoreCategoryId() : map.get(storeProduct.getStoreCategoryId()),
                                storeProduct.getProductName(),
                                storeProduct.getProductPrice() / 100,
                                storeProduct.getProductDiscount() / 100,
                                storeProduct.getProductInfo(),
                                storeProduct.getProductType().name(),
                                storeProduct.getUnitValue(),
                                null == storeProduct.getUnitOfMeasurement() ? "" : storeProduct.getUnitOfMeasurement().name(),
                                storeProduct.getPackageSize(),
                                storeProduct.isDisplayCaseTurnedOn() ? 1 : 0,
                                storeProduct.getId(),
                                storeProduct.getProductReference()
                            );
                        } catch (IOException e) {
                            LOG.error("Failed writing to a file id={} storeName={}", storeProduct.getId(), bizStore.getDisplayName());
                        }
                    });
                }
                break;
        }

        return file;
    }

    private String getFilenameWithOriginal(String filename) {
        String baseName = FilenameUtils.getBaseName(filename) + ORIGINAL;
        String fileExtension = FileUtil.getFileExtension(filename);

        return baseName + DOT + fileExtension;
    }
}
