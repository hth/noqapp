package com.noqapp.view.flow.merchant;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.AuthorizedQueueUser;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.view.flow.merchant.exception.AuthorizedQueueUserDetailException;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import com.noqapp.view.flow.utils.WebFlowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * hitender
 * 1/18/18 9:52 AM
 */
@Component
public class AuthorizedQueueUserDetailFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizedQueueUserDetailFlowActions.class);

    private int queueLimit;

    private WebFlowUtils webFlowUtils;
    private BizService bizService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private AccountService accountService;

    @Autowired
    public AuthorizedQueueUserDetailFlowActions(
            @Value("${BusinessUserStoreService.queue.limit}")
            int queueLimit,

            WebFlowUtils webFlowUtils,
            BizService bizService,
            BusinessUserService businessUserService,
            BusinessUserStoreService businessUserStoreService,
            AccountService accountService
    ) {
        this.queueLimit = queueLimit;

        this.webFlowUtils = webFlowUtils;
        this.bizService = bizService;
        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.accountService = accountService;
    }

    @SuppressWarnings("all")
    public AuthorizedQueueUser loadQueueUserDetail(ExternalContext externalContext) {
        LOG.info("loadQueueUserDetail Start");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        String businessUserId = (String) webFlowUtils.getFlashAttribute(externalContext, "businessUserId");
        businessUser = businessUserService.findById(businessUserId);
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(businessUser.getQueueUserId());

        AuthorizedQueueUser authorizedQueueUser = new AuthorizedQueueUser();

        List<BizStoreEntity> enrolledInStores = new LinkedList<>();
        List<BizStoreEntity> bizStores = bizService.getAllBizStores(businessUser.getBizName().getId());
        List<BusinessUserStoreEntity> businessUserStores;
        if (null != businessUser) {
            businessUserStores = businessUserStoreService.findAllStoreQueueAssociated(businessUser.getQueueUserId());

            for (BizStoreEntity bizStore : bizStores) {
                BusinessUserStoreEntity businessUserStore = new BusinessUserStoreEntity(
                        businessUser.getQueueUserId(),
                        bizStore.getId(),
                        bizStore.getBizName().getId(),
                        bizStore.getCodeQR(),
                        userProfile.getLevel());

                if (businessUserStores.contains(businessUserStore)) {
                    enrolledInStores.add(bizStore);
                }
            }
        }
        bizStores.removeAll(enrolledInStores);
        authorizedQueueUser
                .setQueueLimit(queueLimit)
                .setQid(userProfile.getQueueUserId())
                .setName(userProfile.getName())
                .setEnrolledInStores(enrolledInStores)
                .setBizStores(bizStores)
                .setCategories(bizService.getBusinessCategoriesForDropDown(businessUser.getBizName().getId()));

        return authorizedQueueUser;
    }

    @SuppressWarnings("all")
    public void completeQueueUserDetail(AuthorizedQueueUser authorizedQueueUser) {
        for (String bizStoreId : authorizedQueueUser.getInterests()) {
            try {
                Optional<BizStoreEntity> matchingObject = authorizedQueueUser.getBizStores().stream().
                        filter(p -> p.getId().equals(bizStoreId)).
                        findAny(); //Using findFirst() as findAny() runs in parallel & not sequential. Dataset is unique

                BizStoreEntity bizStore = matchingObject.isPresent() ? matchingObject.get() : null;
                UserProfileEntity userProfile = accountService.findProfileByQueueUserId(authorizedQueueUser.getQid());
                BusinessUserStoreEntity businessUserStoreEntity = new BusinessUserStoreEntity(
                        authorizedQueueUser.getQid(),
                        bizStoreId,
                        bizStore.getBizName().getId(),
                        bizStore.getCodeQR(),
                        userProfile.getLevel()
                );

                businessUserStoreService.save(businessUserStoreEntity);
            } catch (RuntimeException e) {
                LOG.error("Failed to authorize user to business profile qid={} bizStoreId={} reason={}",
                        authorizedQueueUser.getQid(),
                        bizStoreId,
                        e.getLocalizedMessage(),
                        e);
                throw new AuthorizedQueueUserDetailException("Failed to authorize user to business");
            }
        }
    }
}
