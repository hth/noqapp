package com.noqapp.repository;

import com.noqapp.domain.S3FileEntity;

import java.util.List;

/**
 * hitender
 * 5/29/18 5:39 PM
 */
public interface S3FileManager extends RepositoryManager<S3FileEntity> {

    List<S3FileEntity> findAllWithLimit();
}
