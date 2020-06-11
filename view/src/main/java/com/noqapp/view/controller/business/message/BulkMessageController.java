package com.noqapp.view.controller.business.message;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BulkMessageService;
import com.noqapp.service.BusinessUserService;
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

/**
 * hitender
 * 6/11/20 11:26 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/message/bulk")
public class BulkMessageController {
    private static final Logger LOG = LoggerFactory.getLogger(BulkMessageController.class);

    private String nextPage;

    private SendNotificationValidator sendNotificationValidator;
    private BulkMessageService bulkMessageService;
    private BusinessUserService businessUserService;

    @Autowired
    public BulkMessageController(
        @Value("${nextPage:/business/message/bulk}")
        String nextPage,

        SendNotificationValidator sendNotificationValidator,
        BulkMessageService bulkMessageService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;

        this.sendNotificationValidator = sendNotificationValidator;
        this.bulkMessageService = bulkMessageService;
        this.businessUserService = businessUserService;
    }

    /** Gymnastic for PRG. */
    @GetMapping(produces = "text/html;charset=UTF-8")
    public String notificationLanding(
        @ModelAttribute("sendNotificationForm")
        SendNotificationForm sendNotificationForm,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Sending Global notification landed qid={}", queueUser.getQueueUserId());

        if (model.asMap().containsKey("result")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.sendNotificationForm",
                model.asMap().get("result"));
        }
        sendNotificationForm.setBusinessName(businessUser.getBizName().getBusinessName());
        sendNotificationForm.setSentCount(bulkMessageService.sendMessageToPastClients(businessUser.getBizName().getId()));
        return nextPage;
    }

    @PostMapping(params = {"send-notification"}, produces = "text/html;charset=UTF-8")
    public String sendNotification(
        @ModelAttribute("sendNotificationForm")
        SendNotificationForm sendNotificationForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Landed on bulk upload page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        sendNotificationValidator.validate(sendNotificationForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            return "redirect:" + "/business/message/bulk" + ".htm";
        }

        try {
            int sendMessageCount = bulkMessageService.sendMessageToPastClients(
                sendNotificationForm.getTitle().getText(),
                sendNotificationForm.getBody().getText(),
                queueUser.getQueueUserId(),
                businessUser.getBizName().getId()
            );

            sendNotificationForm
                .setSentCount(sendMessageCount)
                .setSuccess(true)
                .setIgnoreSentiments(false);
            redirectAttrs.addFlashAttribute("sendNotificationForm", sendNotificationForm);
            LOG.info("Sent message {} {} {}",
                sendMessageCount,
                sendNotificationForm.getTitle(),
                sendNotificationForm.getBody());
        } catch (Exception e) {
            LOG.error("Failed sending message reason={}", e.getLocalizedMessage(), e);
        }
        return "redirect:" + "/business/message/bulk" + ".htm";
    }

    @PostMapping(params = {"cancel-send-notification"}, produces = "text/html;charset=UTF-8")
    public String postPreferredBusinessCancel(
        @ModelAttribute("sendNotificationForm")
        SendNotificationForm sendNotificationForm
    ) {
        LOG.info("Loading admin landing after user search cancelled");
        return "redirect:/";
    }
}
