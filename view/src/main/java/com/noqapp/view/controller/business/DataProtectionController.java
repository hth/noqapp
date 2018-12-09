package com.noqapp.view.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.DataProtectionEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.form.business.DataProtectionForm;

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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 2018-12-09 08:56
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/dataProtection")
public class DataProtectionController {
    private static final Logger LOG = LoggerFactory.getLogger(DataProtectionController.class);

    private String nextPage;

    private BizService bizService;
    private BusinessUserService businessUserService;

    @Autowired
    public DataProtectionController(
        @Value("${nextPage:/business/dataProtection}")
        String nextPage,

        BizService bizService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;

        this.bizService = bizService;
        this.businessUserService = businessUserService;
    }

    /**
     * Gymnastic for PRG.
     */
    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("dataProtectionForm")
        DataProtectionForm dataProtectionForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizNameEntity bizName = businessUser.getBizName();
        Map<String, DataProtectionEnum>  maps = bizName.getDataProtections();
        dataProtectionForm
            .setDataProtectionForSupervisor(maps.get(UserLevelEnum.Q_SUPERVISOR.name()))
            .setDataProtectionForManager(maps.get(UserLevelEnum.S_MANAGER.name()))
            .setDataProtections(DataProtectionEnum.asMapWithNameAsKey());
        return nextPage;
    }

    @PostMapping(value = "/landing",  params = {"update-dataProtection"}, produces = "text/html;charset=UTF-8")
    public String update(
        @ModelAttribute("dataProtectionForm")
        DataProtectionForm dataProtectionForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizNameEntity bizName = businessUser.getBizName();
        Map<String, DataProtectionEnum> map = new HashMap<>();
        map.put(UserLevelEnum.Q_SUPERVISOR.name(), dataProtectionForm.getDataProtectionForSupervisor());
        map.put(UserLevelEnum.S_MANAGER.name(), dataProtectionForm.getDataProtectionForManager());
        bizService.updateDataProtection(map, bizName.getId());

        return "redirect:" + "/business/dataProtection/landing" + ".htm";
    }

    @PostMapping(value = "/landing", params = {"cancel-dataProtection"})
    public String postPreferredBusinessCancel() {
        LOG.info("Loading admin landing after user search cancelled");
        return "redirect:/business/landing.htm";
    }
}
