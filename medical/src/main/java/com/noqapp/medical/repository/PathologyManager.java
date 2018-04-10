package com.noqapp.medical.repository;

import com.noqapp.medical.domain.PathologyEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 4/7/18 10:19 PM
 */
public interface PathologyManager  extends RepositoryManager<PathologyEntity> {

    List<PathologyEntity> findAll();

    long totalNumberOfRecords();

    boolean existsName(String name);
}
