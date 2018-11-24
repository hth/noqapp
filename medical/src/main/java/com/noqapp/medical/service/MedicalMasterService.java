package com.noqapp.medical.service;

import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.medical.repository.MasterLabManager;

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

    private MasterLabManager masterLabManager;

    @Autowired
    public MedicalMasterService(
        MasterLabManager masterLabManager
    ) {
        this.masterLabManager = masterLabManager;
    }

    public void saveRadiology(MasterLabEntity masterRadiology) {
        masterLabManager.save(masterRadiology);
    }

    public List<MasterLabEntity> findAllMatching(MedicalDepartmentEnum medicalDepartment) {
        return masterLabManager.findAllMatching(medicalDepartment);
    }
}
