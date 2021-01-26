package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.PublishJobEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.PublishJobService;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import com.noqapp.view.form.PublishJobForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * hitender
 * 12/28/20 9:53 AM
 */
@Component
public class PublishJobFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(PublishJobFlowActions.class);

    private PublishJobService publishJobService;
    private BusinessUserService businessUserService;

    @Autowired
    public PublishJobFlowActions(
        PublishJobService publishJobService,
        BusinessUserService businessUserService
    ) {
        this.publishJobService = publishJobService;
        this.businessUserService = businessUserService;
    }

    /**
     * Initialize publish job form.
     * @return
     */
    @SuppressWarnings("unused")
    public PublishJobForm initiatePublishJob(String publishId) {
        if (StringUtils.isBlank(publishId)) {
            return PublishJobForm.newInstance();
        } else {
            PublishJobEntity jobPost = publishJobService.findOne(publishId);
            return PublishJobForm.newInstance()
                .setTitle(jobPost.getTitle())
                .setDescription(jobPost.getDescription())
                .setPublishId(new ScrubbedInput(publishId));
        }
    }

    @SuppressWarnings("unused")
    public String confirm(PublishJobForm publishJobForm) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        PublishJobEntity publishJob;
        if (publishJobForm.getPublishId() == null) {
            publishJob = new PublishJobEntity()
                .setQueueUserId(queueUser.getQueueUserId())
                .setTitle(publishJobForm.getTitle())
                .setBusinessType(businessUser.getBizName().getBusinessType())
                .setBizNameId(businessUser.getBizName().getId())
                .setValidateStatus(publishJobForm.getValidateStatus())
                .setDescription(publishJobForm.getDescription())
                .setPublishDate(publishJobForm.getPublishDate());
        } else {
            publishJob = publishJobService.findOne(publishJobForm.getPublishId().getText());
            publishJob
                .setTitle(publishJobForm.getTitle())
                //TODO(hth) Mark as not validated in future to validating text
                .setValidateStatus(ValidateStatusEnum.A)
                .setDescription(publishJobForm.getDescription());
        }
        publishJobService.save(publishJob);
        return "success";
    }
}
