package com.noqapp.medical.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.ITest;
import com.noqapp.medical.domain.MasterPathologyEntity;
import com.noqapp.medical.domain.MasterRadiologyEntity;

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
class MedicalMasterServiceITest extends ITest {

    private MedicalMasterService medicalMasterService;

    @BeforeEach
    void setUp() {
        medicalMasterService = new MedicalMasterService(
            masterPathologyManager,
            masterRadiologyManager,
            fileService);
    }

    @Test
    void masterPathology() {
        MasterPathologyEntity masterPathologyEntity = new MasterPathologyEntity()
            .setProductName("Blood Test")
            .addMedicalDepartment(MedicalDepartmentEnum.CRD)
            .addMedicalDepartment(MedicalDepartmentEnum.ORT);
        medicalMasterService.savePathology(masterPathologyEntity);

        findAllPathologyMatching();
    }

    @Test
    void masterRadiology() {
        MasterRadiologyEntity masterRadiologyEntity = new MasterRadiologyEntity()
            .setProductName("Pelvis XRAY")
            .addMedicalDepartment(MedicalDepartmentEnum.CRD)
            .addMedicalDepartment(MedicalDepartmentEnum.ORT);
        medicalMasterService.saveRadiology(masterRadiologyEntity);

        masterRadiologyEntity = new MasterRadiologyEntity()
            .setProductName("Hand XRAY")
            .addMedicalDepartment(MedicalDepartmentEnum.ORT);
        medicalMasterService.saveRadiology(masterRadiologyEntity);

        findAllRadiologyMatching();
    }

    void findAllPathologyMatching() {
        List<MasterPathologyEntity> pathologies = medicalMasterService.findAllPathologyMatching(MedicalDepartmentEnum.CRD);
        assertEquals(1, pathologies.size());
    }

    void findAllRadiologyMatching() {
        List<MasterRadiologyEntity> radiologies = medicalMasterService.findAllRadiologyMatching(MedicalDepartmentEnum.CRD);
        assertEquals(1, radiologies.size());

        radiologies = medicalMasterService.findAllRadiologyMatching(MedicalDepartmentEnum.ORT);
        assertEquals(2, radiologies.size());
    }
}