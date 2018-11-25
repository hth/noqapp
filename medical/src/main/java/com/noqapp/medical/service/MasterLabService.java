package com.noqapp.medical.service;

import static com.noqapp.service.FtpService.MASTER_MEDICAL;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.medical.repository.MasterLabManager;
import com.noqapp.service.FileService;
import com.noqapp.service.FtpService;

import org.apache.commons.vfs2.FileObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

    @Autowired
    public MasterLabService(
        MasterLabManager masterLabManager,
        FtpService ftpService,
        FileService fileService
    ) {
        this.masterLabManager = masterLabManager;
        this.ftpService = ftpService;
        this.fileService = fileService;
    }

    public List<MasterLabEntity> findAll() {
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
    public FileObject getMasterTarGZ() {
        if (ftpService.existFolder(MASTER_MEDICAL)) {
            FileObject[] fileObjects = ftpService.getAllFilesInDirectory(MASTER_MEDICAL);
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
}
