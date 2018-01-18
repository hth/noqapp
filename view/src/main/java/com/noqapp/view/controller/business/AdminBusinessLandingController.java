package com.noqapp.view.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.analytic.BizDimensionEntity;
import com.noqapp.domain.helper.QueueDetail;
import com.noqapp.domain.helper.QueueSupervisor;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.analytic.BizDimensionService;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.view.form.QueueSupervisorActionForm;
import com.noqapp.view.form.business.BusinessLandingForm;
import com.noqapp.view.form.business.QueueSupervisorForm;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 12/7/16 11:40 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business")
public class AdminBusinessLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminBusinessLandingController.class);

    private int queueLimit;
    private String nextPage;
    private String migrateBusinessRegistrationFlow;
    private String storeActionFlow;
    private String addQueueSupervisorFlow;
    private String listQueueSupervisorPage;
    private String authorizedUsersPage;

    private BusinessUserService businessUserService;
    private BizDimensionService bizDimensionService;
    private BizService bizService;
    private BusinessUserStoreService businessUserStoreService;
    private AccountService accountService;

    @Autowired
    public AdminBusinessLandingController(
            @Value ("${BusinessUserStoreService.queue.limit}")
            int queueLimit,

            @Value ("${nextPage:/business/landing}")
            String nextPage,

            @Value ("${migrateBusinessRegistrationFlow:redirect:/migrate/business/registration.htm}")
            String migrateBusinessRegistrationFlow,

            @Value ("${storeActionFlow:redirect:/store/storeAction.htm}")
            String storeActionFlow,

            @Value ("${addQueueSupervisorFlow:redirect:/store/addQueueSupervisor.htm}")
            String addQueueSupervisorFlow,

            @Value("${listQueueSupervisorPage:/business/listQueueSupervisor}")
            String listQueueSupervisorPage,

            @Value("${authorizedUsersPage:/business/authorizedUsers}")
            String authorizedUsersPage,

            BusinessUserService businessUserService,
            BizDimensionService bizDimensionService,
            BizService bizService,
            BusinessUserStoreService businessUserStoreService,
            AccountService accountService
    ) {
        this.queueLimit = queueLimit;
        this.nextPage = nextPage;
        this.businessUserService = businessUserService;
        this.storeActionFlow = storeActionFlow;
        this.addQueueSupervisorFlow = addQueueSupervisorFlow;
        this.listQueueSupervisorPage = listQueueSupervisorPage;
        this.authorizedUsersPage = authorizedUsersPage;

        this.migrateBusinessRegistrationFlow = migrateBusinessRegistrationFlow;
        this.bizDimensionService = bizDimensionService;
        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
        this.accountService = accountService;
    }

    /**
     * Loading landing page for business.
     * This link is mapped in web flow after merchant adds new store to existing business.
     *
     * @param businessLandingForm
     * @return
     */
    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
            @ModelAttribute ("businessLandingForm")
            BusinessLandingForm businessLandingForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        return nextPage(businessUserService.findBusinessUser(queueUser.getQueueUserId()), businessLandingForm);
    }

    @SuppressWarnings("Duplicates")
    private String nextPage(
            BusinessUserEntity businessUser,
            BusinessLandingForm businessLandingForm
    ) {
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case V:
                populateLandingForm(businessLandingForm, businessUser);
                return nextPage;
            case N:
                businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.I);
                businessUserService.save(businessUser);
                /* After setting status as incomplete, continue to call migrateBusinessRegistrationFlow. */
                LOG.info("Migrate to business registration qid={} level={}", businessUser.getQueueUserId(), businessUser.getUserLevel());
                return migrateBusinessRegistrationFlow;
            case C:
            case I:
                LOG.info("Migrate to business registration qid={} level={}", businessUser.getQueueUserId(), businessUser.getUserLevel());
                return migrateBusinessRegistrationFlow;
            default:
                LOG.error("Reached unsupported condition={}", businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    private void populateLandingForm(BusinessLandingForm businessLandingForm, BusinessUserEntity businessUser) {
        Assert.notNull(businessUser, "Business user should not be null");
        BizNameEntity bizName = businessUser.getBizName();
        String bizNameId = bizName.getId();
        LOG.info("Loading dashboard for bizName={} bizId={}", bizName.getBusinessName(), bizName.getId());

        BizDimensionEntity bizDimension = bizDimensionService.findBy(bizNameId);
        if (null != bizDimension) {
            businessLandingForm.setBizName(bizDimension.getBizName());
        } else {
            businessLandingForm.setBizName(bizName.getBusinessName());
        }

        businessLandingForm.setBizCodeQR(bizName.getCodeQR());
        businessLandingForm.setCategories(bizService.getBusinessCategoriesAsMap(businessUser.getBizName().getId()));
        List<BizStoreEntity> bizStores = bizService.getAllBizStores(businessUser.getBizName().getId());
        businessLandingForm.setBizStores(bizStores);
        for (BizStoreEntity bizStore : bizStores) {
            QueueDetail queueDetail = new QueueDetail()
                    .setId(bizStore.getId())
                    .setAssignedToQueue(businessUserStoreService.findNumberOfPeopleAssignedToQueue(bizStore.getId()))
                    .setPendingApprovalToQueue(businessUserStoreService.findNumberOfPeoplePendingApprovalToQueue(bizStore.getId()));

            businessLandingForm.addQueueDetail(queueDetail);
        }
    }

    /**
     * List queue supervisors for a store.
     *
     * @param queueSupervisorForm
     * @param queueSupervisorActionForm Not populated but used
     * @param storeId
     * @return
     */
    @GetMapping (value = "/{storeId}/listQueueSupervisor", produces = "text/html;charset=UTF-8")
    public String listQueueSupervisor(
            @ModelAttribute ("queueSupervisorForm")
            QueueSupervisorForm queueSupervisorForm,

            @ModelAttribute ("queueSupervisorActionForm")
            QueueSupervisorActionForm queueSupervisorActionForm,

            @PathVariable ("storeId")
            ScrubbedInput storeId,

            Model model
    ) {
        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        queueSupervisorForm.setBizStoreId(bizStore.getId());
        queueSupervisorForm.setQueueName(bizStore.getDisplayName());

        List<QueueSupervisor> queueSupervisors = businessUserStoreService.getAllManagingStore(storeId.getText());
        queueSupervisorForm.setQueueSupervisors(queueSupervisors);

        List<QueueSupervisor> availableQueueSupervisor = businessUserStoreService.getAllNonAdminForBusiness(
                bizStore.getBizName().getId(),
                bizStore.getId()
        );
        /* Filter already existing Q_SUPERVISOR for this queue. */
        availableQueueSupervisor.removeAll(queueSupervisors);
        queueSupervisorForm.setAvailableQueueSupervisor(availableQueueSupervisor);

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.errorMessage", model.asMap().get("result"));
        }

        return listQueueSupervisorPage;
    }

    @GetMapping (value = "/addStore", produces = "text/html;charset=UTF-8")
    public String addStore() {
        LOG.info("Add store to business {}", storeActionFlow);
        return storeActionFlow;
    }

    @GetMapping (value = "/{bizStoreId}/editStore", produces = "text/html;charset=UTF-8")
    public String editStore(
            @PathVariable ("bizStoreId")
            ScrubbedInput bizStoreId,

            RedirectAttributes redirectAttrs
    ) {
        LOG.info("Edit business store {}", bizStoreId);
        redirectAttrs.addFlashAttribute("bizStoreId", bizStoreId);
        return storeActionFlow;
    }

    @GetMapping (value = "/{bizStoreId}/addQueueSupervisor", produces = "text/html;charset=UTF-8")
    public String addQueueSupervisorFlow(
            @PathVariable ("bizStoreId")
            ScrubbedInput bizStoreId,

            RedirectAttributes redirectAttributes
    ) {
        LOG.info("Add queue manager to bizStoreId={} {}", bizStoreId.getText(), addQueueSupervisorFlow);
        redirectAttributes.addFlashAttribute("bizStoreId", bizStoreId.getText());
        return addQueueSupervisorFlow;
    }

    /**
     * Approve or reject new supervisor.
     *
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/actionQueueSupervisor")
    public String actionQueueSupervisor(
            @ModelAttribute ("queueSupervisorActionForm")
            QueueSupervisorActionForm queueSupervisorActionForm,

            BindingResult result,
            RedirectAttributes redirectAttrs,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            BusinessUserEntity businessUser = businessUserService.findById(queueSupervisorActionForm.getBusinessUserId().getText());
            if (null == businessUser) {
                LOG.warn("Could not find businessUser={}", queueSupervisorActionForm.getBusinessUserId().getText());
                response.sendError(SC_NOT_FOUND, "Could not find");
                return null;
            }

            UserProfileEntity userProfile;
            String qid;
            switch (queueSupervisorActionForm.getAction().getText()) {
                case "APPROVE":
                    businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.V);
                    businessUser.active();

                    businessUser.setValidateByQid(queueUser.getQueueUserId());
                    businessUserService.save(businessUser);
                    businessUserStoreService.activateAccount(businessUser.getQueueUserId(), businessUser.getBizName().getId());
                    break;
                case "ADD":
                    int queueSupervisingCount = businessUserStoreService.findAllStoreQueueAssociated(businessUser.getQueueUserId()).size();
                    userProfile = accountService.findProfileByQueueUserId(businessUser.getQueueUserId());
                    if (queueSupervisingCount > queueLimit) {
                        LOG.warn("Failed validation since queue supervising has reached {} limit", queueLimit);

                        result.addError(
                                new ObjectError(
                                        "errorMessage",
                                        userProfile.getName()
                                                + " already manages max limit of "
                                                + queueLimit
                                                + " queues. Please un-subscribe user from other queues.")
                        );

                        redirectAttrs.addFlashAttribute("result", result);
                        //Re-direct to prevent resubmit
                        return "redirect:/business/" + queueSupervisorActionForm.getBizStoreId().getText() + "/listQueueSupervisor.htm";
                    }

                    BizStoreEntity bizStore = bizService.getByStoreId(queueSupervisorActionForm.getBizStoreId().getText());
                    businessUserStoreService.addToBusinessUserStore(
                            businessUser.getQueueUserId(),
                            bizStore,
                            businessUser.getBusinessUserRegistrationStatus());
                    break;
                case "REJECT":
                case "DELETE":
                    qid = businessUser.getQueueUserId();

                    businessUserStoreService.removeFromBusiness(qid, businessUser.getBizName().getId());
                    businessUserService.deleteHard(businessUser);

                    /*
                     * Downgrade ROLES for QID as it was set to Q_SUPERVISOR when approving.
                     * But after approved and when rejecting, not sure about role, hence downgrading all to CLIENT.
                     */
                    userProfile = accountService.findProfileByQueueUserId(qid);
                    switch (userProfile.getLevel()) {
                        case Q_SUPERVISOR:
                        case S_MANAGER:
                        case M_ADMIN:
                            userProfile.setLevel(UserLevelEnum.CLIENT);
                            break;
                        default:
                            LOG.error("Reached unsupported condition as userLevel={}", userProfile.getLevel());
                            throw new UnsupportedOperationException("Reached unsupported condition");
                    }
                    accountService.save(userProfile);

                    UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                            userProfile.getQueueUserId(),
                            userProfile.getLevel());
                    accountService.save(userAccount);

                    break;
                case "REMOVE":
                    /* This removes from administrating a Queue. Does not remove from business. */
                    businessUserStoreService.removeFromStore(
                            businessUser.getQueueUserId(),
                            queueSupervisorActionForm.getBizStoreId().getText());
                    break;
                default:
                    LOG.warn("Reached un-reachable condition {}", queueSupervisorActionForm.getAction());
                    throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
            }

            String goToPage;
            switch (queueSupervisorActionForm.getAction().getText()) {
                case "DELETE":
                    goToPage = "redirect:/business/authorizedUsers.htm";
                    break;
                default:
                    goToPage = "redirect:/business/" + queueSupervisorActionForm.getBizStoreId().getText() + "/listQueueSupervisor.htm";
            }
            return goToPage;
        } catch (Exception e) {
            LOG.error("Failed updated status for id={} status={} reason={}",
                    queueSupervisorActionForm.getBusinessUserId().getText(),
                    queueSupervisorActionForm.getAction().getText(),
                    e.getLocalizedMessage(),
                    e);

            String goToPage;
            switch (queueSupervisorActionForm.getAction().getText()) {
                case "DELETE":
                    goToPage = "redirect:/business/authorizedUsers.htm";
                    break;
                default:
                    goToPage = "redirect:/business/" + queueSupervisorActionForm.getBizStoreId().getText() + "/listQueueSupervisor.htm";
            }
            return goToPage;
        }
    }

    /**
     * List all users with role of Queue Supervisor and Manager managing queues for business.
     *
     * @param queueSupervisorForm
     * @return
     */
    @GetMapping (value = "/authorizedUsers", produces = "text/html;charset=UTF-8")
    public String authorizedUsers(
            @ModelAttribute ("queueSupervisorForm")
            QueueSupervisorForm queueSupervisorForm,

            @ModelAttribute ("queueSupervisorActionForm")
            QueueSupervisorActionForm queueSupervisorActionForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(queueUser.getQueueUserId());
        queueSupervisorForm.setQueueName(businessUser.getBizName().getBusinessName());
        queueSupervisorForm.setQueueSupervisors(businessUserStoreService.getAuthorizedUsersForBusiness(businessUser.getBizName().getId()));

        return authorizedUsersPage;
    }
}
