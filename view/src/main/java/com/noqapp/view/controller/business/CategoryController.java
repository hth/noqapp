package com.noqapp.view.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.helper.QueueDetail;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.InvocationByEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.view.form.business.BusinessLandingForm;
import com.noqapp.view.form.business.CategoryLandingForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 12/20/17 4:32 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/category")
public class CategoryController {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    private String nextPage;
    private String storeByCategoryPage;

    private BizService bizService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;

    @Autowired
    public CategoryController(
        @Value("${nextPage:/business/category}")
        String nextPage,

        @Value ("${storeByCategoryPage:/business/storeByCategory}")
        String storeByCategoryPage,

        BizService bizService,
        BusinessUserService businessUserService,
        BusinessUserStoreService businessUserStoreService
    ) {
        this.nextPage = nextPage;
        this.storeByCategoryPage = storeByCategoryPage;

        this.bizService = bizService;
        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
    }

    /**
     * Loading landing page for business category.
     */
    @GetMapping
    public String landing(
        @ModelAttribute("categoryLanding")
        CategoryLandingForm categoryLanding,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on business category page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        String bizNameId = businessUser.getBizName().getId();
        Map<String, String> categories = CommonHelper.getCategories(businessUser.getBizName().getBusinessType(), InvocationByEnum.BUSINESS);
        categoryLanding
            .setBizNameId(new ScrubbedInput(bizNameId))
            .setCategories(null == categories ? new HashMap<>() : categories)
            .setCategoryCounts(null == categories ? new HashMap<>() : bizService.countCategoryUse(categories.keySet(), bizNameId));
        return nextPage;
    }

    /**
     * List stores belonging to selected category name.
     */
    @GetMapping(value = "/{bizCategoryId}/storeByCategory")
    public String storeByCategory(
        @PathVariable("bizCategoryId")
        ScrubbedInput bizCategoryId,

        @ModelAttribute ("businessLandingForm")
        BusinessLandingForm businessLandingForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on editing category business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizNameEntity bizName = businessUser.getBizName();
        businessLandingForm
            .setBizName(bizName.getBusinessName())
            .setBusinessType(bizName.getBusinessType())
            .setCategories(CommonHelper.getCategories(bizName.getBusinessType(), InvocationByEnum.BUSINESS));
        List<BizStoreEntity> bizStores = bizService.getBizStoresByCategory(bizCategoryId.getText(), businessUser.getBizName().getId());
        businessLandingForm.setBizStores(bizStores);
        for (BizStoreEntity bizStore : bizStores) {
            QueueDetail queueDetail = new QueueDetail()
                .setId(bizStore.getId())
                .setAssignedToQueue(businessUserStoreService.findNumberOfPeopleAssignedToQueue(bizStore.getId()))
                .setPendingApprovalToQueue(businessUserStoreService.findNumberOfPeoplePendingApprovalToQueue(bizStore.getId()));

            businessLandingForm.addQueueDetail(queueDetail);
        }

        return storeByCategoryPage;
    }
}
