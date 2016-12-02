package com.token.repository;

import com.token.domain.MailEntity;
import com.token.domain.types.MailStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 11/27/16 12:44 AM
 */
public interface MailManager extends RepositoryManager<MailEntity> {

    List<MailEntity> pendingMails();

    void updateMail(String id, MailStatusEnum mailStatus);
}

