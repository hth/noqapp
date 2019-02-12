package com.noqapp.view.controller.admin;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.admin.SendNotificationForm;
import com.noqapp.view.validator.SendNotificationValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * hitender
 * 2019-02-11 17:03
 */
@Controller
@RequestMapping(value = "/admin/notification")
public class NotificationController {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationController.class);

    private String nextPage;

    private SendNotificationValidator sendNotificationValidator;
    private TokenQueueService tokenQueueService;
    private UserProfileManager userProfileManager;

    @Autowired
    public NotificationController(
        @Value("${nextPage:/admin/notification}")
        String nextPage,

        SendNotificationValidator sendNotificationValidator,
        TokenQueueService tokenQueueService,
        UserProfileManager userProfileManager
    ) {
        this.nextPage = nextPage;

        this.sendNotificationValidator = sendNotificationValidator;
        this.tokenQueueService = tokenQueueService;
        this.userProfileManager = userProfileManager;
    }

    /** Gymnastic for PRG. */
    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String notificationLanding(
        @ModelAttribute("sendNotificationForm")
        SendNotificationForm sendNotificationForm,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Sending Global notification landed qid={}", queueUser.getQueueUserId());

        if (model.asMap().containsKey("result")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.sendNotificationForm",
                model.asMap().get("result"));
        }

        return nextPage;
    }

    @PostMapping(value = "/landing", params = {"send-notification"}, produces = "text/html;charset=UTF-8")
    public String sendNotification(
        @ModelAttribute("sendNotificationForm")
        SendNotificationForm sendNotificationForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        sendNotificationValidator.validate(sendNotificationForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            return "redirect:" + "/admin/notification/landing" + ".htm";
        }

        List<UserProfileEntity> userProfiles = userProfileManager.findAllPhoneOwners();
        int sentCount = 0;
        for (UserProfileEntity userProfile : userProfiles) {
            if (userProfile.getQueueUserId().equalsIgnoreCase("100000000095")) {
                tokenQueueService.sendMessageToSpecificUser(
                    sendNotificationForm.getTitle().getText(),
                    sendNotificationForm.getBody().getText(),
                    userProfile.getQueueUserId(),
                    MessageOriginEnum.D);

                sentCount++;
            }
        }
        sendNotificationForm.setSentCount(sentCount).setSuccess(true);
        redirectAttrs.addFlashAttribute("sendNotificationForm", sendNotificationForm);
        return "redirect:" + "/admin/notification/landing" + ".htm";
    }

    @PostMapping(value = "/landing", params = {"cancel-send-notification"})
    public String postPreferredBusinessCancel() {
        LOG.info("Loading admin landing after user search cancelled");
        return "redirect:/admin/landing.htm";
    }

}
