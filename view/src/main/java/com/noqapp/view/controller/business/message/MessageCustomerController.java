package com.noqapp.view.controller.business.message;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.MessageCustomerService;
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
 * Sends bulk message to all the customers who have visited the business at least once.
 * hitender
 * 6/11/20 11:26 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/message/customer")
public class MessageCustomerController {
    private static final Logger LOG = LoggerFactory.getLogger(MessageCustomerController.class);

    private String nextPage;

    private SendNotificationValidator sendNotificationValidator;
    private MessageCustomerService messageCustomerService;
    private BusinessUserService businessUserService;

    @Autowired
    public MessageCustomerController(
        @Value("${nextPage:/business/message/customer}")
        String nextPage,

        SendNotificationValidator sendNotificationValidator,
        MessageCustomerService messageCustomerService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;

        this.sendNotificationValidator = sendNotificationValidator;
        this.messageCustomerService = messageCustomerService;
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
        LOG.info("Sending message to your user landed qid={}", queueUser.getQueueUserId());

        if (model.asMap().containsKey("result")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.sendNotificationForm",
                model.asMap().get("result"));
        }
        sendNotificationForm.setBusinessName(businessUser.getBizName().getBusinessName());
        sendNotificationForm.setSentCount(messageCustomerService.sendMessageToPastClients(businessUser.getBizName().getId()));
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
        LOG.info("Sending bulk message qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        sendNotificationValidator.validate(sendNotificationForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            return "redirect:" + "/business/message/customer";
        }

        try {
            int sendMessageCount = messageCustomerService.sendMessageToPastClients(
                sendNotificationForm.getTitle().getText(),
                CommonUtil.appendBusinessNameToNotificationMessage(sendNotificationForm.getBody().getText(), businessUser.getBizName().getBusinessName()),
                businessUser.getBizName().getId(),
                queueUser.getQueueUserId()
            );

            sendNotificationForm
                .setSentCount(sendMessageCount)
                .setSuccess(true)
                .setIgnoreSentiments(false);
            redirectAttrs.addFlashAttribute("sendNotificationForm", sendNotificationForm);
            LOG.info("Sent bulk message {} {} {}",
                sendMessageCount,
                sendNotificationForm.getTitle(),
                sendNotificationForm.getBody());
        } catch (Exception e) {
            LOG.error("Failed sending message reason={}", e.getLocalizedMessage(), e);
        }
        return "redirect:" + "/business/message/customer";
    }

    @PostMapping(params = {"cancel-send-notification"}, produces = "text/html;charset=UTF-8")
    public String sendNotification(
        @ModelAttribute("sendNotificationForm")
        SendNotificationForm sendNotificationForm
    ) {
        LOG.info("Loading main landing after user has either cancelled or sent notification successfully");
        return "redirect:/";
    }
}
