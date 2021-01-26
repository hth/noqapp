package com.noqapp.service;

import com.noqapp.domain.PublishJobEntity;
import com.noqapp.repository.PublishJobManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 12/27/20 9:39 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class PublishJobService {
    private static final Logger LOG = LoggerFactory.getLogger(PublishJobService.class);

    private PublishJobManager publishJobManager;

    @Autowired
    public PublishJobService(PublishJobManager publishJobManager) {
        this.publishJobManager = publishJobManager;
    }

    public PublishJobEntity findOne(String publishJobId) {
        return publishJobManager.findOne(publishJobId);
    }

    public List<PublishJobEntity> findAll(String bizNameId) {
        return publishJobManager.findAll(bizNameId);
    }

    public void save(PublishJobEntity publishJob) {
        publishJobManager.save(publishJob);
    }

    public void takeOffOrOnline(String id, boolean active) {
        publishJobManager.takeOffOrOnline(id, active);
    }
}
