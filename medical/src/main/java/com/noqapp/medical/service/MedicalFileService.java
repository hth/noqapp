package com.noqapp.medical.service;

import static com.noqapp.common.utils.FileUtil.createRandomFilenameOf24Chars;
import static com.noqapp.common.utils.FileUtil.getFileExtensionWithDot;
import static com.noqapp.common.utils.FileUtil.getFileSeparator;
import static com.noqapp.common.utils.FileUtil.getTmpDir;
import static com.noqapp.service.FileService.RADIOLOGY_PRODUCT_HEADERS;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.S3FileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.medical.LabCategoryEnum;
import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.medical.domain.MedicalPathologyEntity;
import com.noqapp.medical.domain.MedicalRadiologyEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.repository.MedicalPathologyManager;
import com.noqapp.medical.repository.MedicalRadiologyManager;
import com.noqapp.medical.repository.MedicalRecordManager;
import com.noqapp.repository.S3FileManager;
import com.noqapp.service.FileService;
import com.noqapp.service.FtpService;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.imageio.ImageIO;

/**
 * hitender
 * 2018-12-11 06:21
 */
@Service
public class MedicalFileService {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalFileService.class);

    private MedicalRecordManager medicalRecordManager;
    private MedicalPathologyManager medicalPathologyManager;
    private MedicalRadiologyManager medicalRadiologyManager;
    private S3FileManager s3FileManager;
    private FileService fileService;
    private FtpService ftpService;

    private ExecutorService executorService;

    @Autowired
    public MedicalFileService(
        MedicalRecordManager medicalRecordManager,
        MedicalPathologyManager medicalPathologyManager,
        MedicalRadiologyManager medicalRadiologyManager,
        S3FileManager s3FileManager,
        FileService fileService,
        FtpService ftpService
    ) {
        this.medicalRecordManager = medicalRecordManager;
        this.medicalPathologyManager = medicalPathologyManager;
        this.medicalRadiologyManager = medicalRadiologyManager;
        this.s3FileManager = s3FileManager;
        this.fileService = fileService;
        this.ftpService = ftpService;

        this.executorService = newCachedThreadPool();
    }

    /** Process bulk upload of CSV file for a store. */
    List<MasterLabEntity> processUploadedForMasterProductCSVFile(InputStream in, HealthCareServiceEnum healthCareService) {
        try {
            Iterable<CSVRecord> records;
            switch (healthCareService) {
                case XRAY:
                case SONO:
                case SCAN:
                case SPEC:
                case PATH:
                    records = CSVFormat.DEFAULT
                        .withHeader(RADIOLOGY_PRODUCT_HEADERS)
                        .withFirstRecordAsHeader()
                        .parse(new InputStreamReader(in));
                    break;
                case PHYS:
                    LOG.error("Reached unsupported condition={}", healthCareService);
                    throw new UnsupportedOperationException("Reached unsupported condition " + healthCareService);
                default:
                    LOG.error("Reached unsupported condition={}", healthCareService);
                    throw new UnsupportedOperationException("Reached unsupported condition " + healthCareService);
            }

            List<MasterLabEntity> storeProducts = new ArrayList<>();
            for (CSVRecord record : records) {
                try {
                    MasterLabEntity storeProduct = getStoreProductEntityFromCSV(record, healthCareService);
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

    /** Read from a CSV file. */
    private MasterLabEntity getStoreProductEntityFromCSV(CSVRecord record, HealthCareServiceEnum healthCareService) {
        MasterLabEntity masterLab = new MasterLabEntity();
        switch (healthCareService) {
            case XRAY:
            case SONO:
            case SCAN:
            case MRI:
            case SPEC:
            case PATH:
                masterLab
                    .setProductName(record.get("Name").trim())
                    .setProductShortName(record.get("Name").trim())
                    .setHealthCareService(healthCareService);
                break;
            case PHYS:
                break;
            default:
                LOG.error("Reached unsupported condition={}", healthCareService);
                throw new UnsupportedOperationException("Reached unsupported condition " + healthCareService);
        }
        if (StringUtils.isNotBlank(record.get("Key")) && Validate.isValidObjectId(record.get("Key"))) {
            masterLab.setId(record.get("Key"));
        } else {
            masterLab.setId(CommonUtil.generateHexFromObjectId());
        }
        return masterLab;
    }

    @Mobile
    @Async
    public void addMedicalImage(String recordReferenceId, String filename, BufferedImage bufferedImage) {
        LOG.debug("Adding medical image {} {}", recordReferenceId, filename);
        MedicalRecordEntity medicalRecord = medicalRecordManager.findById(recordReferenceId);

        File toFile = null;
        File tempFile = null;
        try {
            toFile = fileService.writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);

            // /java/temp/directory/filename.extension
            String toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
            tempFile = new File(toFileAbsolutePath);
            fileService.writeToFile(tempFile, ImageIO.read(toFile));
            ftpService.upload(filename, recordReferenceId, FtpService.MEDICAL);

            medicalRecord.addImage(filename);
            medicalRecordManager.save(medicalRecord);
        } catch (IOException e) {
            LOG.error("Failed adding medical {} image={} reason={}", recordReferenceId, filename, e.getLocalizedMessage(), e);
        } finally {
            if (null != toFile) {
                toFile.delete();
            }

            if (null != tempFile) {
                tempFile.delete();
            }
        }
    }

    @Mobile
    @Async
    public void removeMedicalImage(String qid, String medicalReferenceId, String filename) {
        LOG.debug("Remove medical image {} {} {}", qid, medicalReferenceId, filename);
        if (StringUtils.isNotBlank(filename)) {
            MedicalRecordEntity medicalRecord = medicalRecordManager.findById(medicalReferenceId);
            if (null != medicalRecord.getImages() && !medicalRecord.getImages().isEmpty()) {
                /* Delete existing file business service image before the upload process began. */
                ftpService.delete(filename, medicalReferenceId, FtpService.MEDICAL);

                /* Delete from S3. */
                s3FileManager.save(new S3FileEntity(qid, medicalReferenceId + "/" + filename, FtpService.MEDICAL_AWS));
                medicalRecord.getImages().remove(filename);
                medicalRecordManager.save(medicalRecord);
            } else {
                LOG.warn("No file exists for medicalRecordId={} by qid={} filename={}", medicalReferenceId, qid, filename);
            }
        }
    }

    @Mobile
    @Async
    public void addLabImage(String transactionId, String filename, InputStream inputStream, LabCategoryEnum labCategory, String mimeType) {
        LOG.debug("Adding lab image {} {}", transactionId, filename);
        MedicalRadiologyEntity medicalRadiology = null;
        MedicalPathologyEntity medicalPathology = null;
        String id;
        switch (labCategory) {
            case MRI:
            case SONO:
            case XRAY:
            case SCAN:
            case SPEC:
                medicalRadiology = medicalRadiologyManager.findByTransactionId(transactionId);
                id = medicalRadiology.getId();
                break;
            case PATH:
                medicalPathology = medicalPathologyManager.findByTransactionId(transactionId);
                id = medicalPathology.getId();
                break;
            default:
                LOG.error("Reached unreachable condition {}", labCategory);
                throw new UnsupportedOperationException("Reached unreachable condition");
        }

        File toFile = null;
        File tempFile = null;
        try {
            if (mimeType.endsWith("pdf")) {
                toFile = fileService.writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), inputStream);
            } else {
                BufferedImage bufferedImage = fileService.bufferedImage(inputStream);
                toFile = fileService.writeToFile(createRandomFilenameOf24Chars() + getFileExtensionWithDot(filename), bufferedImage);
            }

            // /java/temp/directory/filename.extension
            String toFileAbsolutePath = getTmpDir() + getFileSeparator() + filename;
            tempFile = new File(toFileAbsolutePath);
            if (mimeType.endsWith("pdf")) {
                fileService.writeToFile(tempFile, new FileInputStream(toFile));
            } else {
                fileService.writeToFile(tempFile, ImageIO.read(toFile));
            }
            ftpService.upload(filename, id, FtpService.MEDICAL);

            switch (labCategory) {
                case MRI:
                case SONO:
                case XRAY:
                case SCAN:
                case SPEC:
                    medicalRadiology.addImage(filename);
                    medicalRadiologyManager.save(medicalRadiology);
                    break;
                case PATH:
                    medicalPathology.addImage(filename);
                    medicalPathologyManager.save(medicalPathology);
                    break;
                default:
                    LOG.error("Reached unreachable condition {}", labCategory);
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
        } catch (IOException e) {
            LOG.error("Failed adding medical {} image={} reason={}", id, filename, e.getLocalizedMessage(), e);
        } finally {
            if (null != toFile) {
                toFile.delete();
            }

            if (null != tempFile) {
                tempFile.delete();
            }
        }
    }

    @Mobile
    @Async
    public void removeLabImage(String qid, String transactionId, String filename, LabCategoryEnum labCategory) {
        LOG.debug("Remove lab image {} {} {}", qid, transactionId, filename);
        MedicalRadiologyEntity medicalRadiology = null;
        MedicalPathologyEntity medicalPathology = null;
        String id;
        switch (labCategory) {
            case MRI:
            case SONO:
            case XRAY:
            case SCAN:
            case SPEC:
                medicalRadiology = medicalRadiologyManager.findByTransactionId(transactionId);
                id = medicalRadiology.getId();
                break;
            case PATH:
                medicalPathology = medicalPathologyManager.findByTransactionId(transactionId);
                id = medicalPathology.getId();
                break;
            default:
                LOG.error("Reached unreachable condition {}", labCategory);
                throw new UnsupportedOperationException("Reached unreachable condition");
        }

        if (StringUtils.isNotBlank(filename)) {
            switch (labCategory) {
                case MRI:
                case SONO:
                case XRAY:
                case SCAN:
                case SPEC:
                    if (null != medicalRadiology.getImages() && !medicalRadiology.getImages().isEmpty()) {
                        /* Delete existing file business service image before the upload process began. */
                        ftpService.delete(filename, id, FtpService.MEDICAL);

                        /* Delete from S3. */
                        s3FileManager.save(new S3FileEntity(qid, id + "/" + filename, FtpService.MEDICAL_AWS));
                        medicalRadiology.getImages().remove(filename);
                        medicalRadiologyManager.save(medicalRadiology);
                    } else {
                        LOG.warn("No file exists for medicalRecordId={} by qid={} filename={}", id, qid, filename);
                    }
                    break;
                case PATH:
                    if (null != medicalPathology.getImages() && !medicalPathology.getImages().isEmpty()) {
                        /* Delete existing file business service image before the upload process began. */
                        ftpService.delete(filename, id, FtpService.MEDICAL);

                        /* Delete from S3. */
                        s3FileManager.save(new S3FileEntity(qid, id + "/" + filename, FtpService.MEDICAL_AWS));
                        medicalPathology.getImages().remove(filename);
                        medicalPathologyManager.save(medicalPathology);
                    } else {
                        LOG.warn("No file exists for medicalRecordId={} by qid={} filename={}", id, qid, filename);
                    }
                    break;
                default:
                    LOG.error("Reached unreachable condition {}", labCategory);
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
        }
    }

    @Mobile
    public String processMedicalImage(String recordReferenceId, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            String filename = FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType);
            executorService.submit(() -> addMedicalImage(recordReferenceId, filename, bufferedImage));
            return filename;
        } else {
            LOG.error("Failed mime mismatch found={} sentMime={}", mimeType, multipartFile.getContentType());
            throw new RuntimeException("Mime type mismatch");
        }
    }

    /** A lab report can be Image, PDF. */
    public String processLabReport(String transactionId, MultipartFile multipartFile, LabCategoryEnum labCategory) throws IOException {
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            InputStream inputStream = multipartFile.getInputStream();
            String filename = FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType);
            executorService.submit(() -> addLabImage(transactionId, filename, inputStream, labCategory, mimeType));
            return filename;
        } else {
            LOG.error("Failed mime mismatch found={} sentMime={}", mimeType, multipartFile.getContentType());
            throw new RuntimeException("Mime type mismatch");
        }
    }
}
