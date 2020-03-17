package com.noqapp.view.controller.admin;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.loader.scheduledtasks.GenerateStoreQueueHTML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * hitender
 * 3/17/20 10:13 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/admin/invokeCronTask")
public class InvokeCronTaskController {
    private static final Logger LOG = LoggerFactory.getLogger(InvokeCronTaskController.class);

    private String nextPage;

    private GenerateStoreQueueHTML generateStoreQueueHTML;

    @Autowired
    public InvokeCronTaskController(
        @Value("${nextPage:/admin/invokeCronTask}")
        String nextPage,

        GenerateStoreQueueHTML generateStoreQueueHTML
    ) {
        this.nextPage = nextPage;
        this.generateStoreQueueHTML = generateStoreQueueHTML;
    }

    @GetMapping
    public String invokeCronTask() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Admin landed qid={}", queueUser.getQueueUserId());
        return nextPage;
    }

    @PostMapping(value = "/generateStoreQueueHTML", produces = "text/html;charset=UTF-8")
    public String generateStoreQueueHTML() {
        LOG.info("Generate Store Queue HTML");
        generateStoreQueueHTML.generateHTMLPages();
        return "redirect:/admin/landing.htm";
    }
}
