package com.noqapp.repository;

import com.noqapp.domain.QuestionnaireEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 10/20/19 2:34 AM
 */
public interface QuestionnaireManager extends RepositoryManager<QuestionnaireEntity> {

    List<QuestionnaireEntity> findAll(String bizNameId);

    QuestionnaireEntity findLatest(String bizNameId);

    QuestionnaireEntity findById(String questionnaireId);

    boolean isEditable(String questionnaireId);
}
