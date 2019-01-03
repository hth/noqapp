package com.noqapp.view.flow.merchant;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import com.noqapp.view.form.PublishArticleForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Publish Article.
 * hitender
 * 2018-12-30 00:12
 */
@Component
public class PublishArticleFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(PublishArticleFlowActions.class);

    private BusinessUserService businessUserService;

    @Autowired
    public PublishArticleFlowActions(BusinessUserService businessUserService) {
        this.businessUserService = businessUserService;
    }

    /**
     * Initialize publish article form.
     * @return
     */
    @SuppressWarnings("unused")
    public PublishArticleForm initiatePublishArticle() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        return PublishArticleForm.newInstance();
    }

    @SuppressWarnings("unused")
    public String confirm(PublishArticleForm publishArticleForm) {
        LOG.info("{}", publishArticleForm.getFile().getContentType());
        return "success";
    }
}
