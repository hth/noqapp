package com.noqapp.medical.repository;

import com.noqapp.medical.domain.RadiologyEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 4/7/18 10:20 PM
 */
public interface RadiologyManager extends RepositoryManager<RadiologyEntity> {

    List<RadiologyEntity> findAll();

    long totalNumberOfRecords();

    boolean existsName(String name);
}
