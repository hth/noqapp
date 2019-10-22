package com.noqapp.view.form.business;

import com.noqapp.domain.QuestionnaireEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 10/23/19 12:37 AM
 */
public class QuestionnaireForm {

    private List<QuestionnaireEntity> questionnaires;

    public List<QuestionnaireEntity> getQuestionnaires() {
        return questionnaires;
    }

    public QuestionnaireForm setQuestionnaires(List<QuestionnaireEntity> questionnaires) {
        this.questionnaires = questionnaires;
        return this;
    }
}
