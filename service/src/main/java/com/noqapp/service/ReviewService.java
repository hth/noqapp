package com.noqapp.service;

import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonReview;
import com.noqapp.domain.json.JsonReviewList;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.UserProfileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 10/11/18 11:02 PM
 */
@Service
public class ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

    private int reviewLimitedToDays;

    private QueueManager queueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    private UserProfileManager userProfileManager;

    @Autowired
    public ReviewService(
        @Value("${reviewLimitedToDays:180}")
        int reviewLimitedToDays,

        QueueManager queueManager,
        QueueManagerJDBC queueManagerJDBC,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC,
        UserProfileManager userProfileManager
    ) {
        this.reviewLimitedToDays = reviewLimitedToDays;

        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
        this.userProfileManager = userProfileManager;
    }

    @Mobile
    public JsonReviewList findQueueReviews(String codeQR) {
        List<QueueEntity> queues = queueManager.findReviews(codeQR);
        try {
            List<QueueEntity> jdbcQueues = queueManagerJDBC.findReviews(codeQR, reviewLimitedToDays);
            if (null != jdbcQueues) {
                queues.addAll(jdbcQueues);
            } else {
                LOG.info("JDBC is null and queues={}", queues.size());
            }
        } catch (Exception e) {
            LOG.error("Failed getting historical reason={}", e.getLocalizedMessage(), e);
        }

        JsonReviewList jsonReviewList = new JsonReviewList();
        for (QueueEntity queue : queues) {
            populatedReviews(jsonReviewList, queue.getRatingCount(), queue.getReview(), queue.getQueueUserId());
        }

        return jsonReviewList;
    }

    @Mobile
    public JsonReviewList findQueueLevelUpReviews(String bizNameId) {
        List<QueueEntity> queues = queueManager.findLevelUpReviews(bizNameId);
        try {
            List<QueueEntity> jdbcQueues = queueManagerJDBC.findLevelUpReviews(bizNameId, reviewLimitedToDays);
            if (null != jdbcQueues) {
                queues.addAll(jdbcQueues);
            } else {
                LOG.info("JDBC is null and levelUp queues={}", queues.size());
            }
        } catch (Exception e) {
            LOG.error("Failed getting historical reason={}", e.getLocalizedMessage(), e);
        }

        JsonReviewList jsonReviewList = new JsonReviewList();
        for (QueueEntity queue : queues) {
            populatedReviews(jsonReviewList, queue.getRatingCount(), queue.getReview(), queue.getQueueUserId());
        }

        return jsonReviewList;
    }


    @Mobile
    public JsonReviewList findOrderReviews(String codeQR) {
        List<PurchaseOrderEntity> purchaseOrders = purchaseOrderManager.findReviews(codeQR);
        try {
            List<PurchaseOrderEntity> jdbcPurchaseOrders = purchaseOrderManagerJDBC.findReviews(codeQR, reviewLimitedToDays);
            if (null != jdbcPurchaseOrders) {
                purchaseOrders.addAll(jdbcPurchaseOrders);
            } else {
                LOG.info("JDBC is null and purchaseOrders={}", purchaseOrders.size());
            }
        } catch (Exception e) {
            LOG.error("Failed getting historical reason={}", e.getLocalizedMessage(), e);
        }

        JsonReviewList jsonReviewList = new JsonReviewList();
        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            populatedReviews(jsonReviewList, purchaseOrder.getRatingCount(), purchaseOrder.getReview(), purchaseOrder.getQueueUserId());
        }

        return jsonReviewList;
    }

    private void populatedReviews(JsonReviewList jsonReviewList, int ratingCount, String review, String qid) {
        UserProfileEntity userProfile = null;
        if (null != qid) {
            userProfile = userProfileManager.findByQueueUserId(qid);
        }

        jsonReviewList.addJsonReview(
            new JsonReview(
                ratingCount,
                review,
                userProfile == null ? "" : userProfile.getProfileImage(),
                userProfile == null ? "" : userProfile.getName()))
            .addRatingCount(ratingCount);
    }
}
