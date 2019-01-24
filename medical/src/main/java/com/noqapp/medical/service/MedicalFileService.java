package com.noqapp.medical.service;

import static com.noqapp.common.utils.FileUtil.createRandomFilenameOf24Chars;
import static com.noqapp.common.utils.FileUtil.getFileExtensionWithDot;
import static com.noqapp.common.utils.FileUtil.getFileSeparator;
import static com.noqapp.common.utils.FileUtil.getTmpDir;
import static com.noqapp.service.FileService.RADIOLOGY_PRODUCT_HEADERS;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.S3FileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * hitender
 * 2018-12-11 06:21
 */
@Service
public class MedicalFileService {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalFileService.class);

    private MedicalRecordManager medicalRecordManager;
    private S3FileManager s3FileManager;
    private FileService fileService;
    private FtpService ftpService;

    @Autowired
    public MedicalFileService(
        MedicalRecordManager medicalRecordManager,
        S3FileManager s3FileManager,
        FileService fileService,
        FtpService ftpService
    ) {
        this.medicalRecordManager = medicalRecordManager;
        this.s3FileManager = s3FileManager;
        this.fileService = fileService;
        this.ftpService = ftpService;
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
                LOG.warn("Not file exists for medicalRecordId={} by qid={} filename={}", medicalReferenceId, qid, filename);
            }
        }
    }
}
