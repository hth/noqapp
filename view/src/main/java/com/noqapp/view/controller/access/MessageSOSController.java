package com.noqapp.view.controller.access;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.UserProfilePreferenceService;
import com.noqapp.view.form.AddPrimaryContactMessageSOSForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 5/24/21 10:05 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/access/sos")
public class MessageSOSController {
    private static final Logger LOG = LoggerFactory.getLogger(MessageSOSController.class);

    private String nextPage;
    private String addPrimaryContactMessageSOSFlowActions;

    private UserProfilePreferenceService userProfilePreferenceService;
    private ApiHealthService apiHealthService;

    @Autowired
    public MessageSOSController(
        @Value ("${nextPage:/access/sos}")
        String nextPage,

        @Value("${addPrimaryContactMessageSOSFlowActions:redirect:/access/sos/add-primaryContact}")
        String addPrimaryContactMessageSOSFlowActions,

        UserProfilePreferenceService userProfilePreferenceService,
        ApiHealthService apiHealthService
    ) {
        this.nextPage = nextPage;
        this.addPrimaryContactMessageSOSFlowActions = addPrimaryContactMessageSOSFlowActions;

        this.userProfilePreferenceService = userProfilePreferenceService;
        this.apiHealthService = apiHealthService;
    }

    @GetMapping
    public String landing(
        @ModelAttribute("addPrimaryContactMessageSOSForm")
        AddPrimaryContactMessageSOSForm addPrimaryContactMessageSOSForm,

        ModelMap modelMap
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserProfileEntity> userProfiles = userProfilePreferenceService.findSOSUsers(queueUser.getQueueUserId());
        modelMap.addAttribute("userProfiles", userProfiles);

        apiHealthService.insert(
            "/landing",
            "landing",
            MessageSOSController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return nextPage;
    }

    @GetMapping(value = "/add")
    public String add() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Requested to add primaryContact {}", queueUser.getQueueUserId());
        return addPrimaryContactMessageSOSFlowActions;
    }

    @PostMapping(value = "/delete")
    public String delete(
        @ModelAttribute("addPrimaryContactMessageSOSForm")
        AddPrimaryContactMessageSOSForm addPrimaryContactMessageSOSForm,

        HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Requested to delete primaryContact {} by qid={}", addPrimaryContactMessageSOSForm.getPhoneNumber(), queueUser.getQueueUserId());

        userProfilePreferenceService.removeSOSUser(addPrimaryContactMessageSOSForm.getPhoneNumber(), queueUser.getQueueUserId());
        return "redirect:/access/sos";
    }
}
