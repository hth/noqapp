package com.noqapp.medical.repository;

import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterLabEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 11/16/18 3:17 PM
 */
public interface MasterLabManager extends RepositoryManager<MasterLabEntity> {

    List<MasterLabEntity> findAllMatching(MedicalDepartmentEnum medicalDepartment);

    List<MasterLabEntity> findAll();

    void deleteAll();

    long deleteMatching(HealthCareServiceEnum healthCareService);

    List<MasterLabEntity> findAllMatching(HealthCareServiceEnum healthCareService);

    MasterLabEntity findOne(String productName, HealthCareServiceEnum healthCareService);
}
