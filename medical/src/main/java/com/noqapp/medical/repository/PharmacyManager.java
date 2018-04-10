package com.noqapp.medical.repository;

import com.noqapp.medical.domain.PharmacyEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 4/7/18 7:04 PM
 */
public interface PharmacyManager extends RepositoryManager<PharmacyEntity> {

    List<PharmacyEntity> findAll();

    long totalNumberOfRecords();

    boolean existsName(String name);
}
