package com.noqapp.view.controller.business.store;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.PublishJobEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.PublishJobService;
import com.noqapp.view.form.PublishJobForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 12/28/20 12:54 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/store/publishJob")
public class PublishJobController {
    private static final Logger LOG = LoggerFactory.getLogger(PublishJobController.class);

    private String nextPage;
    private String publishJobFlow;

    private PublishJobService publishJobService;
    private BusinessUserService businessUserService;

    @Autowired
    public PublishJobController(
        @Value("${nextPage:/business/job/landing}")
        String nextPage,

        @Value("${publishJobFlow:redirect:/store/publishJob.htm}")
        String publishJobFlow,

        PublishJobService publishJobService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.publishJobFlow = publishJobFlow;

        this.publishJobService = publishJobService;
        this.businessUserService = businessUserService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("publishJobForm")
        PublishJobForm publishJobForm,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to publish article {}", queueUser.getQueueUserId());
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        List<PublishJobEntity> publishJobs = publishJobService.findAll(businessUser.getBizName().getId());

        List<PublishJobForm> publishJobForms = new LinkedList<>();
        for (PublishJobEntity publishJob : publishJobs) {
            publishJobForms.add(
                PublishJobForm.newInstance()
                    .setTitle(publishJob.getTitle())
                    .setPublishId(new ScrubbedInput(publishJob.getId()))
                    .setActive(publishJob.isActive())
                    .setPublishDate(publishJob.getPublishDate()));
        }
        model.addAttribute("publishJobForms", publishJobForms);
        return nextPage;
    }

    @GetMapping(value = "/newJob", produces = "text/html;charset=UTF-8")
    public String newArticle() {
        LOG.info("Landed to publish new job");
        return publishJobFlow;
    }

    @PostMapping(value = "/action")
    public String actionQueueSupervisor(
        @ModelAttribute("publishJobForm")
        PublishJobForm publishJobForm,

        RedirectAttributes redirectAttrs
    ) {
        LOG.info("Action on article={} action={}", publishJobForm.getPublishId(), publishJobForm.getAction());
        switch (publishJobForm.getAction().getText()) {
            case "EDIT":
                redirectAttrs.addFlashAttribute("publishId", publishJobForm.getPublishId().getText());
                break;
            case "OFFLINE":
                publishJobService.takeOffOrOnline(publishJobForm.getPublishId().getText(), false);
                break;
            case "ONLINE":
                publishJobService.takeOffOrOnline(publishJobForm.getPublishId().getText(), true);
                break;
            case "DELETE":
                PublishJobEntity publishJob = publishJobService.findOne(publishJobForm.getPublishId().getText());
                publishJob.markAsDeleted();
                publishJob.inActive();
                publishJobService.save(publishJob);
                break;
            default:
                LOG.warn("Reached un-reachable condition {}", publishJobForm.getAction());
                throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
        }

        String goToPage;
        switch (publishJobForm.getAction().getText()) {
            case "EDIT":
                goToPage = publishJobFlow;
                break;
            default:
                goToPage = "redirect:/business/store/publishJob/landing.htm";
        }
        return goToPage;
    }
}
