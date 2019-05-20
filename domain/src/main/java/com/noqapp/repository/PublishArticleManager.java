package com.noqapp.repository;

import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.types.ValidateStatusEnum;

import java.util.List;

/**
 * hitender
 * 2019-01-02 18:11
 */
public interface PublishArticleManager extends RepositoryManager<PublishArticleEntity> {

    List<PublishArticleEntity> findAll(String qid);

    PublishArticleEntity findOne(String id);

    boolean exists(String id, String qid);

    void takeOffOrOnline(String id, boolean active);

    PublishArticleEntity changeStatus(String id, ValidateStatusEnum validateStatus);

    List<PublishArticleEntity> findPendingApprovals();

    PublishArticleEntity findOnePendingReview(String id);

    List<PublishArticleEntity> getLatestArticles();

    long findPendingApprovalCount();
}
