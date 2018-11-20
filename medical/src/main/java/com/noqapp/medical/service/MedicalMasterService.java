package com.noqapp.medical.service;

import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterPathologyEntity;
import com.noqapp.medical.domain.MasterRadiologyEntity;
import com.noqapp.medical.repository.MasterPathologyManager;
import com.noqapp.medical.repository.MasterRadiologyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 11/18/18 12:19 PM
 */
@Service
public class MedicalMasterService {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalMasterService.class);

    private MasterPathologyManager masterPathologyManager;
    private MasterRadiologyManager masterRadiologyManager;

    @Autowired
    public MedicalMasterService(
        MasterPathologyManager masterPathologyManager,
        MasterRadiologyManager masterRadiologyManager
    ) {
        this.masterPathologyManager = masterPathologyManager;
        this.masterRadiologyManager = masterRadiologyManager;
    }

    public void savePathology(MasterPathologyEntity masterPathology) {
        masterPathologyManager.save(masterPathology);
    }

    public void saveRadiology(MasterRadiologyEntity masterRadiology) {
        masterRadiologyManager.save(masterRadiology);
    }

    public List<MasterPathologyEntity> findAllPathologyMatching(MedicalDepartmentEnum medicalDepartment) {
        return masterPathologyManager.findAllMatching(medicalDepartment);
    }

    public List<MasterRadiologyEntity> findAllRadiologyMatching(MedicalDepartmentEnum medicalDepartment) {
        return masterRadiologyManager.findAllMatching(medicalDepartment);
    }
}
