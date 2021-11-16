package com.noqapp.repository;

import com.noqapp.domain.MailEntity;
import com.noqapp.domain.types.MailStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 11/27/16 12:44 AM
 */
public interface MailManager extends RepositoryManager<MailEntity> {

    List<MailEntity> pendingMails();

    void updateMail(String id, MailStatusEnum mailStatus);

    /** Check if mail with specific message exists in queue to be sent. */
    boolean existsMailWithMessage(String message);
}

