package com.noqapp.repository;

import com.noqapp.domain.ExternalAccessEntity;

import java.util.List;

/**
 * hitender
 * 2/4/18 11:05 AM
 */
public interface ExternalAccessManager extends RepositoryManager<ExternalAccessEntity> {

    ExternalAccessEntity findById(String id);

    List<ExternalAccessEntity> findAll(String bizId);

    List<ExternalAccessEntity> findByQid(String qid);
}
