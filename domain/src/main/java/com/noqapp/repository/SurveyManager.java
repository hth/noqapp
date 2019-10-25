package com.noqapp.repository;

import com.noqapp.domain.SurveyEntity;
import com.noqapp.domain.types.SentimentTypeEnum;

/**
 * User: hitender
 * Date: 10/20/19 6:38 AM
 */
public interface SurveyManager extends RepositoryManager<SurveyEntity> {

    SurveyEntity getRecentOverallRating(String bizNameId);

    void updateSentiment(String id, SentimentTypeEnum sentimentType);
}