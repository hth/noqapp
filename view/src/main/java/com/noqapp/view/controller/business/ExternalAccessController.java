package com.noqapp.view.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.ExternalAccessEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.ExternalAccessService;
import com.noqapp.view.form.business.ExternalAccessForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * Manages access
 * hitender
 * 2/3/18 10:36 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/external/access")
public class ExternalAccessController {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalAccessController.class);

    private String nextPage;

    private ExternalAccessService externalAccessService;
    private BusinessUserService businessUserService;

    @Autowired
    public ExternalAccessController(
        @Value("${nextPage:/business/externalAccess}")
        String nextPage,

        ExternalAccessService externalAccessService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;

        this.externalAccessService = externalAccessService;
        this.businessUserService = businessUserService;
    }

    @GetMapping
    public String landing(
        @ModelAttribute("externalAccessForm")
        ExternalAccessForm externalAccessForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on external access page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        BusinessUserEntity businessUser = businessUserService.findByQid(queueUser.getQueueUserId());
        if (null == businessUser) {
            LOG.warn("Could not find businessUser={}", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }

        if (UserLevelEnum.M_ADMIN != businessUser.getUserLevel()) {
            LOG.warn("Could not find businessUser={}", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Could not find");
            return null;
        }

        List<ExternalAccessEntity> externalAccesses = externalAccessService.findAll(businessUser.getBizName().getId());
        externalAccessForm.setExternalAccesses(externalAccesses);
        return nextPage;
    }

    /**
     * Approve or reject new supervisor.
     */
    @PostMapping(value = "/actionExternalAccess")
    public String actionQueueSupervisor(
        @ModelAttribute ("externalAccessForm")
        ExternalAccessForm externalAccessForm,

        HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            BusinessUserEntity businessUser = businessUserService.findByQid(queueUser.getQueueUserId());
            if (null == businessUser) {
                LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
                response.sendError(SC_NOT_FOUND, "Could not find");
                return null;
            }

            switch (externalAccessForm.getAction().getText()) {
                case "APPROVE":
                    externalAccessService.grantPermission(externalAccessForm.decodeId(), businessUser.getQueueUserId());
                    break;
                case "REJECT":
                case "REMOVE":
                    externalAccessService.revokePermission(externalAccessForm.decodeId());
                    break;
                default:
                    LOG.warn("Reached un-reachable condition {}", externalAccessForm.getAction());
                    throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
            }

            return "redirect:/business/external/access.htm";
        } catch (Exception e) {
            LOG.error("Failed updated status for externalAccessId={} status={} reason={}",
                externalAccessForm.decodeId(),
                externalAccessForm.getAction().getText(),
                e.getLocalizedMessage(),
                e);

            return "redirect:/business/external/access.htm";
        }
    }
}
