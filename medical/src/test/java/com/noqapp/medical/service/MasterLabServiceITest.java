package com.noqapp.medical.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.ITest;
import com.noqapp.medical.domain.MasterLabEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

/**
 * hitender
 * 11/18/18 1:50 PM
 */
@DisplayName("Master Medical Record")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("Repository")
class MasterLabServiceITest extends ITest {

    private MasterLabService masterLabService;

    @BeforeEach
    void setUp() {
        masterLabService = new MasterLabService(masterLabManager, ftpService, fileService, medicalFileService, medicalTransactionService);
    }

    @Test
    void masterRadiology() {
        MasterLabEntity masterLabEntity = new MasterLabEntity()
            .setProductName("Pelvis XRAY")
            .setProductShortName("Pelvis")
            .setHealthCareService(HealthCareServiceEnum.XRAY)
            .addMedicalDepartment(MedicalDepartmentEnum.CRD)
            .addMedicalDepartment(MedicalDepartmentEnum.ORT);
        masterLabService.save(masterLabEntity);

        masterLabEntity = new MasterLabEntity()
            .setProductName("Hand XRAY")
            .setProductShortName("Hand")
            .setHealthCareService(HealthCareServiceEnum.XRAY)
            .addMedicalDepartment(MedicalDepartmentEnum.ORT);
        masterLabService.save(masterLabEntity);

        findAllRadiologyMatching();
    }

    void findAllRadiologyMatching() {
        List<MasterLabEntity> radiologies = masterLabService.findAllMatching(MedicalDepartmentEnum.CRD);
        assertEquals(1, radiologies.size());

        radiologies = masterLabService.findAllMatching(MedicalDepartmentEnum.ORT);
        assertEquals(2, radiologies.size());
    }
}