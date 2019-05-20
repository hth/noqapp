package com.noqapp.service;

import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.PublishArticleManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 2019-01-02 20:53
 */
@Service
public class PublishArticleService {

    private PublishArticleManager publishArticleManager;

    @Autowired
    public PublishArticleService(PublishArticleManager publishArticleManager) {
        this.publishArticleManager = publishArticleManager;
    }

    public void save(PublishArticleEntity publishArticle) {
        publishArticleManager.save(publishArticle);
    }

    public List<PublishArticleEntity> findAll(String qid) {
        return publishArticleManager.findAll(qid);
    }

    public PublishArticleEntity findOne(String id) {
        return publishArticleManager.findOne(id);
    }

    public boolean exists(String id, String qid) {
        return publishArticleManager.exists(id, qid);
    }

    public void takeOffOrOnline(String id, boolean active) {
        publishArticleManager.takeOffOrOnline(id, active);
    }

    public List<PublishArticleEntity> findPendingApprovals() {
        return publishArticleManager.findPendingApprovals();
    }

    public PublishArticleEntity findOnePendingReview(String id) {
        return publishArticleManager.findOnePendingReview(id);
    }

    @Mobile
    public List<PublishArticleEntity> getLatestArticles() {
        return publishArticleManager.getLatestArticles();
    }

    public long findPendingApprovalCount() {
        return publishArticleManager.findPendingApprovalCount();
    }
}
