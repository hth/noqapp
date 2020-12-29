package com.noqapp.repository;

import com.noqapp.domain.PublishJobEntity;

import java.util.List;

/**
 * hitender
 * 12/27/20 4:56 PM
 */
public interface PublishJobManager extends RepositoryManager<PublishJobEntity> {
    PublishJobEntity findOne(String id);

    List<PublishJobEntity> findAll(String bizNameId);

    void takeOffOrOnline(String id, boolean active);
}
