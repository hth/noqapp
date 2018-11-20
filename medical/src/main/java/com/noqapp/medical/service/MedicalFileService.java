package com.noqapp.medical.service;

import static com.noqapp.service.FtpService.MASTER_MEDICAL;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterPathologyEntity;
import com.noqapp.medical.domain.MasterRadiologyEntity;
import com.noqapp.service.FileService;
import com.noqapp.service.FtpService;

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
 * 11/18/18 7:37 PM
 */
@Service
public class MedicalFileService {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalFileService.class);

    private MedicalMasterService medicalMasterService;
    private FtpService ftpService;
    private FileService fileService;

    @Autowired
    public MedicalFileService(
        MedicalMasterService medicalMasterService,
        FtpService ftpService,
        FileService fileService
    ) {
        this.medicalMasterService = medicalMasterService;
        this.ftpService = ftpService;
        this.fileService = fileService;
    }
    /**
     * Generate master tar file of CSV format for medical business types for specific medical department. Invoked via cron job.
     */
    public void createMasterFileAssociatedWithBusinessType(
        BusinessTypeEnum businessType,
        MedicalDepartmentEnum medicalDepartment
    ) throws IOException {
        if (!ftpService.existFolder(MASTER_MEDICAL + "/" + businessType.name() + "/" + medicalDepartment.name())) {
            boolean status = ftpService.createFolder(MASTER_MEDICAL +  "/" + businessType.name() + "/" + medicalDepartment.name());
            LOG.debug("Folder created MASTER_MEDICAL/{}/{} successfully={}", businessType.name(), medicalDepartment.name(), status);
        }

        File csv = FileUtil.createTempFile(businessType + "_" + medicalDepartment, "csv");
        csv.deleteOnExit();
        Path pathOfCSV = Paths.get(csv.toURI());
        List<String> strings = new ArrayList<>();

        switch (businessType) {
            case PH:
                break;
            case RA:
                List<MasterRadiologyEntity> masterRadiologies = medicalMasterService.findAllRadiologyMatching(medicalDepartment);
                for (MasterRadiologyEntity masterRadiology : masterRadiologies) {
                    strings.add(masterRadiology.toCommaSeparatedString());
                }
                break;
            case PY:
                break;
            case PT:
                List<MasterPathologyEntity> masterPathologies = medicalMasterService.findAllPathologyMatching(medicalDepartment);
                for (MasterPathologyEntity masterPathology : masterPathologies) {
                    strings.add(masterPathology.toCommaSeparatedString());
                }
                break;
        }
        Files.write(pathOfCSV, strings, StandardCharsets.UTF_8);

        String fileName = businessType.getName() + "_" + medicalDepartment.name() + "_" + DateUtil.dateToString(new Date());
        File tar = new File(FileUtil.getTmpDir(), fileName + ".tar.gz");
        tar.deleteOnExit();
        fileService.createTarGZ(pathOfCSV.toFile(), tar,  fileName);

        /* Clean up existing file before uploading. */
        ftpService.deleteAllFilesInDirectory(MASTER_MEDICAL + "/" + businessType.name() + "/" + medicalDepartment.name());
        ftpService.upload(tar.getName(), "/" + businessType.name() + "/" + medicalDepartment.name(), MASTER_MEDICAL);

        tar.delete();
        csv.delete();
    }
}
