package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.PublishArticleService;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import com.noqapp.view.form.PublishArticleForm;

import org.apache.commons.lang3.StringUtils;

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
    private PublishArticleService publishArticleService;
    private BusinessUserStoreService businessUserStoreService;
    private BizService bizService;

    @Autowired
    public PublishArticleFlowActions(
        BusinessUserService businessUserService,
        PublishArticleService publishArticleService,
        BusinessUserStoreService businessUserStoreService,
        BizService bizService
    ) {
        this.businessUserService = businessUserService;
        this.publishArticleService = publishArticleService;
        this.businessUserStoreService = businessUserStoreService;
        this.bizService = bizService;
    }

    /**
     * Initialize publish article form.
     * @return
     */
    @SuppressWarnings("unused")
    public PublishArticleForm initiatePublishArticle(String publishId) {
        if (StringUtils.isBlank(publishId)) {
            return PublishArticleForm.newInstance();
        } else {
            PublishArticleEntity publishArticle = publishArticleService.findOne(publishId);
            return PublishArticleForm.newInstance()
                .setTitle(publishArticle.getTitle())
                .setDescription(publishArticle.getDescription())
                .setPublishId(new ScrubbedInput(publishId))
                .setBannerImage(publishArticle.getBannerImage());
        }
    }

    @SuppressWarnings("unused")
    public String confirm(PublishArticleForm publishArticleForm) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        if (publishArticleForm.getPublishId() == null) {
            String businessCategoryId = null;
            if (BusinessTypeEnum.DO == businessUser.getBizName().getBusinessType()) {
                BusinessUserStoreEntity businessUserStore = businessUserStoreService.findUserManagingStoreWithUserLevel(queueUser.getQueueUserId(), businessUser.getUserLevel());
                BizStoreEntity bizStore = bizService.getByStoreId(businessUserStore.getBizStoreId());
                businessCategoryId = bizStore.getBizCategoryId();
            }

            PublishArticleEntity publishArticle = new PublishArticleEntity()
                .setQueueUserId(queueUser.getQueueUserId())
                .setTitle(publishArticleForm.getTitle())
                .setBusinessType(businessUser.getBizName().getBusinessType())
                .setBizCategoryId(businessCategoryId)
                .setValidateStatus(publishArticleForm.getValidateStatus())
                .setDescription(publishArticleForm.getDescription());

            publishArticleService.save(publishArticle);
        } else {
            PublishArticleEntity publishArticle = publishArticleService.findOne(publishArticleForm.getPublishId().getText());
            publishArticle
                .setTitle(publishArticleForm.getTitle())
                .setValidateStatus(StringUtils.isBlank(publishArticle.getBannerImage()) ? ValidateStatusEnum.I : ValidateStatusEnum.P)
                .setDescription(publishArticleForm.getDescription());

            publishArticleService.save(publishArticle);
        }
        return "success";
    }
}
