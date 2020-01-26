package com.noqapp.view.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.DataVisibilityEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.form.business.DataVisibilityForm;

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
@RequestMapping(value = "/business/dataVisibility")
public class DataVisibilityController {
    private static final Logger LOG = LoggerFactory.getLogger(DataVisibilityController.class);

    private String nextPage;

    private BizService bizService;
    private BusinessUserService businessUserService;

    @Autowired
    public DataVisibilityController(
        @Value("${nextPage:/business/dataVisibility}")
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
        @ModelAttribute("dataVisibilityForm")
        DataVisibilityForm dataVisibilityForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on data visibility page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizNameEntity bizName = businessUser.getBizName();
        Map<String, DataVisibilityEnum>  maps = bizName.getDataVisibilities();
        dataVisibilityForm
            .setDataVisibilityForSupervisor(maps.get(UserLevelEnum.Q_SUPERVISOR.name()))
            .setDataVisibilityForManager(maps.get(UserLevelEnum.S_MANAGER.name()))
            .setDataVisibilities(DataVisibilityEnum.asMapWithNameAsKey());
        return nextPage;
    }

    @PostMapping(value = "/landing",  params = {"update-dataVisibility"}, produces = "text/html;charset=UTF-8")
    public String update(
        @ModelAttribute("dataVisibilityForm")
        DataVisibilityForm dataVisibilityForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Update data visibility qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizNameEntity bizName = businessUser.getBizName();
        Map<String, DataVisibilityEnum> map = new HashMap<>();
        map.put(UserLevelEnum.Q_SUPERVISOR.name(), dataVisibilityForm.getDataVisibilityForSupervisor());
        map.put(UserLevelEnum.S_MANAGER.name(), dataVisibilityForm.getDataVisibilityForManager());
        bizService.updateDataVisibility(map, bizName.getId());

        return "redirect:" + "/business/dataVisibility/landing" + ".htm";
    }

    @PostMapping(value = "/landing", params = {"cancel-dataVisibility"})
    public String postPreferredBusinessCancel() {
        LOG.info("Loading admin landing after user search cancelled");
        return "redirect:/business/landing.htm";
    }
}
