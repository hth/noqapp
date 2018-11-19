package com.noqapp.medical.repository;

import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterRadiologyEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 11/16/18 3:17 PM
 */
public interface MasterRadiologyManager extends RepositoryManager<MasterRadiologyEntity> {

    List<MasterRadiologyEntity> findAllMatching(MedicalDepartmentEnum medicalDepartment);
}
