package com.noqapp.service;

import com.noqapp.domain.types.PointActivityEnum;
import com.noqapp.repository.PointEarnedManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 11/23/21 11:11 AM
 */
@Service
public class PointEarnedService {
    private static final Logger LOG = LoggerFactory.getLogger(PointEarnedService.class);

    private PointEarnedManager pointEarnedManager;

    @Autowired
    public PointEarnedService(PointEarnedManager pointEarnedManager) {
        this.pointEarnedManager = pointEarnedManager;
    }

    public long totalReviewPoints(String qid) {
        return pointEarnedManager.countReviewPoints(qid) * PointActivityEnum.REV.getPoint();
    }


    public long totalInvitePoints(String qid) {
     return pointEarnedManager.countInvitePoints(qid) * PointActivityEnum.INV.getPoint()
         + pointEarnedManager.countInviteePoints(qid) * PointActivityEnum.ISU.getPoint();
    }
}
