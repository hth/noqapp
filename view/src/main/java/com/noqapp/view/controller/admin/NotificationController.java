package com.noqapp.view.controller.admin;

import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.admin.SendNotificationForm;
import com.noqapp.view.validator.SendNotificationValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * hitender
 * 2019-02-11 17:03
 */
@Controller
@RequestMapping(value = "/admin/notification")
public class NotificationController {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationController.class);

    private String nextPage;

    private Environment environment;
    private SendNotificationValidator sendNotificationValidator;
    private TokenQueueService tokenQueueService;
    private UserProfileManager userProfileManager;
    private RegisteredDeviceManager registeredDeviceManager;

    @Autowired
    public NotificationController(
        @Value("${nextPage:/admin/notification}")
        String nextPage,

        Environment environment,
        SendNotificationValidator sendNotificationValidator,
        TokenQueueService tokenQueueService,
        UserProfileManager userProfileManager,
        RegisteredDeviceManager registeredDeviceManager
    ) {
        this.nextPage = nextPage;

        this.environment = environment;
        this.sendNotificationValidator = sendNotificationValidator;
        this.tokenQueueService = tokenQueueService;
        this.userProfileManager = userProfileManager;
        this.registeredDeviceManager = registeredDeviceManager;
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

        try {
            AtomicInteger sentCount = new AtomicInteger();
            try (Stream<UserProfileEntity> stream = userProfileManager.findAllPhoneOwners()) {
                stream.iterator().forEachRemaining(userProfile -> {
                    if (environment.getProperty("build.env").equalsIgnoreCase("prod")) {
                        tokenQueueService.sendMessageToSpecificUser(
                            sendNotificationForm.getTitle().getText(),
                            sendNotificationForm.getBody().getText(),
                            sendNotificationForm.getImageURL().getText(),
                            userProfile.getQueueUserId(),
                            MessageOriginEnum.D);

                        sentCount.getAndIncrement();
                    } else {
                        if (userProfile.getQueueUserId().equalsIgnoreCase("100000000095")) {
                            tokenQueueService.sendMessageToSpecificUser(
                                sendNotificationForm.getTitle().getText(),
                                sendNotificationForm.getBody().getText(),
                                sendNotificationForm.getImageURL().getText(),
                                userProfile.getQueueUserId(),
                                MessageOriginEnum.D);

                            sentCount.getAndIncrement();
                        }
                    }
                });
            }

            try (Stream<RegisteredDeviceEntity> stream = registeredDeviceManager.findAllTokenWithoutQID(null)) {
                stream.iterator().forEachRemaining(registeredDevice -> {
                    if (environment.getProperty("build.env").equalsIgnoreCase("prod")) {
                        tokenQueueService.sendMessageToSpecificUser(
                            sendNotificationForm.getTitle().getText(),
                            sendNotificationForm.getBody().getText(),
                            sendNotificationForm.getImageURL().getText(),
                            registeredDevice,
                            MessageOriginEnum.D);

                        sentCount.getAndIncrement();
                    }
                });
            }

            sendNotificationForm
                .setSentCount(sentCount.get())
                .setSuccess(true)
                .setIgnoreSentiments(false);
            redirectAttrs.addFlashAttribute("sendNotificationForm", sendNotificationForm);
            LOG.info("Sent global notification {} {} {}", sentCount.get(), sendNotificationForm.getTitle(), sendNotificationForm.getBody());
        } catch (Exception e) {
            LOG.error("Failed sending message reason={}", e.getLocalizedMessage(), e);
        }
        return "redirect:" + "/admin/notification/landing" + ".htm";
    }

    @PostMapping(value = "/landing", params = {"cancel-send-notification"})
    public String postPreferredBusinessCancel() {
        LOG.info("Loading admin landing after user search cancelled");
        return "redirect:/admin/landing.htm";
    }
}
