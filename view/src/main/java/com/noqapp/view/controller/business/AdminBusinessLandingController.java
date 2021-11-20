package com.noqapp.view.controller.business;

import static com.noqapp.domain.types.BusinessSupportEnum.OD;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.PreferredBusinessEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.analytic.BizDimensionEntity;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.helper.QueueDetail;
import com.noqapp.domain.helper.QueueSupervisor;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.CommonStatusEnum;
import com.noqapp.domain.types.InvocationByEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.PreferredBusinessService;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.service.ScheduleAppointmentService;
import com.noqapp.service.analytic.BizDimensionService;
import com.noqapp.service.transaction.BusinessModificationService;
import com.noqapp.view.form.QueueSupervisorActionForm;
import com.noqapp.view.form.business.BusinessLandingForm;
import com.noqapp.view.form.business.MigrateBusinessTypeForm;
import com.noqapp.view.form.business.PreferredBusinessForm;
import com.noqapp.view.form.business.QueueSupervisorForm;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 12/7/16 11:40 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business")
public class AdminBusinessLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminBusinessLandingController.class);

    private int queueLimit;
    private String nextPage;
    private String listQueueSupervisorPage;
    private String authorizedUsersPage;
    private String preferredBusinessPage;

    private String migrateBusinessRegistrationFlow;
    private String storeActionFlow;
    private String addQueueSupervisorFlow;
    private String queueUserDetailFlow;
    private String addDoctorFlow;
    private String addNewAgentFlow;
    private String editBusinessFlow;
    private String migrateBusinessTypePage;

    private BusinessUserService businessUserService;
    private BizDimensionService bizDimensionService;
    private BizService bizService;
    private BusinessUserStoreService businessUserStoreService;
    private AccountService accountService;
    private ProfessionalProfileService professionalProfileService;
    private PreferredBusinessService preferredBusinessService;
    private BusinessModificationService businessModificationService;
    private ScheduleAppointmentService scheduleAppointmentService;

    @Autowired
    public AdminBusinessLandingController(
        @Value("${BusinessUserStoreService.queue.limit}")
        int queueLimit,

        @Value("${nextPage:/business/landing}")
        String nextPage,

        @Value("${listQueueSupervisorPage:/business/listQueueSupervisor}")
        String listQueueSupervisorPage,

        @Value("${preferredBusinessPage:/business/preferredBusiness}")
        String preferredBusinessPage,

        @Value("${authorizedUsersPage:/business/authorizedUsers}")
        String authorizedUsersPage,

        @Value("${migrateBusinessRegistrationFlow:redirect:/migrate/business/registration}")
        String migrateBusinessRegistrationFlow,

        @Value("${storeActionFlow:redirect:/store/storeAction}")
        String storeActionFlow,

        @Value("${addQueueSupervisorFlow:redirect:/store/addQueueSupervisor}")
        String addQueueSupervisorFlow,

        @Value("${addDoctorFlow:redirect:/store/addDoctor}")
        String addDoctorFlow,

        @Value("${addNewAgentFlow:redirect:/store/addNewAgent}")
        String addNewAgentFlow,

        @Value("${queueUserDetailFlow:redirect:/store/authorizedQueueUserDetail}")
        String queueUserDetailFlow,

        @Value("${editBusinessFlow:redirect:/migrate/business/registration}")
        String editBusinessFlow,

        @Value("${migrateBusinessTypePage:/business/migrateBusinessType}")
        String migrateBusinessTypePage,

        BusinessUserService businessUserService,
        BizDimensionService bizDimensionService,
        BizService bizService,
        BusinessUserStoreService businessUserStoreService,
        AccountService accountService,
        ProfessionalProfileService professionalProfileService,
        PreferredBusinessService preferredBusinessService,
        BusinessModificationService businessModificationService,
        ScheduleAppointmentService scheduleAppointmentService
    ) {
        this.queueLimit = queueLimit;
        this.nextPage = nextPage;
        this.listQueueSupervisorPage = listQueueSupervisorPage;
        this.preferredBusinessPage = preferredBusinessPage;
        this.authorizedUsersPage = authorizedUsersPage;
        this.migrateBusinessTypePage = migrateBusinessTypePage;

        this.migrateBusinessRegistrationFlow = migrateBusinessRegistrationFlow;
        this.storeActionFlow = storeActionFlow;
        this.addQueueSupervisorFlow = addQueueSupervisorFlow;
        this.addDoctorFlow = addDoctorFlow;
        this.addNewAgentFlow = addNewAgentFlow;
        this.queueUserDetailFlow = queueUserDetailFlow;
        this.editBusinessFlow = editBusinessFlow;

        this.businessUserService = businessUserService;
        this.bizDimensionService = bizDimensionService;
        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
        this.accountService = accountService;
        this.professionalProfileService = professionalProfileService;
        this.preferredBusinessService = preferredBusinessService;
        this.businessModificationService = businessModificationService;
        this.scheduleAppointmentService = scheduleAppointmentService;
    }

    /**
     * Loading landing page for business.
     * Note: This link is mapped in web flow after business adds new store to existing business.
     */
    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("businessLandingForm")
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
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        return nextPage(businessUser, businessLandingForm);
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
                LOG.info("Migrate to business registration qid={} userLevel={}", businessUser.getQueueUserId(), businessUser.getUserLevel());
                return migrateBusinessRegistrationFlow;
            case C:
            case I:
                LOG.info("Migrate to business registration qid={} userLevel={}", businessUser.getQueueUserId(), businessUser.getUserLevel());
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

        businessLandingForm
            .setBusinessType(bizName.getBusinessType())
            .setBizCodeQR(bizName.getCodeQR())
            .setCategories(CommonHelper.getCategories(bizName.getBusinessType(), InvocationByEnum.BUSINESS));
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
     */
    @GetMapping(value = "/{storeId}/listQueueSupervisor", produces = "text/html;charset=UTF-8")
    public String listQueueSupervisor(
        @ModelAttribute("queueSupervisorForm")
        QueueSupervisorForm queueSupervisorForm,

        @ModelAttribute("queueSupervisorActionForm")
        QueueSupervisorActionForm queueSupervisorActionForm,

        @PathVariable("storeId")
        ScrubbedInput storeId,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("List QueueSupervisors for storeId={} qid={} userLevel={}", storeId.getText(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        queueSupervisorForm.setBizStoreId(bizStore.getId());
        queueSupervisorForm.setQueueName(bizStore.getDisplayName());
        queueSupervisorForm.setBusinessType(bizStore.getBusinessType());

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

    /**
     * Only admin can add store as of now. Plan is to add support for S_MANAGER to allow adding stores. S_MANAGER with
     * Business Type of DO, will not have this option either.
     * Still not supported till this date.
     */
    @GetMapping(value = "/addStore", produces = "text/html;charset=UTF-8")
    public String addStore(RedirectAttributes redirectAttrs, HttpServletResponse response) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Add store to business {} qid={} userLevel={}", storeActionFlow, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttrs.addFlashAttribute("storeFranchise", RegisterBusiness.StoreFranchise.OFF);
        return storeActionFlow;
    }

    /**
     * Only admin can add store as of now. Plan is to add support for S_MANAGER to allow adding stores. S_MANAGER with
     * Business Type of DO, will not have this option either.
     * Still not supported till this date.
     */
    @GetMapping(value = "/addFranchiseStore", produces = "text/html;charset=UTF-8")
    public String addFranchiseStore(RedirectAttributes redirectAttrs, HttpServletResponse response) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Add franchise store to business {} qid={} userLevel={}", storeActionFlow, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttrs.addFlashAttribute("storeFranchise", RegisterBusiness.StoreFranchise.ON);
        return storeActionFlow;
    }

    @GetMapping(value = "/{bizStoreId}/editStore", produces = "text/html;charset=UTF-8")
    public String editStore(
        @PathVariable("bizStoreId")
        ScrubbedInput bizStoreId,

        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Edit business store id={} qid={} userLevel={}", bizStoreId, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttrs.addFlashAttribute("bizStoreId", bizStoreId);
        return storeActionFlow;
    }

    @GetMapping(value = "/{bizStoreId}/addQueueSupervisor", produces = "text/html;charset=UTF-8")
    public String addQueueSupervisorFlow(
        @PathVariable("bizStoreId")
        ScrubbedInput bizStoreId,

        RedirectAttributes redirectAttributes,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Add queue supervisor to bizStoreId={} qid={} userLevel={} {}", bizStoreId.getText(), queueUser.getQueueUserId(), queueUser.getUserLevel(), addQueueSupervisorFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttributes.addFlashAttribute("bizStoreId", bizStoreId.getText());
        return addQueueSupervisorFlow;
    }

    @GetMapping(value = "/{bizStoreId}/addDoctor", produces = "text/html;charset=UTF-8")
    public String addDoctor(
        @PathVariable("bizStoreId")
        ScrubbedInput bizStoreId,

        RedirectAttributes redirectAttributes,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Add doctor to bizStoreId={} qid={} userLevel={} {}", bizStoreId.getText(), queueUser.getQueueUserId(), queueUser.getUserLevel(), addQueueSupervisorFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttributes.addFlashAttribute("bizStoreId", bizStoreId.getText());
        return addDoctorFlow;
    }

    /** Authorized Users to New Store or Queue. */
    @GetMapping(value = "/queueUserDetail/{businessUserId}", produces = "text/html;charset=UTF-8")
    public String queueUserDetail(
        @PathVariable("businessUserId")
        ScrubbedInput businessUserId,

        RedirectAttributes redirectAttributes,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("QueueUserDetail businessUserId={} {} {} {}", businessUserId.getText(), queueUser.getQueueUserId(), queueUser.getUserLevel(), queueUserDetailFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttributes.addFlashAttribute("businessUserId", businessUserId.getText());
        return queueUserDetailFlow;
    }

    @GetMapping(value = "/queueUserProfile/{businessUserId}", produces = "text/html;charset=UTF-8")
    public String queueUserProfile(
        @PathVariable("businessUserId")
        ScrubbedInput businessUserId,

        RedirectAttributes redirectAttributes,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("QueueUserDetail businessUserId={} {} {} {}",
            businessUserId.getText(), queueUser.getQueueUserId(), queueUser.getUserLevel(), queueUserDetailFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttributes.addFlashAttribute("businessUserId", businessUserId.getText());
        return "redirect:/access/userProfile/show";
    }

    /**
     * Approve or reject new supervisor. If approving a doctor then the role is set of a manager as default.
     * Each queue will only have one manager. If a doctor is removed from a queue, will not lose its role
     * as a manager. Once a doctor is a manager then role is set as manager for life.
     */
    @PostMapping(value = "/actionQueueSupervisor")
    public String actionQueueSupervisor(
        @ModelAttribute("queueSupervisorActionForm")
        QueueSupervisorActionForm queueSupervisorActionForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
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
            BizStoreEntity bizStore;
            String qid;
            switch (queueSupervisorActionForm.getAction().getText()) {
                case "APPROVE":
                    businessUserStoreService.approve(queueSupervisorActionForm.getBizStoreId().getText(), queueUser.getQueueUserId(), businessUser);
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
                        return "redirect:/business/" + queueSupervisorActionForm.getBizStoreId().getText() + "/listQueueSupervisor";
                    }

                    bizStore = bizService.getByStoreId(queueSupervisorActionForm.getBizStoreId().getText());
                    businessUserStoreService.addToBusinessUserStore(
                        businessUser.getQueueUserId(),
                        bizStore,
                        businessUser.getBusinessUserRegistrationStatus(),
                        userProfile.getLevel());

                    if (UserLevelEnum.S_MANAGER == userProfile.getLevel()) {
                        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQidAndRemoveAnySoftDelete(businessUser.getQueueUserId());
                        if (null != professionalProfile) {
                            bizStore = bizService.getByStoreId(queueSupervisorActionForm.getBizStoreId().getText());
                            professionalProfile.addManagerAtStoreCodeQR(bizStore.getCodeQR());
                            professionalProfileService.save(professionalProfile);
                        }
                    }

                    /* Clean cache. */
                    businessUserStoreService.evictFromCache(businessUser.getQueueUserId(), bizStore.getCodeQR());
                    break;
                case "REJECT":
                case "DELETE":
                    qid = businessUser.getQueueUserId();

                    businessUserStoreService.removeFromBusiness(qid, businessUser.getBizName().getId());
                    businessUserService.deleteHard(businessUser);
                    professionalProfileService.softDeleteProfessionalProfileProfile(qid);

                    /*
                     * Downgrade ROLES for QID as it was set to Q_SUPERVISOR when approving.
                     * But after approved and when rejecting, not sure about role, hence downgrading all to CLIENT.
                     */
                    userProfile = accountService.findProfileByQueueUserId(qid);
                    switch (userProfile.getLevel()) {
                        case Q_SUPERVISOR:
                            if (accountService.isPhoneValidated(qid)) {
                                userProfile
                                    .setLevel(UserLevelEnum.CLIENT)
                                    .setBusinessType(null);
                            } else {
                                userProfile
                                    .setLevel(UserLevelEnum.Q_SUPERVISOR)
                                    .setBusinessType(null);
                            }
                            break;
                        case M_ADMIN:
                            userProfile
                                .setLevel(UserLevelEnum.CLIENT)
                                .setBusinessType(null);
                            break;
                        case S_MANAGER:
                            if (accountService.isPhoneValidated(qid)) {
                                ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(businessUser.getQueueUserId());
                                if (null == professionalProfile) {
                                    userProfile
                                        .setLevel(UserLevelEnum.CLIENT)
                                        .setBusinessType(null);
                                } else {
                                    //TODO(hth) currently removes all the code QR, it should only remove the specific code qr of the businesses.
                                    professionalProfile.setManagerAtStoreCodeQRs(new HashSet<>());
                                    professionalProfileService.save(professionalProfile);
                                }
                            } else {
                                userProfile
                                    .setLevel(UserLevelEnum.Q_SUPERVISOR)
                                    .setBusinessType(null);
                            }
                            break;
                        default:
                            /*
                             * Could be session not expired, and user is still logged in
                             * and access secured page with previous role.
                             */
                            LOG.error("Reached unsupported condition userLevel={} mail={}", userProfile.getLevel(), userProfile.getEmail());
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

                    bizStore = bizService.getByStoreId(queueSupervisorActionForm.getBizStoreId().getText());
                    businessUserStoreService.evictFromCache(businessUser.getQueueUserId(), bizStore.getCodeQR());
                    if (UserLevelEnum.S_MANAGER == accountService.findProfileByQueueUserId(businessUser.getQueueUserId()).getLevel()) {
                        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(businessUser.getQueueUserId());
                        if (null != professionalProfile) {
                            professionalProfile.removeManagerAtStoreCodeQR(bizStore.getCodeQR());
                            professionalProfileService.save(professionalProfile);
                        }
                    }
                    break;
                default:
                    LOG.warn("Reached un-reachable condition {}", queueSupervisorActionForm.getAction());
                    throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
            }

            String goToPage;
            switch (queueSupervisorActionForm.getAction().getText()) {
                case "DELETE":
                    goToPage = "redirect:/business/authorizedUsers";
                    break;
                default:
                    goToPage = "redirect:/business/" + queueSupervisorActionForm.getBizStoreId().getText() + "/listQueueSupervisor";
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
                    goToPage = "redirect:/business/authorizedUsers";
                    break;
                default:
                    goToPage = "redirect:/business/" + queueSupervisorActionForm.getBizStoreId().getText() + "/listQueueSupervisor";
            }
            return goToPage;
        }
    }

    /** List all users with role of Queue Supervisor and Manager managing queues for business. */
    @GetMapping(value = "/authorizedUsers", produces = "text/html;charset=UTF-8")
    public String authorizedUsers(
        @ModelAttribute("queueSupervisorForm")
        QueueSupervisorForm queueSupervisorForm,

        @ModelAttribute("queueSupervisorActionForm")
        QueueSupervisorActionForm queueSupervisorActionForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("List authorizedUser for {} qid={} {}",
            businessUser.getBizName().getBusinessName(),
            queueUser.getQueueUserId(),
            queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        queueSupervisorForm.setQueueName(businessUser.getBizName().getBusinessName());
        queueSupervisorForm.setQueueSupervisors(businessUserStoreService.getAuthorizedUsersForBusiness(businessUser.getBizName().getId()));
        return authorizedUsersPage;
    }

    /** Add new agent. */
    @GetMapping(value = "/{bizStoreId}/addNewAgent", produces = "text/html;charset=UTF-8")
    public String addNewAgent(
        @PathVariable("bizStoreId")
        ScrubbedInput bizStoreId,

        RedirectAttributes redirectAttributes,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Add new agent to business {} qid={} userLevel={}", addNewAgentFlow, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttributes.addFlashAttribute("bizStoreId", bizStoreId.getText());
        return addNewAgentFlow;
    }

    /** Note: As of now, there is no support for same QID being used between different businesses. Level change is across the board. */
    @PostMapping(
        value = "/changeLevel",
        headers = "Accept=application/json",
        produces = "application/json")
    @ResponseBody
    public String changeLevel(
        @RequestParam("id")
        ScrubbedInput businessUserId,

        @RequestParam("userLevel")
        ScrubbedInput userLevel,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return String.format("{ \"id\" : \"%s\", \"action\" : \"%s\" }", businessUserId.getText(), CommonStatusEnum.FAILURE.name());
        }
        LOG.info("Change Level bizId={} qid={} userLevel={} {}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel(), addQueueSupervisorFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        try {
            BusinessUserEntity businessUserOfId = businessUserService.findById(businessUserId.getText());
            long change = businessUserStoreService.changeUserLevel(
                businessUserOfId.getQueueUserId(),
                UserLevelEnum.valueOf(userLevel.getText()),
                businessUserOfId.getBizName().getBusinessType());
            String text;
            if (-1 == change) {
                text = "Failed. User role is already set to " + UserLevelEnum.valueOf(userLevel.getText()).getDescription();
                LOG.info("Failed changing to same userLevel for qid={} to userLevel={}", businessUserOfId.getQueueUserId(), userLevel.getText());
                return String.format("{ \"id\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}",
                    businessUserId.getText(), CommonStatusEnum.FAILURE.name(), text);
            } else if (2 <= change) {
                text = "Successfully changed user role to " + UserLevelEnum.valueOf(userLevel.getText()).getDescription();
                LOG.info("Changed userLevel successfully for qid={} to userLevel={}",
                    businessUserOfId.getQueueUserId(), userLevel.getText());
                return String.format("{ \"id\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}",
                    businessUserId.getText(), CommonStatusEnum.SUCCESS.name(), text);
            } else {
                text = "Failed changing user role to " + UserLevelEnum.valueOf(userLevel.getText()).getDescription();
                LOG.error("Failed changing userLevel for qid={} to userLevel={}", businessUserOfId.getQueueUserId(), userLevel.getText());
                return String.format("{ \"id\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}",
                    businessUserId.getText(), CommonStatusEnum.FAILURE.name(), text);
            }
        } catch (Exception e) {
            LOG.error("Failed changing userLevel reason={}", e.getLocalizedMessage(), e);
            return String.format("{ \"id\" : \"%s\", \"action\" : \"%s\" }", businessUserId.getText(), CommonStatusEnum.FAILURE.name());
        }
    }

    /**
     * Edit existing business.
     */
    @GetMapping(value = "/editBusiness", produces = "text/html;charset=UTF-8")
    public String editBusiness(
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Edit business bizId={} qid={} userLevel={} {}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel(), addQueueSupervisorFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        redirectAttrs.addFlashAttribute("bizNameId", businessUser.getBizName().getId());
        return editBusinessFlow;
    }

    /** For adding preferredBusiness. */
    @GetMapping(value = "/preferredBusiness")
    public String getPreferredBusiness(
        @ModelAttribute("preferredBusinessForm")
        PreferredBusinessForm preferredBusinessForm,

        Model model,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Get preferred business bizId={} qid={} userLevel={} {}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel(), addQueueSupervisorFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        List<PreferredBusinessEntity> preferredBusinesses = preferredBusinessService.findAll(businessUser.getBizName().getId());
        for (PreferredBusinessEntity preferredBusiness : preferredBusinesses) {
            BizNameEntity bizName = bizService.getByBizNameId(preferredBusiness.getPreferredBizNameId());
            preferredBusiness.setPreferredBusinessName(bizName.getBusinessName());
            preferredBusinessForm.addPreferredBusiness(preferredBusiness);
        }

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.preferredBusinessForm", model.asMap().get("result"));
            preferredBusinessForm.setBusinessNameToAdd((ScrubbedInput) model.asMap().get("businessNameToAdd"));
        } else {
            redirectAttrs.addFlashAttribute("preferredBusinessForm", preferredBusinessForm);
        }

        return preferredBusinessPage;
    }

    /** For adding preferredBusiness. */
    @PostMapping(value = "/preferredBusiness", params = {"add"})
    public String postPreferredBusinessAdd(
        @ModelAttribute("preferredBusinessForm")
        PreferredBusinessForm preferredBusinessForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Add preferred business bizId={} qid={} userLevel={} {}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel(), addQueueSupervisorFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        BizNameEntity bizName = bizService.findAllBizWithMatchingName(preferredBusinessForm.getBusinessNameToAdd().getText());
        if (null == bizName) {
            LOG.warn("No business with name={} exists", preferredBusinessForm.getBusinessNameToAdd().getText());
        } else {
            boolean success = preferredBusinessService.addPreferredBusiness(businessUser.getBizName().getId(), bizName);
            if (!success) {
                LOG.warn("Failed to add preferred business");
            }
        }

        LOG.info("Loading preferred business");
        return "redirect:" + preferredBusinessPage;
    }

    @PostMapping(value = "/preferredBusiness", params = {"cancel_Add"})
    public String postPreferredBusinessCancel() {
        LOG.info("Loading preferred business cancelled");
        return "redirect:/business/landing";
    }

    @PostMapping(value = "/preferredBusiness", params = {"delete"})
    public String postPreferredBusinessDelete(
        @ModelAttribute("preferredBusinessForm")
        PreferredBusinessForm preferredBusinessForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Delete preferred business bizId={} qid={} userLevel={} {}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel(), addQueueSupervisorFlow);
        /* Above condition to make sure users with right roles and access gets access. */

        preferredBusinessService.deleteById(preferredBusinessForm.getRecordId());
        return "redirect:" + preferredBusinessPage;
    }

    @GetMapping(value = "/migrateBusinessType")
    public String migrateBusinessType(
        @ModelAttribute("migrateBusinessTypeForm")
        MigrateBusinessTypeForm migrateBusinessTypeForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Migrate business type bizId={} qid={} userLevel={}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        migrateBusinessTypeForm
            .setExistingBusinessType(businessUser.getBizName().getBusinessType())
            .setAllowedMigrationBusinessType(supportedMigration(businessUser.getBizName().getBusinessType()));

        return migrateBusinessTypePage;
    }

    @PostMapping(value = "/migrateBusinessType", params = {"migrate"})
    public String initiateMigrationToBusinessType(
        @ModelAttribute("migrateBusinessTypeForm")
        MigrateBusinessTypeForm migrateBusinessTypeForm,

        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Migrate business type bizId={} qid={} userLevel={}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        if (!businessModificationService.isQueueStatusAtStart(businessUser.getBizName().getId())) {
            LOG.warn("Cannot migrate when business is still running. Should be set to {}", QueueStatusEnum.S);

            migrateBusinessTypeForm
                .setMigrationSuccess(false)
                .setMigrationMessage("Cannot migrate during business hour. Best to migrate before start of the day.");
            redirectAttrs.addFlashAttribute("migrateBusinessTypeForm", migrateBusinessTypeForm);
            return "redirect:/business/migrateBusinessType";
        }

        if (migrateBusinessTypeForm.isMigrate()) {
            businessModificationService.changeBizNameBusinessType(
                businessUser.getBizName().getId(),
                migrateBusinessTypeForm.getExistingBusinessType(),
                migrateBusinessTypeForm.getAllowedMigrationBusinessType());

            if (OD == migrateBusinessTypeForm.getAllowedMigrationBusinessType().getBusinessSupport()) {
                /* Cancel all pending appointment when changing business type. */
                scheduleAppointmentService.findAllUpComingAppointmentsByBizName(businessUser.getBizName().getId());
            }

            migrateBusinessTypeForm
                .setMigrationSuccess(true)
                .setMigrationMessage("Migration successful.");
            migrateBusinessTypeForm.setExistingBusinessType(migrateBusinessTypeForm.getAllowedMigrationBusinessType());
        }

        redirectAttrs.addFlashAttribute("migrateBusinessTypeForm", migrateBusinessTypeForm);
        return "redirect:/business/migrateBusinessType";
    }

    @PostMapping(value = "/migrateBusinessType", params = {"cancel_Migrate"})
    public String cancelMigrationToBusinessType() {
        LOG.info("Loading business landing after user business migration cancelled");
        return "redirect:/business/landing";
    }

    private static BusinessTypeEnum supportedMigration(BusinessTypeEnum businessType) {
        switch (businessType) {
            case RS:
                return BusinessTypeEnum.RSQ;
            case RSQ:
                return BusinessTypeEnum.RS;
            case FT:
                return BusinessTypeEnum.FTQ;
            case FTQ:
                return BusinessTypeEnum.FT;
            case BA:
                return BusinessTypeEnum.BAQ;
            case BAQ:
                return BusinessTypeEnum.BA;
            case ST:
                return BusinessTypeEnum.STQ;
            case STQ:
                return BusinessTypeEnum.ST;
            case GS:
                return BusinessTypeEnum.GSQ;
            case GSQ:
                return BusinessTypeEnum.GS;
            case CF:
                return BusinessTypeEnum.CFQ;
            case CFQ:
                return BusinessTypeEnum.CF;
            case CD:
                return BusinessTypeEnum.CDQ;
            case CDQ:
                return BusinessTypeEnum.CD;
            default:
                LOG.warn("No support to migrate this business type {}", businessType);
                return null;
        }
    }
}
