package com.noqapp.medical.repository;

import com.noqapp.medical.domain.PhysicalEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 4/5/18 12:48 PM
 */
public interface PhysicalManager extends RepositoryManager<PhysicalEntity> {

    List<PhysicalEntity> findAll();

    long totalNumberOfRecords();

    boolean existsName(String name);
}
