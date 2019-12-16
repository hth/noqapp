package com.noqapp.service;

import com.noqapp.domain.CustomTextToSpeechEntity;
import com.noqapp.repository.CustomTextToSpeechManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 12/13/19 8:09 AM
 */
@Service
public class CustomTextToSpeechService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomTextToSpeechService.class);

    private CustomTextToSpeechManager customTextToSpeechManager;

    @Autowired
    public CustomTextToSpeechService(CustomTextToSpeechManager customTextToSpeechManager) {
        this.customTextToSpeechManager = customTextToSpeechManager;
    }

    public CustomTextToSpeechEntity findByBizNameId(String bizNameId) {
        return customTextToSpeechManager.findOne(bizNameId);
    }

    public void save(CustomTextToSpeechEntity customTextToSpeech) {
        customTextToSpeechManager.save(customTextToSpeech);
    }
}
