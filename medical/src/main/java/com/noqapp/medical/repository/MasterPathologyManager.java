package com.noqapp.medical.repository;

import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MasterPathologyEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 11/16/18 12:30 PM
 */
public interface MasterPathologyManager extends RepositoryManager<MasterPathologyEntity> {

    List<MasterPathologyEntity> findAllMatching(MedicalDepartmentEnum medicalDepartment);
}
