package com.noqapp.repository;

import com.noqapp.domain.CustomTextToSpeechEntity;

/**
 * User: hitender
 * Date: 12/13/19 8:01 AM
 */
public interface CustomTextToSpeechManager extends RepositoryManager<CustomTextToSpeechEntity> {

    CustomTextToSpeechEntity findOne(String bizNameId);
}
