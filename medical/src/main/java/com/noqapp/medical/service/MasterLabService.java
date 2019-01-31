package com.noqapp.medical.service;

import static com.noqapp.service.FtpService.MASTER_MEDICAL;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.medical.repository.MasterLabManager;
import com.noqapp.medical.transaction.MedicalTransactionService;
import com.noqapp.service.FileService;
import com.noqapp.service.FtpService;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;
import com.noqapp.service.exceptions.FailedTransactionException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * hitender
 * 2018-11-23 09:01
 */
@Service
public class MasterLabService {
    private static final Logger LOG = LoggerFactory.getLogger(MasterLabService.class);

    private MasterLabManager masterLabManager;
    private FtpService ftpService;
    private FileService fileService;
    private MedicalFileService medicalFileService;
    private MedicalTransactionService medicalTransactionService;

    @Autowired
    public MasterLabService(
        MasterLabManager masterLabManager,
        FtpService ftpService,
        FileService fileService,
        MedicalFileService medicalFileService,
        MedicalTransactionService medicalTransactionService
    ) {
        this.masterLabManager = masterLabManager;
        this.ftpService = ftpService;
        this.fileService = fileService;
        this.medicalFileService = medicalFileService;
        this.medicalTransactionService = medicalTransactionService;
    }

    private List<MasterLabEntity> findAll() {
        return masterLabManager.findAll();
    }

    public void save(MasterLabEntity masterLab) {
        masterLabManager.save(masterLab);
    }

    /** Create tar file of products for preferred business with store id. */
    public void createMasterFiles() throws IOException {
        List<MasterLabEntity> storeProducts = findAll();

        if (!ftpService.existFolder(MASTER_MEDICAL)) {
            boolean status = ftpService.createFolder(MASTER_MEDICAL);
            LOG.debug("Folder master created successfully={}", status);
        }

        File csv = FileUtil.createTempFile("lab", "csv");
        csv.deleteOnExit();
        Path pathOfCSV = Paths.get(csv.toURI());
        List<String> strings = new ArrayList<>();
        for (MasterLabEntity storeProduct : storeProducts) {
            strings.add(storeProduct.toCommaSeparatedString());
        }
        Files.write(pathOfCSV, strings, StandardCharsets.UTF_8);

        String fileName = "lab" + "_" + DateUtil.dateToString(new Date());
        File tar = new File(FileUtil.getTmpDir(), fileName + ".tar.gz");
        tar.deleteOnExit();
        fileService.createTarGZ(pathOfCSV.toFile(), tar,  fileName);

        /* Clean up existing file before uploading. */
        ftpService.deleteAllFilesInDirectory(MASTER_MEDICAL);
        ftpService.upload(tar.getName(), null, MASTER_MEDICAL);

        tar.delete();
        csv.delete();
    }

    /** Create zip file of preferred business product list. */
    @Mobile
    public FileObject getMasterTarGZ(DefaultFileSystemManager manager) {
        if (ftpService.existFolder(MASTER_MEDICAL)) {
            FileObject[] fileObjects = ftpService.getAllFilesInDirectory(MASTER_MEDICAL, manager);
            if (null != fileObjects && 0 < fileObjects.length) {
                return fileObjects[0];
            }
        }

        try {
            createMasterFiles();
        } catch (IOException e) {
            LOG.error("Failed to create master file for reason={}", e.getLocalizedMessage(), e);
        }

        return null;
    }

    public List<MasterLabEntity> findAllMatching(MedicalDepartmentEnum medicalDepartment) {
        return masterLabManager.findAllMatching(medicalDepartment);
    }

    public int bulkUpdateStoreProduct(InputStream in, HealthCareServiceEnum healthCareService) {
        try {
            List<MasterLabEntity> masterLabs = medicalFileService.processUploadedForMasterProductCSVFile(in, healthCareService);
            if (!masterLabs.isEmpty()) {
                medicalTransactionService.bulkProductUpdate(masterLabs, healthCareService);
            }
            in.close();
            return masterLabs.size();
        } catch (CSVParsingException e) {
            LOG.warn("Failed parsing CSV file healthCareService={} reason={}", healthCareService, e.getLocalizedMessage());
            throw e;
        } catch (CSVProcessingException e) {
            LOG.warn("Failed processing CSV file healthCareService={} reason={}", healthCareService, e.getLocalizedMessage());
            throw e;
        } catch(FailedTransactionException e) {
            LOG.warn("Failed transaction healthCareService={} reason={}", healthCareService, e.getLocalizedMessage());
            throw e;
        } catch (IOException e) {
            LOG.error("Error reason={}", e.getLocalizedMessage(), e);
        }

        return 0;
    }

    @Async
    public void flagData(String productName, HealthCareServiceEnum healthCareService, String qid) {
        MasterLabEntity masterLab = masterLabManager.findOne(productName, healthCareService);
        masterLab
            .addFlaggedBy(qid)
            .setTimesFlagged(masterLab.getTimesFlagged() + 1);

        masterLabManager.save(masterLab);
    }
}
